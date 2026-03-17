package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

public final class NoticeFileUtils {

    private NoticeFileUtils() {
    }

    public static String buildNoticeFileName(IngestionFlowFile ingestionFlowFile) {
        return ingestionFlowFile.getFileName().replace(".zip", "_notice.zip");
    }
}