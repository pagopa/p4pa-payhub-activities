package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InstallmentService {
	Optional<InstallmentNoPII> getInstallmentById(Long installmentId);
	void updateDueDate(Long installmentId, LocalDate dueDate);
	void updateStatusAndSyncStatus(Long installmentId, InstallmentStatus status, InstallmentSyncStatus syncStatus);
	CollectionModelInstallmentNoPII getInstallmentsByOrgIdAndIudAndStatus(Long orgid, String iud, List<InstallmentStatus> installmentStatuses);
	void updateIunByDebtPositionId(Long debtPositionId, String iun);
	List<InstallmentDTO> getByOrganizationIdAndReceiptId(Long organizationId, Long receiptId, List<DebtPositionOrigin> debtPositionOrigin);
    List<InstallmentDebtorDTO> findByIuvOrNav(String iuvOrNav, String xFiscalCode, Long organizationId);
}
