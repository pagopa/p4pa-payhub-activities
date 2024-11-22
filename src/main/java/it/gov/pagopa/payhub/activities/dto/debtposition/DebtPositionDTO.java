package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
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

    private Long id;
    private String iupdOrg;
    private String iupdPagopa;
    private String organizationFiscalCode;
    private String description;
    private String status;
    private String flowId;
    private String numberLineFlow;
    private String codeTypeInstallment;
    private String gpdIUPD;
    private String gpdStatus;
    private OrganizationDTO org;
    private DebtPositionTypeOrgDTO debtPositionTypeOrgDTO;
    private List<PaymentOptionDTO> paymentOption;
}
