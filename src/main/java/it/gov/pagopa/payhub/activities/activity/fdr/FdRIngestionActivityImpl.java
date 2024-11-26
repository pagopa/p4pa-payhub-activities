package it.gov.pagopa.payhub.activities.activity.fdr;

import it.gov.pagopa.payhub.activities.activity.fdr.service.FlowHandlerRetrieverService;
import it.gov.pagopa.payhub.activities.dto.fdr.FdRIngestionActivityResult;
import it.gov.pagopa.payhub.activities.dto.fdr.FlowHandlerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FdRIngestionActivityImpl implements FdRIngestionActivity {
	private final FlowHandlerRetrieverService flowHandlerRetrieverService;

	public FdRIngestionActivityImpl(FlowHandlerRetrieverService flowHandlerRetrieverService) {
		this.flowHandlerRetrieverService = flowHandlerRetrieverService;
	}

	@Override
	public FdRIngestionActivityResult processFile(String ingestionFlowId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			FlowHandlerDTO flowHandlerDTO = flowHandlerRetrieverService.getByFlowId(Long.valueOf(ingestionFlowId));
			// Creazione directory temporanea

			// Validazione file


			// Decifratura


			// Controllo sicurezza zip


			// Unzip


			// Validazione XSD e unmarshalling


			// Lettura e processazione file


			// Sposta file in archivio

		} catch (Exception e) {
			log.error("Errore durante il processamento del file {}: {}", ingestionFlowId, e.getMessage());
			success = false;

			// Sposta file nella directory di errore
		} finally {
			// Pulizia directory temporanea


			return new FdRIngestionActivityResult(iufList, success);
		}

	}
}
