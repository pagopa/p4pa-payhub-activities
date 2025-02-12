package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelBroker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class BrokerClient {
	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final List<String> DEFAULT_SORT = List.of("asc");

	private final OrganizationApisHolder organizationApisHolder;

	public BrokerClient(OrganizationApisHolder organizationApisHolder) {
		this.organizationApisHolder = organizationApisHolder;
	}

	public PagedModelBroker fetchAll(String accessToken) {
		return organizationApisHolder.getBrokerEntityControllerApi(accessToken).crudGetBrokers(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
	}
}
