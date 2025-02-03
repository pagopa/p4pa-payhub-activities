package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.InstallmentDTOMapper;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.PaymentOptionDTOMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class PaymentOptionDTOMapperTest {

    @Mock
    private InstallmentDTOMapper installmentDTOMapperMock;

    @InjectMocks
    private final PaymentOptionDTOMapper mapper = Mappers.getMapper(PaymentOptionDTOMapper.class);

    @Test
    void testMapPaymentOptionDTO(){
        // Given
        PaymentOptionDTO paymentOptionDTO = buildPaymentOptionDTO();

        InstallmentDTO expectedPaymentsInstallment = Mockito.mock(InstallmentDTO.class);
        Mockito.when(installmentDTOMapperMock.map(paymentOptionDTO.getInstallments().getFirst()))
                .thenReturn(expectedPaymentsInstallment);

        // When
        it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO result = mapper.map(paymentOptionDTO);

        // Then
        TestUtils.reflectionEqualsByName(paymentOptionDTO, result, "installments");
        assertSame(expectedPaymentsInstallment, result.getInstallments().getFirst());
        checkNotNullFields(result);
    }
}
