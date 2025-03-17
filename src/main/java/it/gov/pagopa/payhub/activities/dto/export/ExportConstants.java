package it.gov.pagopa.payhub.activities.dto.export;

import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;

import java.util.*;

public class ExportConstants {

    public static final String EXPORT_PAID_VERSION_V1 = "v1.0";
    public static final String EXPORT_PAID_VERSION_V1_1 = "v1.1";
    public static final String EXPORT_PAID_VERSION_V1_2 = "v1.2";
    public static final String EXPORT_PAID_VERSION_V1_3 = "v1.3";

    private static final Map<ExportFile.FlowFileTypeEnum, Set<String>> availableVersions = new EnumMap<>(ExportFile.FlowFileTypeEnum.class);

    private ExportConstants (){}

    static {
        Set<String> paidVersions = new HashSet<>();
        paidVersions.add(ExportConstants.EXPORT_PAID_VERSION_V1);
        paidVersions.add(ExportConstants.EXPORT_PAID_VERSION_V1_1);
        paidVersions.add(ExportConstants.EXPORT_PAID_VERSION_V1_2);
        paidVersions.add(ExportConstants.EXPORT_PAID_VERSION_V1_3);
        availableVersions.put(ExportFile.FlowFileTypeEnum.PAID, paidVersions);
    }

    public static Set<String> getAvailableVersions(ExportFile.FlowFileTypeEnum fileType) {
        return availableVersions.get(fileType);
    }
}
