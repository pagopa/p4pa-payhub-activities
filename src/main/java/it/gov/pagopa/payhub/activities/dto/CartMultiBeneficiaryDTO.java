package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartMultiBeneficiaryDTO implements Serializable {

    private Long cartMultiBeneficiaryId;
    private int version;
    private String status; //anagrafica stato
    private String codeIpaOrg;
    private String codeAckCartRp;
    private Date creationDate;
    private Date lastUpdateDate;
    private String idSessionCart;
    private String idSessionCartFesp;
    private String paymentResponseUrl;
    private String rpSilSendCartOutcome;
    private String rpSilSendCartUrl;
    private String rpSilSendCartFaultCode;
    private String rpSilSendCartFaultString;
    private String rpSilSendCartIdCode;
    private String rpSilSendCartDescription;
    private Integer rpSilSendCartSerialCode;
    private String rpSilSendCartOriginalFaultCode;
    private String rpSilSendCartOriginalFaultString;
    private String rpSilSendCartOriginalFaultDescription;
}
