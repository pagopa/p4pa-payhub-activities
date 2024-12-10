package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.ReportingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for defining an activity to process payment reporting classifications based on IUF.
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
     * @param iuf the unique identifier of the payment reporting flow
     * @return dto IufClassificationDTO that contains list of classifications and boolean value for process OK or KO
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

    /**
     * verify activity input parameters
     * @param organizationId the unique identifier of the organization
     * @param iuf the unique identifier of the payment reporting flow
     * @return boolean if parameters are verified otherwise false
     */
    private boolean verifyParameters(String organizationId, String iuf) {
        return !(organizationId == null || organizationId.isBlank() || iuf == null || iuf.isBlank());
    }
}