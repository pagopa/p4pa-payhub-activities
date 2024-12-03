package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;

import java.util.List;

public interface IngestionFlowFileDao {

    /**
     * It will return a list of IngestionFlowFileDTO of the organization
     **/
    List<IngestionFlowFileDTO> getIngestionFlowFilesByOrganization(Long organizationId, boolean isSpontaneous);
}
