package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentnotification;


import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification.PaymentNotificationMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
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

@ExtendWith(MockitoExtension.class)
class PaymentNotificationProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<PaymentNotificationIngestionFlowFileDTO, PaymentNotificationIngestionFlowFileResult, PaymentNotificationErrorDTO> {

    @Mock
    private PaymentNotificationErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private PaymentNotificationMapper mapperMock;
    @Mock
    private PaymentNotificationService paymentNotificationServiceMock;

    private PaymentNotificationProcessingService serviceSpy;

    protected PaymentNotificationProcessingServiceTest() {
        super(false);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new PaymentNotificationProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                paymentNotificationServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                paymentNotificationServiceMock,
                organizationServiceMock);
    }

    @Override
    protected PaymentNotificationProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<PaymentNotificationErrorDTO, PaymentNotificationIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected PaymentNotificationIngestionFlowFileResult startProcess(Iterator<PaymentNotificationIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processPaymentNotification(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected PaymentNotificationIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        PaymentNotificationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(PaymentNotificationIngestionFlowFileDTO.class);
        dto.setIud("IUD" + sequencingId);

        PaymentNotificationDTO mappedNotification = podamFactory.manufacturePojo(PaymentNotificationDTO.class);
        mappedNotification.setIud(dto.getIud());

        Mockito.doReturn(mappedNotification)
                .when(mapperMock)
                .map(dto, ingestionFlowFile);

        Mockito.doReturn(mappedNotification)
                .when(paymentNotificationServiceMock)
                .createPaymentNotification(mappedNotification);

        return dto;
    }

    @Override
    protected void assertIngestionFlowFileResultExtension(PaymentNotificationIngestionFlowFileResult result, List<PaymentNotificationIngestionFlowFileDTO> happyUseCases) {
        Assertions.assertEquals(
                happyUseCases.stream()
                        .map(PaymentNotificationIngestionFlowFileDTO::getIud)
                        .sorted()
                        .toList(),
                result.getIudList().stream().sorted().toList()
        );
    }

    @Override
    protected List<Pair<PaymentNotificationIngestionFlowFileDTO, List<PaymentNotificationErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of();
    }
}