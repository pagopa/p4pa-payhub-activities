package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Slf4j
@Service
public class InstallmentClient {
	private final DebtPositionApisHolder debtPositionApisHolder;

	public InstallmentClient(DebtPositionApisHolder debtPositionApisHolder) {
		this.debtPositionApisHolder = debtPositionApisHolder;
	}

	public InstallmentNoPII findById(Long installmentId, String accessToken) {
		try {
			return debtPositionApisHolder.getInstallmentNoPiiEntityControllerApi(accessToken).crudGetInstallmentnopii(String.valueOf(installmentId));
		} catch (HttpClientErrorException.NotFound e) {
			log.info("Cannot find Installment having id: {}", installmentId);
			return null;
		}
	}
}
