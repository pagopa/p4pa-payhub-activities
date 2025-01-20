package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferClassificationStoreServiceTest {
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	@Mock
	private ClassificationService classificationServiceMock;

	private TransferClassificationStoreService service;

	@BeforeEach
	void setUp() {
		service = new TransferClassificationStoreService(classificationServiceMock);
	}

	@Test
	void whenSaveAllThenReturnSavedList() {
		// Arrange
		List<ClassificationsEnum> classifications = List.of(ClassificationsEnum.RT_IUF_TES);
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(123L)
			.iuv("01011112222333345")
			.iur("IUR")
			.transferIndex(1)
			.build();
		List<Classification> dtoList = classifications.stream()
			.map(classification -> Classification.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(transferDTO.getTransferId())
				.paymentsReportingId(paymentsReportingDTO.getPaymentsReportingId())
				.treasuryId(treasuryDTO.getTreasuryId())
				.iuf(paymentsReportingDTO.getIuf())
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.label(String.valueOf(classification))
				.build())
			.toList();
		when(classificationServiceMock.saveAll(dtoList)).thenReturn(dtoList.size());

		// Act & Assert
		assertDoesNotThrow(() ->
			service.saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryDTO, classifications));
	}
}