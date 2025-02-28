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
class PaymentsReporting2ReceiptMapperTest {
	@InjectMocks
	private PaymentsReporting2ReceiptMapper mapper;

	@Test
	void testMapper() {
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();
		ReceiptWithAdditionalNodeDataDTO receipt = mapper.map2DummyReceipt(paymentsReportingFake, "fiscalCodePA");

		TestUtils.checkNotNullFields(receipt, "receiptId", "paymentNote", "officeName", "pspPartitaIva", "paymentMethod", "feeCents", "creationDate", "updateDate");
		TestUtils.checkNotNullFields(receipt.getDebtor(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
		TestUtils.checkNotNullFields(receipt.getPayer(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
		TestUtils.checkNotNullFields(receipt.getTransfers().getFirst(), "companyName", "mbdAttachment", "iban", "metadata");
	}
}