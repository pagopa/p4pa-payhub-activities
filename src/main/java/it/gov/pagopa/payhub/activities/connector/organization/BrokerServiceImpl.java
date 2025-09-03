package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.BrokerClient;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBroker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Lazy
@Slf4j
@Service
public class BrokerServiceImpl implements BrokerService {
	private final AuthnService authnService;
	private final BrokerClient brokerClient;

	public BrokerServiceImpl(AuthnService authnService, BrokerClient brokerClient) {
		this.authnService = authnService;
		this.brokerClient = brokerClient;
	}

	@Override
	public List<Broker> fetchAll() {
		log.debug("Fetching all Broker records");
		PagedModelBroker pagedModelBroker = brokerClient.fetchAll(authnService.getAccessToken());
		return Objects.requireNonNull(pagedModelBroker.getEmbedded()).getBrokers();
	}

	@Override
	public Broker getBrokerByFiscalCode(String fiscalCode) {
		log.debug("Get Broker by fiscal code: {}", fiscalCode);
		return brokerClient.getByFiscalCode(fiscalCode, authnService.getAccessToken());
	}

	@Override
	public Broker getBrokerById(Long brokerId) {
		log.debug("Get Broker by id: {}", brokerId);
		return brokerClient.getById(brokerId, authnService.getAccessToken());
	}


}
