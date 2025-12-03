package it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization;

import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationAdditionalLanguage;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class OrganizationMapperTest {

    @Test
    void testMap() {
        OrganizationIngestionFlowFileDTO dto = new OrganizationIngestionFlowFileDTO();
        dto.setExternalOrganizationId("EXTERNALID");
        dto.setIpaCode("IPA123");
        dto.setOrgFiscalCode("FISCAL123");
        dto.setOrgName("Test Org");
        dto.setOrgTypeCode("TYPE1");
        dto.setOrgEmail("test@org.it");
        dto.setPostalIban("IT60X0542811101000000123456");
        dto.setIban("IT60X0542811101000000123456");
        dto.setSegregationCode("SEGCODE");
        dto.setCbillInterBankCode("CBILL");
        dto.setOrgLogo("logo");
        dto.setStatus("ACTIVE");
        dto.setAdditionalLanguage("EN");
        dto.setStartDate(LocalDateTime.of(2024, 6, 1, 0, 0));
        dto.setFlagNotifyIo(true);
        dto.setFlagNotifyOutcomePush(false);
        dto.setFlagTreasury(false);
        dto.setIoApiKey("IOKEY");
        dto.setSendApiKey("SENDKEY");
        dto.setGenerateNoticeApiKey("GENERATENOTICEKEY");

        Long brokerId = 456L;

        // Act
        OrganizationCreateDTO result = new OrganizationMapper().map(dto, brokerId);

        // Assert
        Assertions.assertSame(dto.getExternalOrganizationId(), result.getExternalOrganizationId());
        Assertions.assertEquals("IPA123", result.getIpaCode());
        Assertions.assertEquals("FISCAL123", result.getOrgFiscalCode());
        Assertions.assertEquals("Test Org", result.getOrgName());
        Assertions.assertEquals("TYPE1", result.getOrgTypeCode());
        Assertions.assertEquals("test@org.it", result.getOrgEmail());
        Assertions.assertEquals("IT60X0542811101000000123456", result.getPostalIban());
        Assertions.assertEquals("IT60X0542811101000000123456", result.getIban());
        Assertions.assertEquals("SEGCODE", result.getSegregationCode());
        Assertions.assertEquals("CBILL", result.getCbillInterBankCode());
        Assertions.assertEquals("logo", result.getOrgLogo());
        Assertions.assertEquals(OrganizationStatus.ACTIVE, result.getStatus());
        Assertions.assertEquals(OrganizationAdditionalLanguage.EN, result.getAdditionalLanguage());
        Assertions.assertEquals(LocalDate.of(2024, 6, 1), result.getStartDate());
        Assertions.assertEquals(456L, result.getBrokerId());
        Assertions.assertTrue(result.getFlagNotifyIo());
        Assertions.assertFalse(result.getFlagNotifyOutcomePush());
        Assertions.assertFalse(result.getFlagPaymentNotification());
        Assertions.assertFalse(result.getFlagTreasury());
        Assertions.assertSame(dto.getIoApiKey(), result.getIoApiKey());
        Assertions.assertSame(dto.getSendApiKey(), result.getSendApiKey());
        Assertions.assertSame(dto.getGenerateNoticeApiKey(), result.getGenerateNoticeApiKey());

        TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
                "updateTraceId", "organizationId", "password");
    }
}