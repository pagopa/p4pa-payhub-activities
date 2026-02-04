package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.pu.debtposition.dto.generated.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

	@Override
	public void updateDueDate(Long installmentId, LocalDate dueDate) {
		installmentClient.updateDueDate(installmentId, dueDate, authnService.getAccessToken());
	}

	@Override
	public void updateStatusAndSyncStatus(Long installmentId, InstallmentStatus status, InstallmentSyncStatus syncStatus) {
		installmentClient.updateStatusAndStatusSync(installmentId, status, syncStatus, authnService.getAccessToken());
	}

	@Override
	public CollectionModelInstallmentNoPII getInstallmentsByOrgIdAndIudAndStatus(Long orgId, String iud, List<InstallmentStatus> installmentStatuses) {
		return installmentClient.findCollectionByOrganizationIdAndIudAndStatus(orgId, iud, installmentStatuses, authnService.getAccessToken());
	}

	@Override
	public void updateIunByDebtPositionId(Long debtPositionId, String iun) {
		installmentClient.updateIunByDebtPositionId(debtPositionId, iun, authnService.getAccessToken());
	}

	@Override
	public List<InstallmentDTO> getByOrganizationIdAndReceiptId(Long organizationId, Long receiptId,
			List<DebtPositionOrigin> debtPositionOrigin) {
		return installmentClient.getByOrganizationIdAndReceiptId(organizationId, receiptId, debtPositionOrigin,
				authnService.getAccessToken());
	}

    @Override
    public List<InstallmentDebtorDTO> findByIuvOrNav(String iuvOrNav, String xFiscalCode, Long organizationId, List<InstallmentStatus> statuses) {
        return installmentClient.findByIuvOrNav(iuvOrNav, xFiscalCode, organizationId, statuses, authnService.getAccessToken());
    }

}
