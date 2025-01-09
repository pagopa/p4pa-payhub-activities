package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DebtPositionDTO implements Serializable {

    private Long debtPositionId;
    private String iupdOrg;
    private String iupdPagopa;
    private String description;
    private String status;
    private IngestionFlowFileDTO ingestionFlowFile;
    private Long ingestionFlowFileLineNumber;
    private Character gpdStatus;
    private Organization org;
    private DebtPositionTypeOrgDTO debtPositionTypeOrg;
    private List<PaymentOptionDTO> paymentOptions;
}
