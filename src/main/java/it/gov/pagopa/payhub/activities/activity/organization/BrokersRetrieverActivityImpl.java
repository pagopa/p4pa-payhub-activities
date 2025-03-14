package it.gov.pagopa.payhub.activities.activity.organization;

import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Slf4j
@Service
public class BrokersRetrieverActivityImpl implements BrokersRetrieverActivity {
	private final BrokerService brokerService;

	public BrokersRetrieverActivityImpl(BrokerService brokerService) {
		this.brokerService = brokerService;
	}

	@Override
	public List<Broker> fetchAllBrokers() {
		log.info("Fetching all brokers");
		return brokerService.fetchAll();
	}
}
