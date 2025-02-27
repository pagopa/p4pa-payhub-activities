package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DummyReceiptMapperTest {
	@InjectMocks
	private DummyReceiptMapper mapper;

	@Test
	void testMapper() {
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();
		ReceiptWithAdditionalNodeDataDTO receipt = mapper.map2DummyReceipt(paymentsReportingFake);

		TestUtils.checkNotNullFields(receipt, "creationDate", "standin", "feeCents", "paymentMethod", "pspPartitaIva", "payer", "officeName", "receiptId", "receiptOrigin", "transfers", "updateDate", "paymentNote");
		TestUtils.checkNotNullFields(receipt.getDebtor(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
	}
}