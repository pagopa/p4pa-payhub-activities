package it.gov.pagopa.payhub.activities.dto.export;

import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileVersion;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExportFlowFileVersions {
    private ExportFlowFileVersions() {}

    public static final String EXPORT_PAID_VERSION_V1 = "v1.0";
    public static final String EXPORT_PAID_VERSION_V1_1 = "v1.1";
    public static final String EXPORT_PAID_VERSION_V1_2 = "v1.2";
    public static final String EXPORT_PAID_VERSION_V1_3 = "v1.3";

    private static final Map<ExportFile.ExportFileTypeEnum, Set<String>> availableVersions;

    static {
        availableVersions = Map.of(
                ExportFile.ExportFileTypeEnum.PAID, Arrays.stream(PaidExportFileVersion.values()).map(PaidExportFileVersion::getValue).collect(Collectors.toSet())
        );
    }

    public static Set<String> getAvailableVersions(ExportFile.ExportFileTypeEnum fileType) {
        return availableVersions.get(fileType);
    }
}
