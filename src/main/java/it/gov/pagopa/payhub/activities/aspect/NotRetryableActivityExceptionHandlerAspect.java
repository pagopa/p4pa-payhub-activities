package it.gov.pagopa.payhub.activities.aspect;

import io.temporal.failure.ApplicationFailure;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.performancelogger.PerformanceLogger;
import it.gov.pagopa.payhub.activities.performancelogger.PerformanceLoggerThresholdLevels;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class NotRetryableActivityExceptionHandlerAspect {

    private static final PerformanceLoggerThresholdLevels defaultPerformanceThresholdLevels =
            new PerformanceLoggerThresholdLevels(300, 1200);

    @Pointcut("within(it.gov.pagopa.payhub.activities.activity..*)")
    public void activityBean() {
        // Do nothing
    }

    @Around("activityBean()")
    public Object aroundActivity(ProceedingJoinPoint jp) {
        try {
            return PerformanceLogger.execute(
                    "ACTIVITY",
                    jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName(),
                    () -> {
                        try {
                            return jp.proceed();
                        } catch (Throwable e) {
                            if(e instanceof RuntimeException runtimeException){
                                throw runtimeException;
                            } else {
                                throw new IllegalStateException("Something went wrong during activity execution: " + jp.getSignature(), e);
                            }
                        }
                    },
                    null,
                    defaultPerformanceThresholdLevels
            );
        } catch (NotRetryableActivityException error) {
            log.debug("Activity thrown NotRetryableException {} in method {}", error.getClass().getName(), jp.getSignature());
            throw ApplicationFailure.newNonRetryableFailureWithCause(error.getMessage(), error.getClass().getName(), error);
        }
    }

}
