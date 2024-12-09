package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.ReportingDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.IufClassificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
@Slf4j
@Lazy
@Component
public class IufClassificationActivityImpl implements IufClassificationActivity{
    private final ReportingDao reportingDao;

    public IufClassificationActivityImpl(ReportingDao reportingDao) {
        this.reportingDao = reportingDao;
    }

    /**
     * Processes IUF classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param iuf            the unique identifier of the payment reporting flow (IUF)
     * @return IufClassificationDTO contains list of classifications and boolean value
     */

    @Override
    public IufClassificationDTO classify(String organizationId, String iuf) {
        if (verifyParameters(organizationId, iuf)) {
            List<ReportingDTO> reportingDTOS = reportingDao.findById(organizationId, iuf);
            if (! reportingDTOS.isEmpty()) {
                return IufClassificationDTO.builder()
                        .reportingDTOList(reportingDTOS)
                        .success(true)
                        .build();
            }
        }
        log.error("Empty list of classifications returned");
        return IufClassificationDTO.builder()
                .reportingDTOList(new ArrayList<>())
                .success(false)
                .build();
    }

    private boolean verifyParameters(String organizationId, String iuf) throws IufClassificationException  {
        if (organizationId == null || organizationId.isBlank())
            return false;
        if (iuf == null || iuf.isBlank())
            return false;
        return true;
    }
}