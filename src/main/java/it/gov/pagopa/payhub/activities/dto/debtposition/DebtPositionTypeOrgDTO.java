package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DebtPositionTypeOrgDTO implements Serializable {

    private Long debtPositionTypeOrgId;
    private OrganizationDTO org;
    private DebtPositionType debtPositionType;
    private String balance;
    private String code;
    private String description;
    private boolean flagMandatoryDueDate;
    private boolean flagAnonymousFiscalCode;
    private String postalIban;
    private String iban;
    private Long amount;
    private String externalPaymentUrl;
    private String balanceDefaultDesc;
    private String orgSector;
    private String postalAccountCode;
    private String holderPostalCC;
    private boolean flagNotifyIO;
    private boolean flagDisablePrintNotice;
    private boolean flagNotifyOutcomePush;
    private Integer maxAttemptForwardingOutcome;
    private String xsdDefinitionRef;
    private String taxonomyCode;
    private Long orgSilId;


    // da controllare cosa tenere o meno
    private boolean flagActive;
    private String urlNotifyPnd;
    private String userPnd;
    private String pswPnd;
    private String urlNotifyActualizationPnd;
}