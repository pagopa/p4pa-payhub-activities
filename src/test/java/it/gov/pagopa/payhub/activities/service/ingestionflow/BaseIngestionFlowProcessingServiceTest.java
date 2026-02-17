package it.gov.pagopa.payhub.activities.service.ingestionflow;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.util.MemoryAppender;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@Slf4j
public abstract class BaseIngestionFlowProcessingServiceTest<C, R extends IngestionFlowFileResult, E extends ErrorFileDTO> {

    protected static final PodamFactory podamFactory = TestUtils.getPodamFactory();
    protected static final int MAX_CONCURRENT_PROCESSING_ROWS = 10;
    private static final int CONSUMER_ROW_METHOD_ARGUMENT_INDEX_LINE_NUMBER = 0;

    private final Class<C> rowDtoClass;
    private final boolean shouldRetrieveOrganization;

    @Mock
    protected Path workingDirectory;
    @Mock
    protected OrganizationService organizationServiceMock;

    protected IngestionFlowFile ingestionFlowFile;
    protected Organization organization;

    private MemoryAppender memoryAppender;

    protected BaseIngestionFlowProcessingServiceTest(boolean shouldRetrieveOrganization) {
        //noinspection unchecked
        this.rowDtoClass = (Class<C>) ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).toClass();
        this.shouldRetrieveOrganization = shouldRetrieveOrganization;
    }

    @BeforeEach
    void initSuper() {
        ingestionFlowFile = buildIngestionFlowFile();
        organization = podamFactory.manufacturePojo(Organization.class);
        organization.setOrganizationId(ingestionFlowFile.getOrganizationId());
        organization.setIpaCode("IPA_CODE");

        if (shouldRetrieveOrganization) {
            Mockito.when(organizationServiceMock.getOrganizationById(organization.getOrganizationId()))
                    .thenReturn(Optional.of(organization));
        }

        ((Logger) LoggerFactory.getLogger(PodamFactoryImpl.class)).setLevel(Level.ERROR);
        buildIngestionFlowProcessingServiceMemoryAppender();
    }

    private void buildIngestionFlowProcessingServiceMemoryAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(IngestionFlowProcessingService.class);
        this.memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterEach
    void verifyNoMoreInteractionsSuper() {
        Mockito.verifyNoMoreInteractions(organizationServiceMock);
    }

    protected abstract IngestionFlowProcessingService<C, R, E> getServiceSpy();

    protected abstract ErrorArchiverService<E> getErrorsArchiverServiceMock();

    protected abstract R startProcess(Iterator<C> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory);

    /**
     * It should build and configure a happy use case configuring sequencingId related mock invocations just once (or if the invocations are related to data changing anyway, as the entire dto).<BR />Don't use Mockito.when() construct, use instead Mockito.doReturn().when() instead
     */
    protected abstract C buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber);

    /**
     * Don't use Mockito.when() construct, use instead Mockito.doReturn().when() instead
     */
    protected abstract List<Pair<C, List<E>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber);

    /**
     * To override in order to test in case of extension to test.<BR />If the result is collecting data based on rows, the order cannot be guaranteed (just record having the same sequencingId would be ordered)
     */
    protected void assertIngestionFlowFileResultExtension(R result, List<C> happyUseCases) {
        // By default, no extension is expected
    }

    @Test
    void givenOrganizationNotFoundErrorWhenProcessThenThrowException() {
        if (shouldRetrieveOrganization) {
            // Given
            Iterator<C> rowIterator = Stream.<C>empty().iterator();
            List<CsvException> readerExceptions = List.of();

            Mockito.reset(organizationServiceMock);
            Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                    .thenReturn(Optional.empty());

            // When, Then (not handled because it cannot happen by construction
            Assertions.assertThrows(OrganizationNotFoundException.class, () -> startProcess(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory));
        }
    }

    @Test
    protected void givenReaderExceptionAndNoProcessedRowsWhenProcessThenWriteError() {
        // Given
        List<CsvException> readerExceptions = buildReaderExceptions();

        Mockito.when(getErrorsArchiverServiceMock().archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        R result = startProcess(Stream.<C>empty().iterator(), readerExceptions, ingestionFlowFile, workingDirectory);

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        assertEquals(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        assertEquals(ingestionFlowFile.getFileVersion(), result.getFileVersion());
        assertEquals(ingestionFlowFile.getOperatorExternalId(), result.getOperatorExternalUserId());

        Mockito.verify(getErrorsArchiverServiceMock()).writeErrors(workingDirectory, ingestionFlowFile,
                readerExceptions.stream()
                        .map(e -> buildCsvGenericErrorDto(ingestionFlowFile, e))
                        .toList()
        );
        Mockito.verify(getErrorsArchiverServiceMock()).archiveErrorFiles(workingDirectory, ingestionFlowFile);
    }

    protected List<CsvException> buildReaderExceptions() {
        return IntStream.rangeClosed(1, 2)
                .mapToObj(i -> {
                    CsvException csvException = new CsvException("DUMMYERROR" + i);
                    csvException.setLineNumber(-i);
                    return csvException;
                })
                .toList();
    }

    @Test
    void givenReaderExceptionAndProcessedRowsWhenProcessThenWriteError() {
        // Given
        List<CsvException> readerExceptions = buildReaderExceptions();

        Mockito.when(getErrorsArchiverServiceMock().archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        C happyUseCase = buildAndConfigureHappyUseCase(ingestionFlowFile, 1, false, readerExceptions.size() + 1L);
        C unexpectedExceptionUseCase = podamFactory.manufacturePojo(rowDtoClass);
        List<Pair<C, List<E>>> unhappyUseCases = buildAndConfigureUnhappyUseCases(ingestionFlowFile, readerExceptions.size() + 2L);

        Mockito.doThrow(new RuntimeException("DUMMYCONSUMEROWEXCEPTION"))
                .when(getServiceSpy())
                .consumeRow(Mockito.anyLong(), Mockito.same(unexpectedExceptionUseCase), Mockito.any(), Mockito.any());

        // When
        R result = startProcess(Stream.concat(
                Stream.of(happyUseCase, unexpectedExceptionUseCase),
                unhappyUseCases.stream().map(Pair::getLeft)
        ).iterator(), readerExceptions, ingestionFlowFile, workingDirectory);

        // Then
        assertEquals(2 + unhappyUseCases.size() + readerExceptions.size(), result.getTotalRows());
        assertEquals(1, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        assertEquals(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        assertEquals(ingestionFlowFile.getFileVersion(), result.getFileVersion());
        assertEquals(ingestionFlowFile.getOperatorExternalId(), result.getOperatorExternalUserId());

        assertIngestionFlowFileResultExtension(result, List.of(happyUseCase));

        Mockito.verify(getErrorsArchiverServiceMock()).writeErrors(workingDirectory, ingestionFlowFile, Stream.concat(
                        Stream.concat(
                                readerExceptions.stream().map(e -> buildCsvGenericErrorDto(ingestionFlowFile, e)),
                                Stream.of(
                                        buildGenericErrorDto(ingestionFlowFile, readerExceptions.size() + 2, unexpectedExceptionUseCase, "PROCESSING_ERROR", "DUMMYCONSUMEROWEXCEPTION")
                                )
                        ),
                        unhappyUseCases.stream().flatMap(p -> p.getRight().stream())
                ).toList()
        );
        Mockito.verify(getErrorsArchiverServiceMock()).archiveErrorFiles(workingDirectory, ingestionFlowFile);
    }

    private E buildCsvGenericErrorDto(IngestionFlowFile ingestionFlowFile, CsvException csvException) {
        return buildGenericErrorDto(ingestionFlowFile, csvException.getLineNumber(), null,
                "CSV_GENERIC_ERROR", "Errore generico nella lettura del file: " + csvException.getMessage());
    }

    private E buildGenericErrorDto(IngestionFlowFile ingestionFlowFile, long rowNumber, C row, String errorCode, String errorMessage) {
        return getServiceSpy().buildErrorDto(ingestionFlowFile, rowNumber, row, errorCode, errorMessage);
    }

    @Test
    void givenMultipleRowsWithSequencedOpsWhenProcessThenHandleRightSequenceProcessing() {
        // Given
        int rowNumber = 0;
        List<Pair<Integer, C>> sequencingId2Rows = List.of(
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 1, false, ++rowNumber), // taskProcessing: 1
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 2, false, ++rowNumber), // taskProcessing: 2
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 3, false, ++rowNumber), // taskProcessing: 3
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 1, true, ++rowNumber),  // waiting for previous execution. taskProcessing: 1
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 2, true, ++rowNumber),  // taskProcessing: 2
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 1, true, ++rowNumber),  // waiting for previous execution. taskProcessing: 1
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 4, false, ++rowNumber), // taskProcessing: 2
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 5, false, ++rowNumber), // taskProcessing: 3
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 6, false, ++rowNumber), // taskProcessing: 4
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 7, false, ++rowNumber), // taskProcessing: 5
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 8, false, ++rowNumber), // taskProcessing: 6
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 9, false, ++rowNumber), // taskProcessing: 7
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 10, false, ++rowNumber),// taskProcessing: 8
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 11, false, ++rowNumber),// taskProcessing: 9
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 12, false, ++rowNumber),// taskProcessing: 10
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 13, false, ++rowNumber),// waiting because MAX_CONCURRENT_PROCESSING_ROWS has been reached. taskProcessing: 1
                buildAndConfigureHappyUseCasePairWithSequencingId(ingestionFlowFile, 3, true, ++rowNumber)   // taskProcessing: 2
                // waiting because rows are terminated
        );

        String[] lineNumberThreadNames = new String[sequencingId2Rows.size()];
        Mockito.doAnswer(a -> {
                    Long lineNumber = a.getArgument(CONSUMER_ROW_METHOD_ARGUMENT_INDEX_LINE_NUMBER);
                    lineNumberThreadNames[lineNumber.intValue() - 1] = Thread.currentThread().toString();
                    return a.callRealMethod();
                })
                .when(getServiceSpy())
                .consumeRow(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());

        List<C> rows = sequencingId2Rows.stream().map(Pair::getValue).toList();

        // When
        R result = startProcess(rows.iterator(), List.of(), ingestionFlowFile, workingDirectory);

        // Then
        assertEquals(sequencingId2Rows.size(), result.getTotalRows());
        assertEquals(sequencingId2Rows.size(), result.getProcessedRows());
        assertNull(result.getErrorDescription());
        assertNull(result.getDiscardedFileName());
        assertEquals(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        assertEquals(ingestionFlowFile.getFileVersion(), result.getFileVersion());
        assertEquals(ingestionFlowFile.getOperatorExternalId(), result.getOperatorExternalUserId());

        assertIngestionFlowFileResultExtension(result, rows);

        Map<C, Invocation> row2Invocation = Mockito.mockingDetails(getServiceSpy()).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("consumeRow"))
                .collect(Collectors.toMap(i -> i.getArgument(1), Function.identity()));

        for (int i = 0; i < sequencingId2Rows.size(); i++) {
            Invocation invocation = row2Invocation.get(sequencingId2Rows.get(i).getRight());
            Assertions.assertEquals(i + 1, getLineNumberFromConsumeRowInvocation(invocation), "lineNumber not correctly handled");
        }

        sequencingId2Rows.stream()
                .collect(Collectors.groupingBy(Pair::getLeft))
                .values()
                .forEach(sameSequencingIdRows -> {
                    String sequencingId = getSequencingId(sameSequencingIdRows.getFirst().getRight());

                    Assertions.assertEquals(
                            Set.of(sequencingId),
                            sameSequencingIdRows.stream().map(Pair::getRight).map(this::getSequencingId).collect(Collectors.toSet()),
                            "SequencingId not correctly handled for rows with same sequencingId: check if buildAndConfigureHappyUseCase is correctly using it"
                    );

                    List<Triple<Integer, Long, String>> processedOrder2lineNumber = sameSequencingIdRows.stream()
                            .map(sequencingId2Row -> row2Invocation.get(sequencingId2Row.getRight()))
                            .map(i -> Pair.of(i.getSequenceNumber(), getLineNumberFromConsumeRowInvocation(i)))
                            .map(p -> Triple.of(p.getLeft(), p.getRight(), lineNumberThreadNames[p.getRight().intValue() - 1]))
                            .toList();

                    log.debug("{} has been executed on the following order [(processedOrder,lineNumber,threadName)]:{}", sequencingId, processedOrder2lineNumber);
                    Assertions.assertEquals(
                            processedOrder2lineNumber.stream().sorted(Comparator.comparingInt(Triple::getLeft)).toList(),
                            processedOrder2lineNumber.stream().sorted(Comparator.comparingLong(Triple::getMiddle)).toList(),
                            "SequencingId not correctly handled for rows with same sequencingId: check if consumeRow is correctly waiting the processing of previous rows with same sequencingId"
                    );
                });

        Assertions.assertTrue(Arrays.stream(lineNumberThreadNames).collect(Collectors.toSet()).size() > 1,
                "Test is not valid: all rows have been processed by the same thread, so sequencing cannot be verified. Check if service is correctly configured for parallel processing on the test.");

        Set<Integer> alreadySeenSequencingIds = new HashSet<>();
        List<String> expectedReachedMaxConcurrentProcessingRowsMessages = new ArrayList<>();
        List<String> expectedRetrievingResultsMessages = new ArrayList<>();
        for (Pair<Integer, C> sequencingId2Row : sequencingId2Rows) {
            Integer sequencingId = sequencingId2Row.getKey();
            boolean foundExistingSequencingId = alreadySeenSequencingIds.contains(sequencingId);
            boolean reachedMaxConcurrentProcessingRows = alreadySeenSequencingIds.size() >= MAX_CONCURRENT_PROCESSING_ROWS;
            if (foundExistingSequencingId || reachedMaxConcurrentProcessingRows) {
                expectedReachedMaxConcurrentProcessingRowsMessages.add(
                        "Reached max concurrent processing rows (%d/%d) or found existing sequencingId %s (%s), retrieving results for ingestionFlowFileId %d".formatted(
                                alreadySeenSequencingIds.size(),
                                MAX_CONCURRENT_PROCESSING_ROWS,
                                getSequencingId(sequencingId2Row.getValue()),
                                foundExistingSequencingId,
                                ingestionFlowFile.getIngestionFlowFileId()
                        ));
                addExpectedRetrievingResultsMessage(alreadySeenSequencingIds, expectedRetrievingResultsMessages);
                alreadySeenSequencingIds.clear();
            }
            alreadySeenSequencingIds.add(sequencingId);
        }
        addExpectedRetrievingResultsMessage(alreadySeenSequencingIds, expectedRetrievingResultsMessages);

        Assertions.assertEquals(
                expectedReachedMaxConcurrentProcessingRowsMessages,
                memoryAppender.getLoggedEvents().stream()
                        .map(ILoggingEvent::getFormattedMessage)
                        .filter(message -> message.startsWith("Reached max concurrent processing rows"))
                        .toList()
        );
        Assertions.assertEquals(
                expectedRetrievingResultsMessages,
                memoryAppender.getLoggedEvents().stream()
                        .map(ILoggingEvent::getFormattedMessage)
                        .filter(message -> message.startsWith("Retrieving results for "))
                        .toList()
        );
    }

    private void addExpectedRetrievingResultsMessage(Set<Integer> alreadySeenSequencingIds, List<String> expectedRetrievingResultsMessages) {
        expectedRetrievingResultsMessages.add(
                "Retrieving results for %s tasks for ingestionFlowFileId %d".formatted(
                        alreadySeenSequencingIds.size(), ingestionFlowFile.getIngestionFlowFileId()
                ));
    }

    private Long getLineNumberFromConsumeRowInvocation(Invocation i) {
        return i.getArgument(CONSUMER_ROW_METHOD_ARGUMENT_INDEX_LINE_NUMBER);
    }

    private Pair<Integer, C> buildAndConfigureHappyUseCasePairWithSequencingId(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        return Pair.of(sequencingId, buildAndConfigureHappyUseCase(ingestionFlowFile, sequencingId, sequencingIdAlreadySent, rowNumber));
    }

    private String getSequencingId(C dto) {
        return getServiceSpy().getSequencingId(dto);
    }
}
