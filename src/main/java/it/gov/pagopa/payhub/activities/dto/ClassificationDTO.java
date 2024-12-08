package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationDTO implements Serializable {

    private String iuv;
    private Long orgId;
    private Long receiptId;
    private Long transferIndex;
    private Long amount;
    private Long transferId;
}
