package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

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
		log.debug("Update due date for installmentId: {}", installmentId);
		debtPositionApisHolder.getInstallmentsEntityExtendedControllerApi(accessToken).updateDueDate(installmentId, dueDate);
	}

	public void updateStatusAndStatusSync(Long installmentId, InstallmentStatus status, InstallmentSyncStatus syncStatus, String accessToken){
		log.debug("Update status and syncStatus for installmentId: {}", installmentId);
		InstallmentStatus syncStatusFrom=null;
		InstallmentStatus syncStatusTo=null;
		if(syncStatus!=null){
		 syncStatusFrom = syncStatus.getSyncStatusFrom();
		 syncStatusTo = syncStatus.getSyncStatusTo();
		}
		debtPositionApisHolder.getInstallmentsEntityExtendedControllerApi(accessToken).updateStatusAndToSyncStatus(installmentId, status, syncStatusFrom, syncStatusTo);
	}

    public List<InstallmentDTO> getInstallmentsByOrganizationIdAndNav(String accessToken, Long organizationId, String nav, List<DebtPositionOrigin> debtPositionOrigins) {
        return debtPositionApisHolder.getInstallmentApi(accessToken).getInstallmentsByOrganizationIdAndNav(organizationId, nav, debtPositionOrigins);
    }

	public CollectionModelInstallmentNoPII findCollectionByOrganizationIdAndIudAndStatus(Long orgId, String iud, List<InstallmentStatus> installmentStatuses,String accessToken) {
			return debtPositionApisHolder.getInstallmentNoPiiSearchControllerApi(accessToken).crudInstallmentsGetByOrganizationIdAndIudAndStatus(orgId, iud, installmentStatuses);
	}

	public void updateIunByDebtPositionId(Long debptPositionId, String iun, String accessToken) {
		log.debug("Update IUN for debtPositionId: {}", debptPositionId);
		debtPositionApisHolder.getInstallmentsEntityExtendedControllerApi(accessToken).updateIunByDebtPositionId(debptPositionId, iun);
	}

}
