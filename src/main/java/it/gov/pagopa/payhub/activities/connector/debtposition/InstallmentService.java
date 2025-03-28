package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;

import java.util.Optional;

public interface InstallmentService {
	Optional<InstallmentNoPII> getInstallmentById(Long installmentId);
}
