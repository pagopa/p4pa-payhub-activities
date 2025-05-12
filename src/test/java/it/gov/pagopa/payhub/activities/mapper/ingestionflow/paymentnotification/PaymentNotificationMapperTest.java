package it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.Person;
import it.gov.pagopa.pu.classification.dto.generated.PersonEntityType;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentNotificationMapperTest {

  @Test
  void mapReturnPaymentNotificationDTOWithCorrectValues() {
    PaymentNotificationIngestionFlowFileDTO dto = new PaymentNotificationIngestionFlowFileDTO();
    dto.setIud("testIud");
    dto.setIuv("testIuv");
    dto.setPaymentExecutionDate(LocalDate.now());
    dto.setPaymentType("testType");
    dto.setAmountPaidCents(BigDecimal.valueOf(1000));
    dto.setPaCommissionCents(BigDecimal.valueOf(100));
    dto.setRemittanceInformation("testInfo");
    dto.setTransferCategory("testCategory");
    dto.setDebtPositionTypeOrgCode("testCode");
    dto.setBalance("balance");
    dto.setDebtorUniqueIdentifierType("F");
    dto.setDebtorUniqueIdentifierCode("testCode");
    dto.setDebtorFullName("testName");
    dto.setDebtorAddress("testAddress");
    dto.setDebtorCivic("testCivic");
    dto.setDebtorPostalCode("12345");
    dto.setDebtorLocation("testLocation");
    dto.setDebtorProvince("testProvince");
    dto.setDebtorNation("testNation");

    IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
    ingestionFlowFile.setOrganizationId(2L);
    ingestionFlowFile.setIngestionFlowFileId(1L);

    PaymentNotificationDTO result = new PaymentNotificationMapper().map(dto, ingestionFlowFile);

    Assertions.assertEquals(2L, result.getOrganizationId());
    Assertions.assertEquals(1L, result.getIngestionFlowFileId());
    Assertions.assertEquals("testIud", result.getIud());
    Assertions.assertEquals("testIuv", result.getIuv());
    Assertions.assertEquals(dto.getPaymentExecutionDate(), result.getPaymentExecutionDate());
    Assertions.assertEquals("testType", result.getPaymentType());
    Assertions.assertEquals(1000L, result.getAmountPaidCents());
    Assertions.assertEquals(100L, result.getPaCommissionCents());
    Assertions.assertEquals("testInfo", result.getRemittanceInformation());
    Assertions.assertEquals("testCategory", result.getTransferCategory());
    Assertions.assertEquals("testCode", result.getDebtPositionTypeOrgCode());
    Assertions.assertEquals(dto.getBalance(), result.getBalance());
    Assertions.assertNotNull(result.getDebtor());
    Assertions.assertEquals(PersonEntityType.F, result.getDebtor().getEntityType());
    Assertions.assertEquals("testCode", result.getDebtor().getFiscalCode());
    Assertions.assertEquals("testName", result.getDebtor().getFullName());
    Assertions.assertEquals("testAddress", result.getDebtor().getAddress());
    Assertions.assertEquals("testCivic", result.getDebtor().getCivic());
    Assertions.assertEquals("12345", result.getDebtor().getPostalCode());
    Assertions.assertEquals("testLocation", result.getDebtor().getLocation());
    Assertions.assertEquals("testProvince", result.getDebtor().getProvince());
    Assertions.assertEquals("testNation", result.getDebtor().getNation());
    TestUtils.checkNotNullFields(result,"paymentNotificationId","creationDate","updateDate","updateOperatorExternalId", "updateTraceId");
  }

  @Test
  void mapPersonalDataFromPaymentNotificationReturnPersonWithCorrectValues() {
    PaymentNotificationIngestionFlowFileDTO dto = new PaymentNotificationIngestionFlowFileDTO();
    dto.setDebtorUniqueIdentifierType("F");
    dto.setDebtorUniqueIdentifierCode("testCode");
    dto.setDebtorFullName("testName");
    dto.setDebtorAddress("testAddress");
    dto.setDebtorCivic("testCivic");
    dto.setDebtorPostalCode("12345");
    dto.setDebtorLocation("testLocation");
    dto.setDebtorProvince("testProvince");
    dto.setDebtorNation("testNation");
    dto.setDebtorEmail("email@email.com");

    Person result = PaymentNotificationMapper.mapPersonalDataFromPaymentNotification(dto);

    Assertions.assertEquals(PersonEntityType.F, result.getEntityType());
    Assertions.assertEquals("testCode", result.getFiscalCode());
    Assertions.assertEquals("testName", result.getFullName());
    Assertions.assertEquals("testAddress", result.getAddress());
    Assertions.assertEquals("testCivic", result.getCivic());
    Assertions.assertEquals("12345", result.getPostalCode());
    Assertions.assertEquals("testLocation", result.getLocation());
    Assertions.assertEquals("testProvince", result.getProvince());
    Assertions.assertEquals("testNation", result.getNation());
    TestUtils.checkNotNullFields(result);
  }

}