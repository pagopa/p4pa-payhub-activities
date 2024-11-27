package it.gov.pagopa.payhub.activities.dto.debtposition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DebtPositionType implements Serializable {

    private Long debtTypePositionId;
    private Long brokerId;
    private String code;
    private String descr;
    private String taxonomyCode;
    private String macroArea;
    private String serviceType;
    private String collectingReason;
    private boolean flagPrintDueDate;
    private boolean flagAnonymousFiscalCode; // esiste in DebtPositionTypeOrg
    private LocalDateTime dueDate; // Y/N? da mappare in caso
    private String description; // capire da dove prendere questo dato
}
