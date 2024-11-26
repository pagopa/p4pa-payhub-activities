package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.enums.PaymentOptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOptionDTO implements Serializable {

    private OrganizationDTO org;
    private BigDecimal totalAmount;
    private String description;
    private String status;
    private boolean multiDebtor;
    private PaymentOptionType paymentOptionType;
    private Date dueDate;
    private List<InstallmentDTO> installments;
}
