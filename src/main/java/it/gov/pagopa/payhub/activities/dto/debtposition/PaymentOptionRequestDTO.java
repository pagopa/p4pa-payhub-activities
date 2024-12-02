package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.enums.PaymentOptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOptionRequestDTO {

    private Long totalAmount;
    private String description;
    private String status;
    private boolean multiDebtor; // co-optato
    private PaymentOptionType paymentOptionType;
    private LocalDate dueDate;
    private List<InstallmentRequestDTO> installments;

}
