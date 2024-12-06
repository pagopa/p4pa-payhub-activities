package it.gov.pagopa.payhub.activities.service;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidFlowDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlowValidatorServiceTest {

	private CtFlussoRiversamento ctFlussoRiversamento;

	private FlowValidatorService service;

	@BeforeEach
	void setup() {
		service = new FlowValidatorService();
		CtIdentificativoUnivocoPersonaG ctIdentificativoUnivocoPersonaG = new CtIdentificativoUnivocoPersonaG();
		ctIdentificativoUnivocoPersonaG.setCodiceIdentificativoUnivoco("80010020011");
		CtIstitutoRicevente istitutoRicevente = new CtIstitutoRicevente();
		istitutoRicevente.setIdentificativoUnivocoRicevente(ctIdentificativoUnivocoPersonaG);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIstitutoRicevente(istitutoRicevente);
	}

	@Test
	void givenDataWhenValidateThenSuccess() {
		//given
		IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileDTO.builder()
			.org(OrganizationDTO.builder()
				.orgFiscalCode("80010020011")
				.build())
			.build();
		// when then
		assertDoesNotThrow(() -> service.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO));
	}

	@Test
	void givenDataWhenValidateThenThrowInvalidFlowDataException() {
		//given
		IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileDTO.builder()
			.org(OrganizationDTO.builder()
				.orgFiscalCode("80010020010")
				.build())
			.build();
		// when then
		assertThrows(InvalidFlowDataException.class,
			() -> service.validateOrganization(ctFlussoRiversamento, ingestionFlowFileDTO), "Invalid Organization");
	}
}
