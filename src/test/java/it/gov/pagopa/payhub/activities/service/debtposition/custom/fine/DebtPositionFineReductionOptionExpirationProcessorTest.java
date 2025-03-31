package it.gov.pagopa.payhub.activities.service.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.PaymentOptionService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO2;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineReductionOptionExpirationProcessorTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;
    @Mock
    private PaymentOptionService paymentOptionServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;

    private DebtPositionFineReductionOptionExpirationProcessor processor;

    @BeforeEach
    void setUp(){
        processor = new DebtPositionFineReductionOptionExpirationProcessor(
                debtPositionServiceMock,
                paymentOptionServiceMock,
                installmentServiceMock);
    }

    @Test
    void givenDPNotPaidOrReportedWhenHandleFineReductionExpirationThenReturnUpdatedDP(){
        // Given
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        InstallmentDTO installmentDTO2 = buildInstallmentDTO2();
        paymentOptionDTO2.setInstallments(List.of(installmentDTO2));
        paymentOptionDTO2.setPaymentOptionType(PaymentOptionTypeEnum.SINGLE_INSTALLMENT);
        debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO2));

        when(debtPositionServiceMock.getDebtPosition(debtPositionId))
                .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = processor.handleFineReductionExpiration(debtPositionId);
    }
}
