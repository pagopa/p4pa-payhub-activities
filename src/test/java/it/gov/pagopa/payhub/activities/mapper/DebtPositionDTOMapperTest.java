package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildPaymentsDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentOptionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PaymentOptionFaker.buildPaymentsPaymentOptionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DebtPositionDTOMapperTest {

    @Mock
    private PaymentOptionDTOMapper paymentOptionDTOMapperMock;

    @InjectMocks
    private final DebtPositionDTOMapper mapper = Mappers.getMapper(DebtPositionDTOMapper.class);

    @Test
    void testMapDebtPositionDTO(){
        DebtPositionDTO debtPositionDTOexpected = buildPaymentsDebtPositionDTO();

        Mockito.when(paymentOptionDTOMapperMock.map(buildPaymentOptionDTO())).thenReturn(buildPaymentsPaymentOptionDTO());

        DebtPositionDTO result = mapper.map(buildDebtPositionDTO());

        assertEquals(debtPositionDTOexpected, result);
        checkNotNullFields(result);
    }
}
