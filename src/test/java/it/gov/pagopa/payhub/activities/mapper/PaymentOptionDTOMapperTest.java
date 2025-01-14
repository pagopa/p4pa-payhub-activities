package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildPaymentsInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentsPaymentOptionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PaymentOptionDTOMapperTest {

    @Mock
    private InstallmentDTOMapper installmentDTOMapperMock;

    @InjectMocks
    private final PaymentOptionDTOMapper mapper = Mappers.getMapper(PaymentOptionDTOMapper.class);

    @Test
    void testMapPaymentOptionDTO(){
        PaymentOptionDTO paymentOptionDTOexpected = buildPaymentsPaymentOptionDTO();

        Mockito.when(installmentDTOMapperMock.map(buildInstallmentDTO())).thenReturn(buildPaymentsInstallmentDTO());

        PaymentOptionDTO result = mapper.map(buildPaymentOptionDTO());

        assertEquals(paymentOptionDTOexpected, result);
        checkNotNullFields(result);
    }
}
