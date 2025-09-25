package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste;

import static it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper.POSTE_DATE_FORMAT;
import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class TreasuryPosteMapperTest {

  @InjectMocks
  private TreasuryPosteMapper treasuryPosteMapper;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void mapWithDebitAmountCents() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "IUF123456";
    String billCode = "BILL123";

    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
        TreasuryPosteIngestionFlowFileDTO.class);
    dto.setRegionValueDate(LOCALDATE.format(POSTE_DATE_FORMAT));
    dto.setDebitBillAmount(BigDecimal.valueOf(50L));
    dto.setCreditBillAmount(null);

    IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
    ingestionFlowFile.setOrganizationId(123L);
    ingestionFlowFile.setIngestionFlowFileId(1L);
    var result = treasuryPosteMapper.map(dto, iban, iuf, billCode, LOCALDATE, ingestionFlowFile);

    Assertions.assertNotNull(result);
    assertEquals(TreasuryOrigin.TREASURY_POSTE, result.getTreasuryOrigin());
    assertEquals(-5000L, result.getBillAmountCents());
    checkNotNullFields(result, "creationDate", "updateDate", "updateTraceId", "treasuryId", "updateOperatorExternalId", "links", "iuv", "domainIdCode",
        "receptionDate", "actualSuspensionDate", "checkNumber", "clientReference", "bankReference", "transactionTypeCode", "documentYear", "documentCode", "sealCode", "pspFirstName", "pspAddress",
        "pspPostalCode", "pspCity", "pspFiscalCode", "pspVatNumber", "abiCode", "cabCode", "ibanCode", "accountRegistryCode", "provisionalAe", "provisionalCode", "accountTypeCode", "processCode", "executionPgCode",
        "transferPgCode", "processPgNumber", "managementProvisionalCode", "endToEndId", "clientReference", "bankReference", "regularized");
  }

  @Test
  void mapWithCreditAmountCents() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "IUF123456";
    String billCode = "BILL123";

    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
        TreasuryPosteIngestionFlowFileDTO.class);
    dto.setRegionValueDate(LOCALDATE.format(POSTE_DATE_FORMAT));
    dto.setDebitBillAmount(null);
    dto.setCreditBillAmount(BigDecimal.valueOf(50L));

    IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
    ingestionFlowFile.setOrganizationId(123L);
    ingestionFlowFile.setIngestionFlowFileId(1L);
    var result = treasuryPosteMapper.map(dto, iban, iuf, billCode, LOCALDATE, ingestionFlowFile);

    Assertions.assertNotNull(result);
    assertEquals(TreasuryOrigin.TREASURY_POSTE, result.getTreasuryOrigin());
    assertEquals(5000L, result.getBillAmountCents());
    checkNotNullFields(result, "creationDate", "updateDate", "updateTraceId", "treasuryId", "updateOperatorExternalId", "links", "iuv", "domainIdCode",
        "receptionDate", "actualSuspensionDate", "checkNumber", "clientReference", "bankReference", "transactionTypeCode", "documentYear", "documentCode", "sealCode", "pspFirstName", "pspAddress",
        "pspPostalCode", "pspCity", "pspFiscalCode", "pspVatNumber", "abiCode", "cabCode", "ibanCode", "accountRegistryCode", "provisionalAe", "provisionalCode", "accountTypeCode", "processCode", "executionPgCode",
        "transferPgCode", "processPgNumber", "managementProvisionalCode", "endToEndId", "clientReference", "bankReference", "regularized");
  }

  @Test
  void mapWithRegionValueDateNull() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "IUF123456";
    String billCode = "BILL123";

    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
        TreasuryPosteIngestionFlowFileDTO.class);
    dto.setRegionValueDate(null);

    IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
    ingestionFlowFile.setOrganizationId(123L);
    ingestionFlowFile.setIngestionFlowFileId(1L);
    var result = treasuryPosteMapper.map(dto, iban, iuf, billCode, LOCALDATE, ingestionFlowFile);

    Assertions.assertNotNull(result);
    assertNull(result.getRegionValueDate());
    assertEquals(TreasuryOrigin.TREASURY_POSTE, result.getTreasuryOrigin());
    checkNotNullFields(result, "creationDate", "updateDate", "updateTraceId", "treasuryId", "updateOperatorExternalId", "links", "iuv", "domainIdCode", "receptionDate", "actualSuspensionDate",
        "regionValueDate", "checkNumber", "clientReference", "bankReference", "transactionTypeCode", "documentYear", "documentCode", "sealCode", "pspFirstName", "pspAddress", "pspPostalCode",
        "pspCity", "pspFiscalCode", "pspVatNumber", "abiCode", "cabCode", "ibanCode", "accountRegistryCode", "provisionalAe", "provisionalCode", "accountTypeCode", "processCode", "executionPgCode",
        "transferPgCode", "processPgNumber", "managementProvisionalCode", "endToEndId", "clientReference", "bankReference", "regularized");
  }
}