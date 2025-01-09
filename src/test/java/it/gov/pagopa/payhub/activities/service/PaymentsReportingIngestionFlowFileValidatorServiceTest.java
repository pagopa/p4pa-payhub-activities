package it.gov.pagopa.payhub.activities.service;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFlowFileDataException;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentsReportingIngestionFlowFileValidatorServiceTest {

	private CtFlussoRiversamento ctFlussoRiversamento;

	private PaymentsReportingIngestionFlowFileValidatorService service;

	@BeforeEach
	void setup() {
		service = new PaymentsReportingIngestionFlowFileValidatorService();
		CtIdentificativoUnivocoPersonaG ctIdentificativoUnivocoPersonaG = new CtIdentificativoUnivocoPersonaG();
		ctIdentificativoUnivocoPersonaG.setCodiceIdentificativoUnivoco("80010020011");
		CtIstitutoRicevente istitutoRicevente = new CtIstitutoRicevente();
		istitutoRicevente.setIdentificativoUnivocoRicevente(ctIdentificativoUnivocoPersonaG);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIstitutoRicevente(istitutoRicevente);
	}

	@Test
	void givenValidDataWhenValidateThenSuccess() {
		//given
		IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileDTO.builder()
			.org(Organization.builder()
				.orgFiscalCode("80010020011")
				.build())
			.build();
		// when then
		assertDoesNotThrow(() -> service.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO));
	}

	@Test
	void givenInvalidOrganizationWhenValidateThenThrowInvalidIngestionFlowFileDataException() {
		//given
		IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileDTO.builder()
			.org(Organization.builder()
				.orgFiscalCode("80010020010")
				.build())
			.build();
		// when then
		assertThrows(InvalidIngestionFlowFileDataException.class,
			() -> service.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO), "Invalid Organization");
	}
}
