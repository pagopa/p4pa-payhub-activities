package it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform;

import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousForm;
import it.gov.pagopa.pu.debtposition.dto.generated.SpontaneousFormStructure;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

@Lazy
@Service
@Slf4j
public class SpontaneousFormHandlerService {

	private final SpontaneousFormService spontaneousFormService;

	public SpontaneousFormHandlerService(SpontaneousFormService spontaneousFormService) {
		this.spontaneousFormService = spontaneousFormService;
	}

	/**
	 * Retrieves or creates a spontaneous form.
	 *
	 * @param organizationId the organization ID
	 * @param row the DTO containing spontaneous form data
	 * @return the ID of the existing or newly created spontaneous form, or nullif creation fails
	 */
	public Long handleSpontaneousForm(Long organizationId, DebtPositionTypeOrgIngestionFlowFileDTO row) {

		SpontaneousForm existingForm = spontaneousFormService.findByOrganizationIdAndCode(organizationId, row.getSpontaneousFormCode());

		if (existingForm != null) {
			return existingForm.getSpontaneousFormId();
		}

		try {
			SpontaneousForm newForm = SpontaneousForm.builder()
				.organizationId(organizationId)
				.code(row.getSpontaneousFormCode())
				.structure(new JsonMapper()
					.readValue(row.getSpontaneousFormStructure(), SpontaneousFormStructure.class))
				.dictionary(null)
				.build();

			return Optional.ofNullable(spontaneousFormService.createSpontaneousForm(newForm))
				.map(SpontaneousForm::getSpontaneousFormId)
				.orElse(null);
		} catch (IllegalArgumentException | JacksonException e) {
			String errorMessage = "Error parsing spontaneous form JSON structure for code "+ row.getSpontaneousFormCode() + ": " + ExceptionUtils.getRootCauseMessage(e);
			log.error(errorMessage, e);
			throw new InvalidValueException(errorMessage);
		}
	}
}
