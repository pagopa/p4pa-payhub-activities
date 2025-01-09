package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;

import java.math.BigDecimal;
import java.util.Date;

public class TreasuryFaker {

	public static TreasuryDTO buildTreasuryDTO() {
		return TreasuryDTO.builder()
			.treasuryId(1L)
			.billYear("2025")
			.billCode("BILL123")
			.accountCode("ACC456")
			.domainIdCode("DOMAIN789")
			.transactionTypeCode("TYPE01")
			.remittanceCode("REM001")
			.remittanceInformation("Payment details")
			.billIpNumber(BigDecimal.valueOf(1.00D))
			.billDate(new Date())
			.receptionDate(new Date())
			.documentYear("2025")
			.documentCode("DOC789")
			.sealCode("SEAL456")
			.lastName("Doe")
			.firstName("John")
			.address("123 Fake Street")
			.postalCode("12345")
			.city("Faketown")
			.fiscalCode("FISCALCODE1234")
			.vatNumber("VAT123456")
			.abiCode("ABI123")
			.cabCode("CAB456")
			.accountRegistryCode("REG789")
			.provisionalAe("PROV-AE")
			.provisionalCode("PROV-CODE")
			.ibanCode("IT60X0542811101000000123456")
			.accountTypeCode('C')
			.processCode("PROC123")
			.executionPgCode("EXEC-PG456")
			.transferPgCode("TRANS-PG789")
			.processPgNumber(12345L)
			.regionValueDate(new Date())
			.organizationId(98765L)
			.iuf("IUF123456")
			.iuv("IUV789123")
			.creationDate(new Date())
			.lastUpdateDate(new Date())
			.isRegularized(true)
			.ingestionFlowFileId(56789L)
			.actualSuspensionDate(new Date())
			.managementProvisionalCode("MAN-PROV123")
			.endToEndId("E2E123456")
			.build();
	}
}
