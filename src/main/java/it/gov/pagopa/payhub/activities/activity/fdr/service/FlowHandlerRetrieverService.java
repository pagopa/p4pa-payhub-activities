package it.gov.pagopa.payhub.activities.activity.fdr.service;

import it.gov.pagopa.payhub.activities.dao.FlowHandlerDao;
import it.gov.pagopa.payhub.activities.dto.fdr.FlowHandlerDTO;
import it.gov.pagopa.payhub.activities.exception.OperatorNotAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class FlowHandlerRetrieverService {

	private final FlowHandlerDao flowHandlerDao;

	public FlowHandlerRetrieverService(FlowHandlerDao flowHandlerDao) {
		this.flowHandlerDao = flowHandlerDao;
	}

	public FlowHandlerDTO getByFlowId(Long ingestionFlowId) {
		Optional<FlowHandlerDTO> flowHandler = flowHandlerDao.getFlowHandler(ingestionFlowId);

		return flowHandler
			.orElseThrow(() -> new OperatorNotAuthorizedException("Cannot found flowHandler having id: "+ ingestionFlowId));
	}
}
