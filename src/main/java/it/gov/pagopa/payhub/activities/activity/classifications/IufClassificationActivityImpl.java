package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.ClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.OperatorNotAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
     * @return List<ClassificationDTO> list of classifications
     */
    public List<ClassificationDTO> classify(String organizationId, String iuf) {
        List<ClassificationDTO> classificationDTOS = reportingDao.findById(organizationId, iuf);
        if (classificationDTOS.isEmpty()) {
            log.error("Empty list of classifications returned");
        }
        return  classificationDTOS;
    }
}