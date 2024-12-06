package it.gov.pagopa.payhub.activities.service;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidFlowDataException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for validating flow data, specifically ensuring that the organization
 * details in the flow match the expected organization.
 */
@Lazy
@Service
public class FlowValidatorService {

	/**
	 * Validates that the organization specified in the flow (`CtFlussoRiversamento`) matches
	 * the organization in the ingestion flow file (`IngestionFlowFileDTO`).
	 *
	 * @param ctFlussoRiversamento the flow data object containing information about the transaction flow.
	 * @param ingestionFlowFileDTO the ingestion flow file containing metadata about the ingestion process.
	 * @throws InvalidFlowDataException if the organization details in `ctFlussoRiversamento` do not match
	 *                                  the expected organization in `ingestionFlowFileDTO`.
	 */
	public void validateOrganization(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFileDTO ingestionFlowFileDTO) {
		String fileOrgFiscalcode = ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco();
		String ingestionFlowFileOrgFiscalcode = ingestionFlowFileDTO.getOrg().getOrgFiscalCode();
		if (!fileOrgFiscalcode.equals(ingestionFlowFileOrgFiscalcode)) {
			throw new InvalidFlowDataException("Non matching Organization: " + ingestionFlowFileOrgFiscalcode + "/" + fileOrgFiscalcode);
		}
	}
}
