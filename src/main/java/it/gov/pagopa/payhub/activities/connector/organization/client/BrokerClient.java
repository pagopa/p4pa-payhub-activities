package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBroker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class BrokerClient {
	private final OrganizationApisHolder organizationApisHolder;

	public BrokerClient(OrganizationApisHolder organizationApisHolder) {
		this.organizationApisHolder = organizationApisHolder;
	}

	public PagedModelBroker fetchAll(String accessToken) {
		return organizationApisHolder.getBrokerEntityControllerApi(accessToken).crudGetBrokers(0, 2_000, null);
	}

	public Broker getByFiscalCode(String fiscalCode, String accessToken) {
		return organizationApisHolder.getBrokerSearchControllerApi(accessToken).crudBrokersFindByBrokeredOrgFiscalCode(fiscalCode);
	}

	public Broker getById(Long brokerId, String accessToken) {
		return organizationApisHolder.getBrokerEntityControllerApi(accessToken).crudGetBroker(brokerId.toString());
	}
}
