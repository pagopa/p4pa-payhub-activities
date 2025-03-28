package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Lazy
@Service
public class InstallmentServiceImpl implements InstallmentService {
	private final AuthnService authnService;
	private final InstallmentClient installmentClient;

	public InstallmentServiceImpl(AuthnService authnService, InstallmentClient installmentClient) {
		this.authnService = authnService;
		this.installmentClient = installmentClient;
	}

	@Override
	public Optional<InstallmentNoPII> getInstallmentById(Long installmentId) {
		return  Optional.ofNullable(
			installmentClient.findById(installmentId, authnService.getAccessToken())
		);
	}
}
