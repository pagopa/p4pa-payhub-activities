package it.gov.pagopa.payhub.activities.activity.reportingflow;

import it.gov.pagopa.payhub.activities.activity.reportingflow.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.FdRIngestionActivityResult;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FdRIngestionActivityImpl implements FdRIngestionActivity {
	private final IngestionFlowRetrieverService ingestionFlowRetrieverService;

	public FdRIngestionActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService) {
		this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
	}

	@Override
	public FdRIngestionActivityResult processFile(String ingestionFlowId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			IngestionFlowDTO ingestionFlowDTO = ingestionFlowRetrieverService.getByFlowId(Long.valueOf(ingestionFlowId));
			// Creazione directory temporanea

			// Validazione file


			// Decifratura


			// Controllo sicurezza zip


			// Unzip


			// Validazione XSD e unmarshalling


			// Lettura e processazione file


			// Sposta file in archivio

		} catch (Exception e) {
			log.error("Error during IngestionActivity flowId {} due to: {}", ingestionFlowId, e.getMessage());
			success = false;

			// Sposta file nella directory di errore
		} finally {
			// Pulizia directory temporanea


			return new FdRIngestionActivityResult(iufList, success);
		}

	}
}
