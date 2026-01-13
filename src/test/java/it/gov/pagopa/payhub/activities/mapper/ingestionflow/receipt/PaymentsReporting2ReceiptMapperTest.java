package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PaymentsReporting2ReceiptMapperTest {
	@InjectMocks
	private PaymentsReporting2ReceiptMapper mapper;

	@Test
	void testMapper() {
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();
		Organization organizationFake = OrganizationFaker.buildOrganizationDTO();
        InstallmentDebtorDTO installmentDebtorDTO = TestUtils.getPodamFactory().manufacturePojo(InstallmentDebtorDTO.class);
		ReceiptWithAdditionalNodeDataDTO receipt = mapper.map2Receipt(paymentsReportingFake, organizationFake, List.of(installmentDebtorDTO));

		TestUtils.checkNotNullFields(receipt, "receiptId", "paymentNote", "officeName", "pspPartitaIva", "paymentMethod", "feeCents", "creationDate", "updateDate", "rtFilePath", "iud", "debtPositionTypeOrgCode", "balance", "updateOperatorExternalId", "updateTraceId");
		TestUtils.checkNotNullFields(receipt.getDebtor(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
		TestUtils.checkNotNullFields(receipt.getPayer(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
		TestUtils.checkNotNullFields(receipt.getTransfers().getFirst(), "companyName", "mbdAttachment", "iban", "metadata");
        Assertions.assertEquals(installmentDebtorDTO.getDebtor(), receipt.getDebtor());
    }

    @Test
    void givenEmptyInstallmentDebtorDTOListWhenMapThenOk() {
        PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();
        Organization organizationFake = OrganizationFaker.buildOrganizationDTO();
        ReceiptWithAdditionalNodeDataDTO receipt = mapper.map2Receipt(paymentsReportingFake, organizationFake, new ArrayList<>());

        TestUtils.checkNotNullFields(receipt, "receiptId", "paymentNote", "officeName", "pspPartitaIva", "paymentMethod", "feeCents", "creationDate", "updateDate", "rtFilePath", "iud", "debtPositionTypeOrgCode", "balance", "updateOperatorExternalId", "updateTraceId");
        TestUtils.checkNotNullFields(receipt.getDebtor(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
        TestUtils.checkNotNullFields(receipt.getPayer(), "location", "civic", "address", "postalCode", "postalCode", "province", "nation", "email");
        TestUtils.checkNotNullFields(receipt.getTransfers().getFirst(), "companyName", "mbdAttachment", "iban", "metadata");
        Assertions.assertEquals(PaymentsReporting2ReceiptMapper.ANONYMOUS_PERSON, receipt.getDebtor().getFullName());
    }
}