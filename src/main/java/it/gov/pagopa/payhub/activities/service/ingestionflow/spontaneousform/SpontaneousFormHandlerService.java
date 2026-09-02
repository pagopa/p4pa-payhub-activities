package it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform;

import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.common.InvalidValueException;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeConflictException;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeInvalidValueException;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
	 * @return the ID of the existing or newly created spontaneous form, or null if creation fails
	 */
	public Long handleSpontaneousForm(Long organizationId, DebtPositionTypeOrgIngestionFlowFileDTO row) {
		if(StringUtils.isEmpty(row.getSpontaneousFormCode())){
			return null;
		}

		try {
			SpontaneousForm newForm = SpontaneousForm.builder()
				.organizationId(organizationId)
				.code(row.getSpontaneousFormCode())
				.structure(new JsonMapper()
					.readTree(row.getSpontaneousFormStructure()))
				.dictionary(null)
				.build();

			return Optional.ofNullable(spontaneousFormService.matchOrSaveSpontaneousForm(newForm))
				.map(SpontaneousForm::getSpontaneousFormId)
				.orElse(null);
		} catch (RestInvokeInvalidValueException | RestInvokeConflictException e) {
			throw new InvalidValueException(e.getCode(), e.getMessage());
		} catch (JacksonException je) {
			String errorMessage = "Error parsing spontaneous form JSON structure for code "+ row.getSpontaneousFormCode() + ": " + ExceptionUtils.getRootCauseMessage(je);
			throw new InvalidValueException(errorMessage);
		}
	}
}
