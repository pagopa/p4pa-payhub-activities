package it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.gov.pagopa.payhub.activities.enums.debtposition.DebtPositionSyncWfName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class WfExecutionConfigTest {
    @Test
    void testJsonTypeInfoConfig() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonSubTypes wfConfigSubTypes = WfExecutionConfig.class.getAnnotation(JsonSubTypes.class);
        Assertions.assertNotNull(wfConfigSubTypes);

        for (JsonSubTypes.Type type : wfConfigSubTypes.value()) {
            Assertions.assertEquals(
                    DebtPositionSyncWfName.fromValue(type.name()),
                    ((WfExecutionConfig)type.value().getConstructor().newInstance()).getWorkflowTypeName());

        }
    }
}
