package it.gov.pagopa.payhub.activities.dto.exportflow;

import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileVersion;
import org.junit.jupiter.api.Test;

class ExportFileVersionsTest {

    @Test
    void testPaidExportFileVersion(){
        checkPaidExportFileVersion(PaidExportFileVersion.V1_0, ExportFileVersions.EXPORT_PAID_VERSION_V1);
        checkPaidExportFileVersion(PaidExportFileVersion.V1_1, ExportFileVersions.EXPORT_PAID_VERSION_V1_1);
        checkPaidExportFileVersion(PaidExportFileVersion.V1_2, ExportFileVersions.EXPORT_PAID_VERSION_V1_2);
        checkPaidExportFileVersion(PaidExportFileVersion.V1_3, ExportFileVersions.EXPORT_PAID_VERSION_V1_3);
    }

    private static void checkPaidExportFileVersion(PaidExportFileVersion enumValue, String value){
        if(!enumValue.getValue().equals(value)){
            throw new IllegalStateException("Constant not aligned with version PaidExportFileVersion " + enumValue);
        }
    }
}
