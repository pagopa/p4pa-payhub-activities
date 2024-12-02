package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.PersonRequestDTO;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
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
public class InstallmentRequestDTO {

    private LocalDate dueDate;
    private Long amount;
    private String remittanceInformation;

    private List<TransferDTO> transfers;
    private PersonRequestDTO debtor;
}
