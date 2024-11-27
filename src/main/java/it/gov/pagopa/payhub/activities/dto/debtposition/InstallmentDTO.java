package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.ReceiptDTO;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDTO implements Serializable {

    private Long installmentId;
    private String status;
    private String iud;
    private String iuv;
    private String iur;
    private Instant creationDate;
    private Instant updateDate;
    private LocalDate dueDate;
    private Character rpPayerUniqueIdentifierType;
    private String rpPayerUniqueIdentifierCode;
    private String rpPayerRegistry;
    private String rpPayerAddress;
    private String rpPayerCivic;
    private String rpPayerPostalCode;
    private String rpPayerLocation;
    private String rpPayerProvince;
    private String rpPayerNation;
    private String rpPayerEmail;
    private LocalDate paymentExecutionDate;
    private String paymentTypeCode;
    private Long singlePaymentAmount;
    private Long paCommissionFee;
    private String remittanceInformation;
    private Instant iuvCreationDate;
    private String humanFriendlyRemittanceInformation;
    private String balance;
    private boolean flagGenerateIuv;
    private String sessionId;
    private boolean flagIuvVolatile;
    private String collectionSpecificDetailsSinglePayment;


    private List<TransferDTO> transfers;
    private ReceiptDTO receipt;
}