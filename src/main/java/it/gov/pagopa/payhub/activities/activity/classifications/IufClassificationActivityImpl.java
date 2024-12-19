package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyResultDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Lazy
@Component
public class IufClassificationActivityImpl implements IufClassificationActivity {
    private final PaymentsReportingDao paymentsReportingDao;
    private final TreasuryDao treasuryDao;
    private final ClassificationDao classificationDao;

    public IufClassificationActivityImpl(PaymentsReportingDao paymentsReportingDao, TreasuryDao treasuryDao, ClassificationDao classificationDao) {
        this.paymentsReportingDao = paymentsReportingDao;
        this.treasuryDao = treasuryDao;
        this.classificationDao  = classificationDao;
    }

    @Override
    public IufClassificationActivityResult classify(Long organizationId, String iuf) {
        log.debug("Starting Classification Activity for organization id {} and iuf {}", organizationId,iuf);

        List<ClassifyResultDTO> classifyResultDTOS =
                getClassifyFromPayments(paymentsReportingDao.findByOrganizationIdFlowIdentifierCode(organizationId, iuf));

        log.debug("Number of payments reporting found for iuf {}: {}", iuf, classifyResultDTOS.size());

        for (TreasuryDTO treasuryDTO : treasuryDao.searchByIuf(iuf)) {
            log.debug("Saving classification if payments reporting exist");
            if (!classifyResultDTOS.isEmpty()) {
                saveClassification(organizationId, treasuryDTO.getMygovFlussoTesoreriaId(), iuf);
            }
        }
        return IufClassificationActivityResult.builder()
                .classifyResultDTOS(classifyResultDTOS)
                .success(true)
                .build();
    }

    /**
     * save classification data
     *
     * @param organizationId organization id
     * @param treasuryId  treasury id
     * @param iuf  flow unique identifier
     */
    private void saveClassification(Long organizationId, Long treasuryId, String iuf) {
        log.debug("Saving classification TES_NO_MATCH for organizationId: {} - treasuryId: {} - iuf: {}", organizationId, treasuryId, iuf);

        classificationDao.save(ClassifyDTO.builder()
            .organizationId(organizationId)
            .treasuryId(treasuryId)
            .iuf(iuf)
            .classificationsEnum(ClassificationsEnum.TES_NO_MATCH)
            .build());
    }

    /**
     * @param paymentsReportingDTOS list of payments
     * @return List<ClassifyResultDTO> subset of fields of the list of payments
     */
    private List<ClassifyResultDTO> getClassifyFromPayments(List<PaymentsReportingDTO> paymentsReportingDTOS) {
        List<ClassifyResultDTO> classifyResultDTOS = new ArrayList<>();
        for (PaymentsReportingDTO paymentsReportingDTO: paymentsReportingDTOS) {
            classifyResultDTOS.add(ClassifyResultDTO.builder()
                    .organizationId(paymentsReportingDTO.getOrganizationId())
                    .creditorReferenceId(paymentsReportingDTO.getCreditorReferenceId())
                    .regulationUniqueIdentifier(paymentsReportingDTO.getRegulationUniqueIdentifier())
                    .transferIndex(paymentsReportingDTO.getTransferIndex())
                    .build());
        }
        return classifyResultDTOS;
    }

}