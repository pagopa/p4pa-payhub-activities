package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.TreasuryXlsMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<TreasuryXlsIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryXlsErrorDTO> {

    @Mock
    private TreasuryXlsErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private TreasuryXlsMapper mapperMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private TreasuryXlsProcessingService serviceSpy;

    protected TreasuryXlsProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new TreasuryXlsProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                treasuryServiceMock,
                errorsArchiverServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                treasuryServiceMock,
                errorsArchiverServiceMock,
                organizationServiceMock
        );
    }

    @Override
    protected TreasuryXlsProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<TreasuryXlsErrorDTO> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult startProcess(Iterator<TreasuryXlsIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processTreasuryXls(rowIterator, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected void givenReaderExceptionAndNoProcessedRowsWhenProcessThenWriteError() {
        // Do nothing: XLS iterator doesn't support CsvException
    }

    @Override
    protected List<CsvException> buildReaderExceptions() {
        return Collections.emptyList();
    }

    @Override
    protected TreasuryXlsIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        String iuf = "IUF" + sequencingId;
        String billCode = TreasuryUtils.generateBillCode(iuf);
        LocalDate billDate = LocalDate.now();
        String treasuryId = "TREASURYID" + sequencingId;

        TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
        dto.setBillDate(billDate);
        dto.setExtendedRemittanceDescription("Data Ordine: 21/07/2024; Descrizione Ordinante: XYZ PRIVATE BANKING SPA                          PIAZZA SAN :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/%s :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM".formatted(iuf));
        dto.setBillAmountCents(rowNumber);

        TreasuryIuf existingTreasuryIuf = null;
        // useCase existing and matching treasury
        if (sequencingId == 1) {
            existingTreasuryIuf = new TreasuryIuf();
            existingTreasuryIuf.setIuf(iuf);
            existingTreasuryIuf.setBillCode(billCode);
            existingTreasuryIuf.setBillYear(String.valueOf(billDate.getYear()));
        }

        if (!sequencingIdAlreadySent) {
            Mockito.doReturn(existingTreasuryIuf)
                    .when(treasuryServiceMock)
                    .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), iuf);
        }

        Treasury treasury = podamFactory.manufacturePojo(Treasury.class);
        treasury.setIuf(iuf);
        treasury.setTreasuryId(treasuryId);

        Mockito.doReturn(treasury)
                .when(mapperMock)
                .map(dto, ingestionFlowFile);
        Mockito.doReturn(treasury)
                .when(treasuryServiceMock)
                .insert(treasury);

        return dto;
    }

    @Override
    protected void assertIngestionFlowFileResultExtension(TreasuryIufIngestionFlowFileResult result, List<TreasuryXlsIngestionFlowFileDTO> happyUseCases) {
        Assertions.assertEquals(
                happyUseCases.stream()
                        .map(row -> {
                            String iuf = TreasuryUtils.getIdentificativo(row.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
                            String treasuryId = "TREASURYID" + Objects.requireNonNull(iuf).replace("IUF", "");
                            return Pair.of(iuf, treasuryId);
                        })
                        .distinct()
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue)),
                result.getIuf2TreasuryIdMap()
        );
    }

    @Override
    protected List<Pair<TreasuryXlsIngestionFlowFileDTO, List<TreasuryXlsErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<TreasuryXlsIngestionFlowFileDTO, List<TreasuryXlsErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, false);
    }

    private Pair<TreasuryXlsIngestionFlowFileDTO, List<TreasuryXlsErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, true);
    }

    private Pair<TreasuryXlsIngestionFlowFileDTO, List<TreasuryXlsErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociated(IngestionFlowFile ingestionFlowFile, long rowNumber, boolean matchBillCode) {
        String iuf = "IUFUNHAPPY" + rowNumber;
        String billCode = TreasuryUtils.generateBillCode(iuf);
        LocalDate billDate = LocalDate.now();

        TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
        dto.setBillDate(billDate);
        dto.setExtendedRemittanceDescription("Data Ordine: 21/07/2024; Descrizione Ordinante: XYZ PRIVATE BANKING SPA                          PIAZZA SAN :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/%s :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM".formatted(iuf));
        dto.setBillAmountCents(rowNumber);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        if (matchBillCode) {
            existingTreasuryIuf.setBillCode(billCode);
        } else {
            existingTreasuryIuf.setBillYear(String.valueOf(billDate.getYear()));
        }
        Mockito.doReturn(existingTreasuryIuf)
                .when(treasuryServiceMock)
                .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), iuf);


        List<TreasuryXlsErrorDTO> expectedErrors = List.of(
                TreasuryXlsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF %s e' gia' associato ad un'altra tesoreria per l'ente con codice IPA %s".formatted(iuf, organization.getIpaCode()))
                        .iuf(iuf)
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }
}