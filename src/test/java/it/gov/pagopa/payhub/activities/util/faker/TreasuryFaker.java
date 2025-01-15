package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class TreasuryFaker {

	public static Treasury buildTreasuryDTO() {
		return Treasury.builder()
			.treasuryId("treasuryId")
			.billYear("2025")
			.billCode("BILL123")
			.accountCode("ACC456")
			.domainIdCode("DOMAIN789")
			.transactionTypeCode("TYPE01")
			.remittanceCode("REM001")
			.remittanceInformation("Payment details")
			.billAmountCents(100L)
			.billDate(LocalDate.now())
			.receptionDate(OffsetDateTime.now())
			.documentYear("2025")
			.documentCode("DOC789")
			.sealCode("SEAL456")
			.pspLastName("Doe")
			.pspFirstName("John")
			.pspAddress("123 Fake Street")
			.pspPostalCode("12345")
			.pspCity("Faketown")
			.pspFiscalCode("FISCALCODE1234")
			.pspVatNumber("VAT123456")
			.abiCode("ABI123")
			.cabCode("CAB456")
			.accountRegistryCode("REG789")
			.provisionalAe("PROV-AE")
			.provisionalCode("PROV-CODE")
			.ibanCode("IT60X0542811101000000123456")
			.accountTypeCode("C")
			.processCode("PROC123")
			.executionPgCode("EXEC-PG456")
			.transferPgCode("TRANS-PG789")
			.processPgNumber(12345L)
			.regionValueDate(LocalDate.now())
			.organizationId(98765L)
			.iuf("IUF123456")
			.iuv("IUV789123")
			.creationDate(OffsetDateTime.now())
			.updateDate(OffsetDateTime.now())
			.ingestionFlowFileId(56789L)
			.actualSuspensionDate(LocalDate.now())
			.managementProvisionalCode("MAN-PROV123")
			.endToEndId("E2E123456")
			.build();
	}
}
