package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsMapperTest {

	@InjectMocks
	private TreasuryXlsMapper treasuryXlsMapperMock;

	private final PodamFactory podamFactory = TestUtils.getPodamFactory();

	@Test
	void givenTreasuryXlsIngestionFlowFileDTOWithMinusSignWhenMapThenBuildTreasuryCorrectly() {
		TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
				TreasuryXlsIngestionFlowFileDTO.class);

		dto.setAbiCode("1234");
		dto.setCabCode("1234");
		dto.setAccountCode("1234567890");
		dto.setCurrency("EUR");
		dto.setBillDate(LOCAL_DATE_2024);
		dto.setRegionValueDate(LOCAL_DATE_2024);
		dto.setBillAmountCents(1235L);
		dto.setSign("-");
		dto.setRemittanceCode("1234");
		dto.setCheckNumber("1234567890");
		dto.setBankReference("1234567890");
		dto.setClientReference("1234567890");
		dto.setRemittanceDescription("Data Ordine: 01/01/2020; Descr");
		dto.setExtendedRemittanceDescription("Data Ordine: 01/01/2020; Descrizione Ordinante: XYZ PRIVATE BANKING SPA                          PIAZZA SAN :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM");

		IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
		ingestionFlowFile.setOrganizationId(123L);
		ingestionFlowFile.setIngestionFlowFileId(1L);
		Treasury result = treasuryXlsMapperMock.map(dto, ingestionFlowFile);

		Assertions.assertNotNull(result);

		assertEquals("1234", result.getAbiCode());
		assertEquals("1234", result.getCabCode());
		assertEquals("1234567890", result.getAccountCode());
		assertEquals(LOCAL_DATE_2024, result.getBillDate());
		assertEquals("2024", result.getBillYear());
		assertEquals(LOCAL_DATE_2024, result.getRegionValueDate());
		assertEquals(-1235, result.getBillAmountCents());
		assertEquals("1234", result.getRemittanceCode());
		assertEquals("1234567890", result.getCheckNumber());
		assertEquals("1234567890", result.getBankReference());
		assertEquals("1234567890", result.getClientReference());
		assertEquals("Data Ordine: 01/01/2020; Descr", result.getRemittanceDescription());
		assertEquals(TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF), result.getIuf());
		assertEquals(TreasuryUtils.getRemitterDescription(dto.getExtendedRemittanceDescription()), result.getPspLastName());
		assertEquals(TreasuryUtils.generateBillCode(dto.getBillDate(), TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF)), result.getBillCode());
		assertEquals(ORG_ISTAT_CODE_DEFAULT, result.getOrgIstatCode());
		assertEquals(ORG_BT_CODE_DEFAULT, result.getOrgBtCode());
		assertEquals(1, result.getIngestionFlowFileId());
		assertEquals(123, result.getOrganizationId());
		assertEquals(TreasuryOrigin.TREASURY_XLS, result.getTreasuryOrigin());

		TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateTraceId", "treasuryId",
				"updateOperatorExternalId", "links", "receptionDate", "actualSuspensionDate", "iuv", "domainIdCode",
				"transactionTypeCode", "documentYear", "documentCode", "sealCode", "pspAddress", "pspPostalCode",
				"pspCity", "pspFirstName", "pspFiscalCode", "pspVatNumber", "ibanCode", "accountRegistryCode",
				"provisionalAe", "provisionalCode", "accountTypeCode", "processCode", "executionPgCode",
				"transferPgCode", "processPgNumber", "managementProvisionalCode", "endToEndId", "regularized"
		);
	}

	@Test
	void givenTreasuryXlsIngestionFlowFileDTOWithPlusSignWhenMapThenBuildTreasuryCorrectly() {
		TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
				TreasuryXlsIngestionFlowFileDTO.class);

		dto.setAbiCode("1234");
		dto.setCabCode("1234");
		dto.setAccountCode("1234567890");
		dto.setCurrency("EUR");
		dto.setBillDate(LOCAL_DATE_2024);
		dto.setRegionValueDate(LOCAL_DATE_2024);
		dto.setBillAmountCents(1235L);
		dto.setSign("+");
		dto.setRemittanceCode("1234");
		dto.setCheckNumber("1234567890");
		dto.setBankReference("1234567890");
		dto.setClientReference("1234567890");
		dto.setRemittanceDescription("Data Ordine: 01/01/2020; Descr");
		dto.setExtendedRemittanceDescription("Data Ordine: 01/01/2020; Descrizione Ordinante: XYZ PRIVATE BANKING SPA                          PIAZZA SAN :BI2:ABCKITYYXXX :BE1:IPA TEST 2 :IB1:IT1234567890123456789012345 :IB2:IT1234567890123456789012346 :TID:1234567890123456 :DTE:123456 :DTN:IPA TEST 2 :ERI:EUR 000000000012345 :IM2:000000000012345 :MA2:EU R :RI3:/PUR/LGPE-RIVERSAMENTO/URI/2024-07-26PPAYITR1XXX-S2024072601 :SEC:CASH :OR1:XYZ PRIVATE BANKING SPA PIAZZA SAN  123 00123 TORINO T :TR1:XYZ CBILL PUBBLICA AMM");

		IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
		ingestionFlowFile.setOrganizationId(123L);
		ingestionFlowFile.setIngestionFlowFileId(1L);
		Treasury result = treasuryXlsMapperMock.map(dto, ingestionFlowFile);

		Assertions.assertNotNull(result);

		assertEquals("1234", result.getAbiCode());
		assertEquals("1234", result.getCabCode());
		assertEquals("1234567890", result.getAccountCode());
		assertEquals(LOCAL_DATE_2024, result.getBillDate());
		assertEquals("2024", result.getBillYear());
		assertEquals(LOCAL_DATE_2024, result.getRegionValueDate());
		assertEquals(1235, result.getBillAmountCents());
		assertEquals("1234", result.getRemittanceCode());
		assertEquals("1234567890", result.getCheckNumber());
		assertEquals("1234567890", result.getBankReference());
		assertEquals("1234567890", result.getClientReference());
		assertEquals("Data Ordine: 01/01/2020; Descr", result.getRemittanceDescription());
		assertEquals(TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF), result.getIuf());
		assertEquals(TreasuryUtils.getRemitterDescription(dto.getExtendedRemittanceDescription()), result.getPspLastName());
		assertEquals(TreasuryUtils.generateBillCode(dto.getBillDate(), TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF)), result.getBillCode());
		assertEquals(ORG_ISTAT_CODE_DEFAULT, result.getOrgIstatCode());
		assertEquals(ORG_BT_CODE_DEFAULT, result.getOrgBtCode());
		assertEquals(1, result.getIngestionFlowFileId());
		assertEquals(123, result.getOrganizationId());
		assertEquals(TreasuryOrigin.TREASURY_XLS, result.getTreasuryOrigin());

		TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateTraceId", "treasuryId",
				"updateOperatorExternalId", "links", "receptionDate", "actualSuspensionDate", "iuv", "domainIdCode",
				"transactionTypeCode", "documentYear", "documentCode", "sealCode", "pspAddress", "pspPostalCode",
				"pspCity", "pspFirstName", "pspFiscalCode", "pspVatNumber", "ibanCode", "accountRegistryCode",
				"provisionalAe", "provisionalCode", "accountTypeCode", "processCode", "executionPgCode",
				"transferPgCode", "processPgNumber", "managementProvisionalCode", "endToEndId", "regularized"
		);
	}
}