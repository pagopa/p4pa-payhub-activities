package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DebtPositionRequestDTO {

    private Long debtPositionId;
    private OrganizationDTO org;
    private DebtPositionTypeOrgDTO debtPositionTypeOrg;
    private List<PaymentOptionRequestDTO> paymentOptions;
}
