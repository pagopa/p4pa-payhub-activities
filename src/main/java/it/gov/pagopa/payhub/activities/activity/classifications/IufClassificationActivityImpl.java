package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Lazy
@Component
public class IufClassificationActivityImpl implements IufClassificationActivity {
    private final PaymentsReportingDao paymentsReportingDao;
    private final TreasuryDao treasuryDao;
    private final ClassificationDao classificationDao;

    private static final String TES_NO_MATCH = "TES_NO_MATCH";

    public IufClassificationActivityImpl(PaymentsReportingDao paymentsReportingDao, TreasuryDao treasuryDao, ClassificationDao classificationDao) {
        this.paymentsReportingDao = paymentsReportingDao;
        this.treasuryDao = treasuryDao;
        this.classificationDao  = classificationDao;
    }

    @Override
    public IufClassificationActivityResult classify(Long organizationId, String iuf) {
        log.debug("Starting Classification Activity for organization id {} and iuf {}", organizationId,iuf);

        IufClassificationActivityResult iufClassificationActivityResult = new IufClassificationActivityResult();
        List<PaymentsReportingDTO> paymentsReportingDTOS = paymentsReportingDao.findByOrganizationIdFlowIdentifierCode(organizationId, iuf);
        log.debug("Number of payments reporting found for iuf {}: {}", iuf, paymentsReportingDTOS.size());
        for (TreasuryDTO treasuryDTO : treasuryDao.searchByIuf(iuf)) {
            log.debug("Saving classification if payments reporting exist");
            setAndSave(paymentsReportingDTOS.size(), treasuryDTO.getMygovFlussoTesoreriaId());
        }
        return iufClassificationActivityResult.toBuilder()
                .paymentsReportingDTOS(paymentsReportingDTOS)
                .success(true)
                .build();
    }

    /**
     *
     * @param paymentsReportingSize size of the list of payments reporting found
     * @param treasuryId primary key of Treasury
     */
    private void setAndSave(int paymentsReportingSize, Long treasuryId) {
        if (paymentsReportingSize==0 && treasuryId!=null) {
            log.debug("Saving classification TES_NO_MATCH and treasuryId: {}",treasuryId);
            ClassifyDTO classificationDTO = ClassifyDTO.builder()
                    .classificationCode(TES_NO_MATCH)
                    .treasuryId(treasuryId)
                    .build();
            classificationDao.save(classificationDTO);
        }
    }

}