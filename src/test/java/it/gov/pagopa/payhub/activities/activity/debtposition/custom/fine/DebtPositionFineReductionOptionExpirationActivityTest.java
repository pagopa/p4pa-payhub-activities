package it.gov.pagopa.payhub.activities.activity.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.service.debtposition.custom.fine.DebtPositionFineReductionOptionExpirationProcessor;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionFineReductionOptionExpirationActivityTest {

    @Mock
    private DebtPositionFineReductionOptionExpirationProcessor debtPositionFineReductionOptionExpirationProcessorMock;

    private DebtPositionFineReductionOptionExpirationActivity activity;

    @BeforeEach
    void setUp(){
        activity = new DebtPositionFineReductionOptionExpirationActivityImpl(debtPositionFineReductionOptionExpirationProcessorMock);
    }

    @Test
    void whenHandleFineReductionExpirationThenOk(){
        // Given
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        when(debtPositionFineReductionOptionExpirationProcessorMock.handleFineReductionExpiration(debtPositionId))
                .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = activity.handleFineReductionExpiration(debtPositionId);

        // Then
        assertEquals(debtPositionDTO, result);
    }
}
