package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFlowFileDataException;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingIngestionFlowFileValidatorServiceTest {

	@Mock
	private OrganizationService organizationService;

	private CtFlussoRiversamento ctFlussoRiversamento;

	private PaymentsReportingIngestionFlowFileValidatorService service;

	@BeforeEach
	void setup() {
		service = new PaymentsReportingIngestionFlowFileValidatorService(organizationService);
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
		IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile()
			.organizationId(1L);

		Organization organization = OrganizationFaker.buildOrganizationDTO()
			.organizationId(1L)
			.orgFiscalCode("80010020011");

		// when then
		when(organizationService.getOrganizationById(1L)).thenReturn(java.util.Optional.of(organization));
		assertDoesNotThrow(() -> service.validateData(ctFlussoRiversamento, ingestionFlowFileDTO));
	}

	@Test
	void givenInvalidOrganizationWhenValidateThenThrowInvalidIngestionFlowFileDataException() {
		//given
		IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile()
				.organizationId(1L);

		Organization organization = OrganizationFaker.buildOrganizationDTO()
				.organizationId(1L)
				.orgFiscalCode("80010023");
		// when then
		when(organizationService.getOrganizationById(1L)).thenReturn(java.util.Optional.of(organization));
		assertThrows(InvalidIngestionFlowFileDataException.class,
			() -> service.validateData(ctFlussoRiversamento, ingestionFlowFileDTO), "Invalid Organization");
	}
}
