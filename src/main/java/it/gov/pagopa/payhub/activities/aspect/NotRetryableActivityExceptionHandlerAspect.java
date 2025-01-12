package it.gov.pagopa.payhub.activities.aspect;

import io.temporal.failure.ApplicationFailure;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class NotRetryableActivityExceptionHandlerAspect {

    @Pointcut("within(it.gov.pagopa.payhub.activities.activity..*)")
    public void activityBean(){
        // Do nothing
    }

    @AfterThrowing(pointcut = "activityBean()", throwing = "error")
    public void afterThrowingAdvice(JoinPoint jp, NotRetryableActivityException error){
        log.debug("Activity thrown NotRetryableException {}", error.getClass().getName());
        throw ApplicationFailure.newNonRetryableFailureWithCause(error.getMessage(), error.getClass().getName(), error);
    }

}
