package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.PaymentOptionDTOMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class DebtPositionDTOMapperTest {

    @Mock
    private PaymentOptionDTOMapper paymentOptionDTOMapperMock;

    @InjectMocks
    private final DebtPositionDTOMapper mapper = Mappers.getMapper(DebtPositionDTOMapper.class);

    @Test
    void testMapDebtPositionDTO(){
        // Given
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        PaymentOptionDTO expectedPaymentOption = Mockito.mock(PaymentOptionDTO.class);
        Mockito.when(paymentOptionDTOMapperMock.map(debtPositionDTO.getPaymentOptions().getFirst()))
                .thenReturn(expectedPaymentOption);

        // When
        it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO result = mapper.map(debtPositionDTO);

        // Then
        TestUtils.reflectionEqualsByName(debtPositionDTO, result, "paymentOptions");
        assertSame(expectedPaymentOption, result.getPaymentOptions().getFirst());
        checkNotNullFields(result);
    }
}
