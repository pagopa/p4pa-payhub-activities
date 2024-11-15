package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.FlowDTO;

import java.util.List;

public interface FlowDao {

    /**
     * It will return a list of FlowDTO of the organization
     **/
    List<FlowDTO> getFlowsByOrganization(Long organizationId, boolean isSpontaneous);
}
