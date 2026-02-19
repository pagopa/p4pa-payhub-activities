package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper;
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

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class TreasuryPosteProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<TreasuryPosteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryPosteErrorDTO> {

    @Mock
    private TreasuryPosteErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private TreasuryPosteMapper mapperMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private TreasuryPosteProcessingService serviceSpy;

    protected TreasuryPosteProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new TreasuryPosteProcessingService(
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
    protected TreasuryPosteProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<TreasuryPosteErrorDTO, TreasuryIufIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected TreasuryIufIngestionFlowFileResult startProcess(Iterator<TreasuryPosteIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processTreasuryPoste(rowIterator, "IBAN", readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected TreasuryPosteIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        String iuf = "IUF" + sequencingId;
        String billCode = TreasuryUtils.generateBillCode(iuf);
        LocalDate billDate = LocalDate.now();
        String treasuryId = "TREASURYID" + sequencingId;

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(billDate.format(TreasuryPosteMapper.POSTE_DATE_FORMAT));
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/%s ACCREDITO BOLLETTINO P.A.".formatted(iuf));
        dto.setDebitBillAmount(BigDecimal.valueOf(rowNumber));

        TreasuryIuf existingTreasuryIuf = null;
        // useCase existing and matching treasury
        if (sequencingId == 1) {
            existingTreasuryIuf = new TreasuryIuf();
            existingTreasuryIuf.setIuf(iuf);
            existingTreasuryIuf.setBillCode(billCode);
            existingTreasuryIuf.setBillYear(String.valueOf(billDate.getYear()));
        }

        if(!sequencingIdAlreadySent) {
            Mockito.doReturn(existingTreasuryIuf)
                    .when(treasuryServiceMock)
                    .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), iuf);
        }

        Treasury treasury = podamFactory.manufacturePojo(Treasury.class);
        treasury.setIuf(iuf);
        treasury.setTreasuryId(treasuryId);

        Mockito.doReturn(treasury)
                .when(mapperMock)
                .map(dto, "IBAN", iuf, billCode, billDate, ingestionFlowFile);
        Mockito.doReturn(treasury)
                .when(treasuryServiceMock)
                .insert(treasury);

        return dto;
    }

    @Override
    protected void assertIngestionFlowFileResultExtension(TreasuryIufIngestionFlowFileResult result, List<TreasuryPosteIngestionFlowFileDTO> happyUseCases) {
        Assertions.assertEquals(
                happyUseCases.stream()
                        .map(row -> {
                            String iuf = TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF);
                            String treasuryId = "TREASURYID" + Objects.requireNonNull(iuf).replace("IUF", "");
                            return Pair.of(iuf, treasuryId);
                        })
                        .distinct()
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue)),
                result.getIuf2TreasuryIdMap()
        );
    }

    @Override
    protected List<Pair<TreasuryPosteIngestionFlowFileDTO, List<TreasuryPosteErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<TreasuryPosteIngestionFlowFileDTO, List<TreasuryPosteErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillCode(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, false);
    }

    private Pair<TreasuryPosteIngestionFlowFileDTO, List<TreasuryPosteErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociatedUnmatchedBillYear(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        return configureUnhappyUseCaseIufAlreadyAssociated(ingestionFlowFile, rowNumber, true);
    }

    private Pair<TreasuryPosteIngestionFlowFileDTO, List<TreasuryPosteErrorDTO>> configureUnhappyUseCaseIufAlreadyAssociated(IngestionFlowFile ingestionFlowFile, long rowNumber, boolean matchBillCode) {
        String iuf = "IUFUNHAPPY" + rowNumber;
        String billCode = TreasuryUtils.generateBillCode(iuf);
        LocalDate billDate = LocalDate.now();

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(billDate.format(TreasuryPosteMapper.POSTE_DATE_FORMAT));
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/%s ACCREDITO BOLLETTINO P.A.".formatted(iuf));
        dto.setDebitBillAmount(BigDecimal.valueOf(rowNumber));

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        if (matchBillCode) {
            existingTreasuryIuf.setBillCode(billCode);
        } else {
            existingTreasuryIuf.setBillYear(String.valueOf(billDate.getYear()));
        }
        Mockito.doReturn(existingTreasuryIuf)
                .when(treasuryServiceMock)
                .getByOrganizationIdAndIuf(ingestionFlowFile.getOrganizationId(), iuf);


        List<TreasuryPosteErrorDTO> expectedErrors = List.of(
                TreasuryPosteErrorDTO.builder()
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