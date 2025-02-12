package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFlowFileDataException;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for validating flow data, specifically ensuring that the organization
 * details in the flow match the expected organization.
 */
@Lazy
@Service
public class PaymentsReportingIngestionFlowFileValidatorService {

	private final OrganizationService organizationService;

    public PaymentsReportingIngestionFlowFileValidatorService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
	 * Validates that the organization specified in the flow (`CtFlussoRiversamento`) matches
	 * the organization in the ingestion flow file (`IngestionFlowFile`).
	 *
	 * @param ctFlussoRiversamento the flow data object containing information about the transaction flow.
	 * @param ingestionFlowFileDTO the ingestion flow file containing metadata about the ingestion process.
	 * @throws InvalidIngestionFlowFileDataException if the organization details in `ctFlussoRiversamento` do not match
	 *                                  the expected organization in `ingestionFlowFileDTO`.
	 */
	public void validateData(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFile ingestionFlowFileDTO) {
		String fileOrgFiscalcode = ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco();
		Organization organization = organizationService.getOrganizationById(ingestionFlowFileDTO.getOrganizationId())
				.orElseThrow(() -> new InvalidIngestionFlowFileDataException("Organization not found: " + ingestionFlowFileDTO.getOrganizationId()));
		String ingestionFlowFileOrgFiscalcode = organization.getOrgFiscalCode();
		if (!fileOrgFiscalcode.equals(ingestionFlowFileOrgFiscalcode)) {
			throw new InvalidIngestionFlowFileDataException("Non matching Organization: " + ingestionFlowFileOrgFiscalcode + "/" + fileOrgFiscalcode);
		}
	}
}
