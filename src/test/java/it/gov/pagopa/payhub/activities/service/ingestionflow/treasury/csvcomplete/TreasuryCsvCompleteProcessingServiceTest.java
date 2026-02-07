package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete.TreasuryCsvCompleteMapper;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class TreasuryCsvCompleteProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<TreasuryCsvCompleteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvCompleteErrorDTO> {

    @Mock
    private TreasuryCsvCompleteErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private TreasuryCsvCompleteMapper mapperMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private TreasuryCsvCompleteProcessingService serviceSpy;

    protected TreasuryCsvCompleteProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new TreasuryCsvCompleteProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                treasuryServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                treasuryServiceMock,
                organizationServiceMock
        );
    }

    @Override
    protected TreasuryCsvCompleteProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<TreasuryCsvCompleteErrorDTO> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult startProcess(Iterator<TreasuryCsvCompleteIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processTreasuryCsvComplete(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected TreasuryCsvCompleteIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        String iuf = "IUF" + rowNumber;
        String treasuryId = "TREASURYID" + rowNumber;

        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());
        dto.setBillCode("BILLCODE" + sequencingId);
        dto.setBillYear("BILLYEAR" + sequencingId);
        dto.setIuf(iuf);
        dto.setBillAmountCents(rowNumber);

        Treasury treasury = podamFactory.manufacturePojo(Treasury.class)
                .treasuryId(treasuryId)
                .iuf(iuf);

        // useCase no IUF
        if(sequencingId == 1) {
            dto.setIuf(null);
            treasury.setIuf(null);
        }
        else {
            TreasuryIuf existingTreasuryIuf = null;
            // useCase existing and matching treasury
            if (sequencingId == 2) {
                existingTreasuryIuf = new TreasuryIuf();
                existingTreasuryIuf.setIuf(iuf);
                existingTreasuryIuf.setBillCode(dto.getBillCode());
                existingTreasuryIuf.setBillYear(dto.getBillYear());
            }

            Mockito.doReturn(existingTreasuryIuf)
                    .when(treasuryServiceMock)
                    .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), iuf);
        }

        Mockito.doReturn(treasury)
                .when(mapperMock)
                .map(dto, ingestionFlowFile);
        Mockito.doReturn(treasury)
                .when(treasuryServiceMock)
                .insert(treasury);

        return dto;
    }

    @Override
    protected void assertIngestionFlowFileResultExtension(TreasuryIufIngestionFlowFileResult result, List<TreasuryCsvCompleteIngestionFlowFileDTO> happyUseCases) {
        Assertions.assertEquals(
                happyUseCases.stream()
                        .map(row -> {
                            long rowNumber = row.getBillAmountCents();
                            String treasuryId = "TREASURYID" + rowNumber;
                            String iuf = Objects.requireNonNullElse(
                                    row.getIuf(),
                                    TreasuryUtils.generateTechnicalIuf(treasuryId)
                            );
                            return Pair.of(iuf, treasuryId);
                        })
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue)),
                result.getIuf2TreasuryIdMap()
        );
    }

    @Override
    protected List<Pair<TreasuryCsvCompleteIngestionFlowFileDTO, List<TreasuryCsvCompleteErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIpaMissmatch(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<TreasuryCsvCompleteIngestionFlowFileDTO, List<TreasuryCsvCompleteErrorDTO>> configureUnhappyUseCaseIpaMissmatch(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode("WRONGIPA");

        List<TreasuryCsvCompleteErrorDTO> expectedErrors = List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("ORGANIZATION_IPA_MISMATCH")
                        .errorMessage("Il codice IPA WRONGIPA dell'ente non corrisponde a quello del file " + organization.getIpaCode())
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<TreasuryCsvCompleteIngestionFlowFileDTO, List<TreasuryCsvCompleteErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, false);
    }
    private Pair<TreasuryCsvCompleteIngestionFlowFileDTO, List<TreasuryCsvCompleteErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, true);
    }
    private Pair<TreasuryCsvCompleteIngestionFlowFileDTO, List<TreasuryCsvCompleteErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociated(IngestionFlowFile ingestionFlowFile, long rowNumber, boolean matchBillCode) {
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());
        dto.setBillCode("BILLCODE"+rowNumber);
        dto.setBillYear("BILLYEAR"+rowNumber);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        if(matchBillCode) {
            existingTreasuryIuf.setBillCode(dto.getBillCode());
        } else {
            existingTreasuryIuf.setBillYear(dto.getBillYear());
        }
        Mockito.doReturn(existingTreasuryIuf)
                .when(treasuryServiceMock)
                .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), dto.getIuf());

        List<TreasuryCsvCompleteErrorDTO> expectedErrors = List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF %s e' gia' associato ad un'altra tesoreria per l'ente con codice IPA %s".formatted(dto.getIuf(), organization.getIpaCode()))
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

}
