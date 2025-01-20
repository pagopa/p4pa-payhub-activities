package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryRequestBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TreasuryRequestMapperTest {

    @Test
    void testMap() {
        // Given
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();


        // When
        TreasuryRequestBody result = TreasuryRequestMapper.map(treasury);

        // Then
            assertEquals(treasury.getTreasuryId(), result.getTreasuryId());
        assertEquals(treasury.getBillYear(), result.getBillYear());
        assertEquals(treasury.getBillCode(), result.getBillCode());
        assertEquals(treasury.getAccountCode(), result.getAccountCode());
        assertEquals(treasury.getDomainIdCode(), result.getDomainIdCode());
        assertEquals(treasury.getTransactionTypeCode(), result.getTransactionTypeCode());
        assertEquals(treasury.getRemittanceCode(), result.getRemittanceCode());
        assertEquals(treasury.getRemittanceDescription(), result.getRemittanceDescription());
        assertEquals(treasury.getBillAmountCents(), result.getBillAmountCents());
        assertEquals(treasury.getBillDate(), result.getBillDate());
        assertEquals(treasury.getReceptionDate(), result.getReceptionDate());
        assertEquals(treasury.getDocumentYear(), result.getDocumentYear());
        assertEquals(treasury.getDocumentCode(), result.getDocumentCode());
        assertEquals(treasury.getSealCode(), result.getSealCode());
        assertEquals(treasury.getPspLastName(), result.getPspLastName());
        assertEquals(treasury.getPspFirstName(), result.getPspFirstName());
        assertEquals(treasury.getPspAddress(), result.getPspAddress());
        assertEquals(treasury.getPspPostalCode(), result.getPspPostalCode());
        assertEquals(treasury.getPspCity(), result.getPspCity());
        assertEquals(treasury.getPspFiscalCode(), result.getPspFiscalCode());
        assertEquals(treasury.getPspVatNumber(), result.getPspVatNumber());
        assertEquals(treasury.getAbiCode(), result.getAbiCode());
        assertEquals(treasury.getCabCode(), result.getCabCode());
        assertEquals(treasury.getAccountRegistryCode(), result.getAccountRegistryCode());
        assertEquals(treasury.getProvisionalAe(), result.getProvisionalAe());
        assertEquals(treasury.getProvisionalCode(), result.getProvisionalCode());
        assertEquals(treasury.getIbanCode(), result.getIbanCode());
        assertEquals(treasury.getAccountTypeCode(), result.getAccountTypeCode());
        assertEquals(treasury.getProcessCode(), result.getProcessCode());
        assertEquals(treasury.getExecutionPgCode(), result.getExecutionPgCode());
        assertEquals(treasury.getTransferPgCode(), result.getTransferPgCode());
        assertEquals(treasury.getProcessPgNumber(), result.getProcessPgNumber());
        assertEquals(treasury.getRegionValueDate(), result.getRegionValueDate());
        assertEquals(treasury.getOrganizationId(), result.getOrganizationId());
        assertEquals(treasury.getIuf(), result.getIuf());
        assertEquals(treasury.getIuv(), result.getIuv());
        assertEquals(treasury.getCreationDate(), result.getCreationDate());
        assertEquals(treasury.getUpdateDate(), result.getUpdateDate());
        assertEquals(treasury.getIngestionFlowFileId(), result.getIngestionFlowFileId());
        assertEquals(treasury.getActualSuspensionDate(), result.getActualSuspensionDate());
        assertEquals(treasury.getManagementProvisionalCode(), result.getManagementProvisionalCode());
        assertEquals(treasury.getEndToEndId(), result.getEndToEndId());
        assertEquals(treasury.getRegularized(), result.getRegularized());
        TestUtils.checkNotNullFields(result);
    }

    @Test
    void testMap_NullTreasury() {
        // Given
        Treasury treasury = null;

        // When
        TreasuryRequestBody result = TreasuryRequestMapper.map(treasury);

        // Then
        assertNull(result);
    }
}