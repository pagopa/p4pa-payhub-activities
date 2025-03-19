package it.gov.pagopa.payhub.activities.dto.debtposition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GenericWfExecutionConfig implements WfExecutionConfig {
    private IONotificationBaseMessages ioMessages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class IONotificationBaseMessages implements Serializable {
        private String created;
        private String updated;
        private String deleted;
    }
}
