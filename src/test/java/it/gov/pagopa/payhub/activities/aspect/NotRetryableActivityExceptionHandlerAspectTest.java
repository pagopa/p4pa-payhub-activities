package it.gov.pagopa.payhub.activities.aspect;

import io.temporal.failure.ApplicationFailure;
import it.gov.pagopa.payhub.activities.activity.ingestionflow.UpdateIngestionFlowStatusActivity;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(classes = {NotRetryableActivityExceptionHandlerAspect.class})
@ComponentScan(basePackages = "it.gov.pagopa.payhub.activities.activity")
@EnableAspectJAutoProxy
class NotRetryableActivityExceptionHandlerAspectTest {

    @MockitoSpyBean
    private UpdateIngestionFlowStatusActivity statusActivitySpy;
    @MockitoBean
    private IngestionFlowFileDao ingestionFlowFileDaoMock;

    @Test
    void givenNotRetryableActivityExceptionExtensionWhenInvokeActivityThenExceptionWrapped(){
        // Given
        NotRetryableActivityException expectedNestedException = new NotRetryableActivityException("DUMMY"){};
        Mockito.when(ingestionFlowFileDaoMock.updateStatus(1L, "STATUS", null))
                .thenThrow(expectedNestedException);

        // When
        ApplicationFailure result = Assertions.assertThrows(ApplicationFailure.class, () -> statusActivitySpy.updateStatus(1L, "STATUS", null));

        // Then
        Assertions.assertTrue(result.isNonRetryable());
        Assertions.assertEquals(expectedNestedException.getMessage(), result.getOriginalMessage());
        Assertions.assertEquals(expectedNestedException.getClass().getName(), result.getType());
        Assertions.assertSame(expectedNestedException, result.getCause());
    }
}
