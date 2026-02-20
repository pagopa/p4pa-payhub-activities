package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csv;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv.TreasuryCsvMapper;
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
class TreasuryCsvProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<TreasuryCsvIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryCsvErrorDTO> {

    @Mock
    private TreasuryCsvErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private TreasuryCsvMapper mapperMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private TreasuryCsvProcessingService serviceSpy;

    protected TreasuryCsvProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new TreasuryCsvProcessingService(
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
    protected TreasuryCsvProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<TreasuryCsvErrorDTO, TreasuryIufIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult startProcess(Iterator<TreasuryCsvIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processTreasuryCsv(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory, new TreasuryIufIngestionFlowFileResult());
    }

    @Override
    protected TreasuryCsvIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        String iuf = "IUF" + rowNumber;
        String treasuryId = "TREASURYID" + rowNumber;

        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillAmount(String.valueOf(rowNumber));
        dto.setBillCode("BILLCODE" + sequencingId);
        dto.setBillYear("BILLYEAR" + sequencingId);
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/" + iuf);

        Treasury treasury = podamFactory.manufacturePojo(Treasury.class)
                .treasuryId(treasuryId)
                .iuf(iuf);

        // useCase no IUF
        if(sequencingId == 1) {
            dto.setRemittanceDescription(null);
            treasury.setIuf(null);
        }
        else {
            TreasuryIuf existingTreasuryIuf = null;
            // useCase existing and matching treasury
            if (sequencingId == 2) {
                existingTreasuryIuf = new TreasuryIuf();
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
    protected void assertIngestionFlowFileResultExtension(TreasuryIufIngestionFlowFileResult result, List<TreasuryCsvIngestionFlowFileDTO> happyUseCases) {
        Assertions.assertEquals(
                happyUseCases.stream()
                        .map(row -> {
                            String rowNumber = row.getBillAmount();
                            String treasuryId = "TREASURYID" + rowNumber;
                            String iuf = Objects.requireNonNullElse(
                                    TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF),
                                    TreasuryUtils.generateTechnicalIuf(treasuryId)
                            );
                            return Pair.of(iuf, treasuryId);
                        })
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue)),
                result.getIuf2TreasuryIdMap()
        );
    }

    @Override
    protected List<Pair<TreasuryCsvIngestionFlowFileDTO, List<TreasuryCsvErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<TreasuryCsvIngestionFlowFileDTO, List<TreasuryCsvErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, false);
    }
    private Pair<TreasuryCsvIngestionFlowFileDTO, List<TreasuryCsvErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, true);
    }
    private Pair<TreasuryCsvIngestionFlowFileDTO, List<TreasuryCsvErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociated(IngestionFlowFile ingestionFlowFile, long rowNumber, boolean matchBillCode) {
        String iuf = "IUF" + rowNumber;

        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillCode("BILLCODE"+rowNumber);
        dto.setBillYear("BILLYEAR"+rowNumber);
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/" + iuf);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        if(matchBillCode) {
            existingTreasuryIuf.setBillCode(dto.getBillCode());
        } else {
            existingTreasuryIuf.setBillYear(dto.getBillYear());
        }
        Mockito.doReturn(existingTreasuryIuf)
                .when(treasuryServiceMock)
                .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), iuf);

        List<TreasuryCsvErrorDTO> expectedErrors = List.of(
                TreasuryCsvErrorDTO.builder()
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
