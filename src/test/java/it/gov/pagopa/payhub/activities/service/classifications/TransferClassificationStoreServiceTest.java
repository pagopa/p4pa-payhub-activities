package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

class TransferClassificationStoreServiceTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	@Mock
	private ClassificationDao classificationDaoMock;

	private TransferClassificationStoreService service;

	@BeforeEach
	void setUp() {
		service = new TransferClassificationStoreService(classificationDaoMock);
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
		List<ClassificationDTO> dtoList = classifications.stream()
			.map(classification -> ClassificationDTO.builder()
				.organizationId(transferSemanticKeyDTO.getOrgId())
				.transferId(transferDTO.getTransferId())
				.paymentReportingId(paymentsReportingDTO.getPaymentsReportingId())
				.treasuryId(treasuryDTO.getTreasuryId())
				.iuf(paymentsReportingDTO.getIuf())
				.iuv(transferSemanticKeyDTO.getIuv())
				.iur(transferSemanticKeyDTO.getIur())
				.transferIndex(transferSemanticKeyDTO.getTransferIndex())
				.classificationsEnum(classification)
				.build())
			.toList();
		when(classificationDaoMock.saveAll(dtoList)).thenReturn(dtoList);

		// Act & Assert
		assertDoesNotThrow(() ->
			service.saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryDTO, classifications));
	}
}