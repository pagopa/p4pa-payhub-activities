package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.fdr.FlowHandlerDTO;

import java.util.Optional;

public interface FlowHandlerDao {

	/**
	 *  * It will return the requested FlowHandlerDTO entity from its id
	 * */
	Optional<FlowHandlerDTO> getFlowHandler(Long ingestionFlowId);
}
