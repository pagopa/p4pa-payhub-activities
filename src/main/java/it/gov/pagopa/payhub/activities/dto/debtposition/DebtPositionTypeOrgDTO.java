package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DebtPositionTypeOrgDTO implements Serializable {

    private Long debtPositionTypeOrgId;
    private OrganizationDTO org;
    private DebtPositionType debtPositionType;
    private String iban;
    private String balance; // XML
    private String typeCode;
    private String typeDesc;
    private boolean flagMandatoryDueDate;
    private boolean flagAnonymousFiscalCode;
    private String creditIbanPi;
    private String creditIbanPSP;
    private BigDecimal amount;
    private String installmentPaymentUrl;
    private String balanceDefaultDesc;
    private String orgSector;
    private String postalAccountCode;
    private String holderPostalCC;
    private boolean flagNotifyIO;
    private boolean flagDisablePrintNotice;
    private boolean flagNotifyOutcomePush;
    private Integer maxAttemptForwardingOutcome;
    private String xsdReasonCode;
    private String paymentContextCode;
    private String taxonomyCode;
    private String collectingReason;
    private Long orgSilId;
    private String serviceType;
    private boolean flgDisablePrintNotice;
    private String macroArea;


    // da controllare cosa tenere o meno
    private String backingIbanPi;
    private String backingIbanPSP;
    private boolean flagActive;
    private String urlNotifyPnd;
    private String userPnd;
    private String pswPnd;
    private String urlNotifyActualizationPnd;
}