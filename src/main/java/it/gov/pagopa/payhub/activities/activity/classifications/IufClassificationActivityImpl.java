package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ReportingDao;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
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
     * @param flowIdentifierCode the unique identifier of the payment reporting flow
     * @return dto IufClassificationDTO that contains list of classifications and boolean value for process OK or KO
     */
    @Override
    public IufClassificationDTO classify(Long organizationId, String flowIdentifierCode) {
        if (verifyParameters(organizationId, flowIdentifierCode)) {
            List<PaymentsReportingDTO> paymentsReportingDTOS = reportingDao.findByOrganizationIdFlowIdentifierCode(organizationId, flowIdentifierCode);
            if (! paymentsReportingDTOS.isEmpty()) {
                return IufClassificationDTO.builder()
                        .paymentsReportingDTOS(paymentsReportingDTOS)
                        .success(true)
                        .build();
            }
        }
        log.error("Empty list of classifications returned");
        return IufClassificationDTO.builder()
                .paymentsReportingDTOS(new ArrayList<>())
                .success(false)
                .build();
    }

    /**
     * verify activity input parameters
     * @param organizationId the unique identifier of the organization
     * @param flowIdentifierCode the unique identifier of the payment reporting flow
     * @return boolean if parameters are verified otherwise false
     */
    private boolean verifyParameters(Long organizationId, String flowIdentifierCode) {
        return !(organizationId == null || organizationId==0L || flowIdentifierCode == null || flowIdentifierCode.isBlank());
    }
}