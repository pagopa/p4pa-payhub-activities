package it.gov.pagopa.payhub.activities.service;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidFlowDataException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class FlowValidatorService {

	public void validateOrganization(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFileDTO ingestionFlowFileDTO) {
		if (!ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco()
			.equals(ingestionFlowFileDTO.getOrg().getOrgFiscalCode())) {
			throw new InvalidFlowDataException("Invalid Organization");
		}
	}
}
