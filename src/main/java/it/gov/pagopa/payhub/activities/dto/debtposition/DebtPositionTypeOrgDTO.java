package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.organization.OrganizationDTO;
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

    private Long debtPositionTypeId;
    private OrganizationDTO orgId;
    private String typeCode;
    private String typeDesc;
    private String creditIbanPi;
    private String creditBicPi;
    private String backingIbanPi;
    private String backingBicPi;
    private String creditIbanPSP;
    private String creditBicPSP;
    private String backingIbanPSP;
    private String backingBicPSP;
    private String postalAccountCode;
    private String xsdReasonCode;
    private boolean creditBicPiSeller;
    private boolean creditBicPspSeller;
    private boolean spontaneous;
    private BigDecimal amount;
    private String installmentPaymentUrl;
    private String balanceDefaultDesc;
    private boolean flagAnonymousFiscalCode;
    private boolean flagMandatoryDueDate;
    private boolean flagPrintDueDate;
    private String holderPostalCC;
    private String orgSector;
    private boolean flagNotifyIO;
    private boolean flagNotifyOutcomePush;
    private Integer maxAttemptForwardingOutcome;
    private Long orgSilId;
    private boolean flagActive;
    private String paymentContextCode;
    private boolean flgDisablePrintNotice;
    private String macroArea;
    private String serviceType;
    private String collectingReason;
    private String taxonomyCode;
    private String urlNotifyPnd;
    private String userPnd;
    private String pswPnd;
    private String urlNotifyActualizationPnd;
}