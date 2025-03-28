package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSyncStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

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

	public void updateDueDate(Long installmentId, LocalDate dueDate, String accessToken){
		log.info("Update due date for installmentId: {}", installmentId);
		debtPositionApisHolder.getInstallmentNoPiiSearchControllerApi(accessToken).crudInstallmentsUpdateDueDate(installmentId, dueDate);
	}

	public void updateStatusAndStatusSync(Long installmentId, InstallmentStatus status, InstallmentSyncStatus syncStatus, String accessToken){
		log.info("Update status and syncStatus for installmentId: {}", installmentId);
		debtPositionApisHolder.getInstallmentNoPiiSearchControllerApi(accessToken).crudInstallmentsUpdateStatusAndToSyncStatus(installmentId, status, syncStatus);
	}
}
