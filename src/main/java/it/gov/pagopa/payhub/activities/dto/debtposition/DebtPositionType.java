package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.IntermediaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtPositionType implements Serializable {

    private Long debtTypePositionId;
    private IntermediaryDTO intermediario;
    private String typeCode;
    private String typeDesc;
    private String taxonomyCode;
    private String macroArea;
    private String serviceType;
    private String collectingReason;
    private boolean flagPrintDueDate;
    private boolean flagAnonymousFiscalCode; // esiste in DebtPositionTypeOrg
    private LocalDateTime dueDate; // Y/N
    private String description;
}
