package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class DebtPositionExpirationActivityTest {

    @Mock
    private DebtPositionService debtPositionServiceMock;

    private DebtPositionExpirationActivity activity;

    @BeforeEach
    void init() {
        activity = new DebtPositionExpirationActivityImpl(debtPositionServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(debtPositionServiceMock);
    }

    @Test
    void givenCheckAndUpdateInstallmentExpirationThenOk(){
        Mockito.when(debtPositionServiceMock.checkAndUpdateInstallmentExpiration(1L))
                .thenReturn(OFFSETDATETIME);

        OffsetDateTime offsetDateTime = activity.checkAndUpdateInstallmentExpiration(1L);

        Mockito.verify(debtPositionServiceMock, Mockito.times(1))
                .checkAndUpdateInstallmentExpiration(1L);
        assertSame(OFFSETDATETIME, offsetDateTime);
    }
}
