package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
@Slf4j
@Lazy
@Component
public class IufClassificationActivityImpl implements IufClassificationActivity {
    private final PaymentsReportingDao paymentsReportingDao;
    private final TreasuryDao treasuryDao;
    private final ClassifyDao classifyDao;

    private static final String TES_NO_MATCH = "TES_NO_MATCH";

    public IufClassificationActivityImpl(PaymentsReportingDao paymentsReportingDao, TreasuryDao treasuryDao, ClassifyDao classifyDao) {
        this.paymentsReportingDao = paymentsReportingDao;
        this.treasuryDao = treasuryDao;
        this.classifyDao = classifyDao;
    }

    /**
     * Save classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param iuf            the unique identifier of the payment reporting flow (IUF)
     * @return IufClassificationActivityResult containing PaymentsReportingDTO list and success flag
     */
    @Override
    public IufClassificationActivityResult save(Long organizationId, String iuf) {
        if (organizationId==null || organizationId.equals(0L))
            throw new PaymentsClassificationSaveException("organization id may be not null or zero");
        if (iuf==null || iuf.isEmpty())
            throw new PaymentsClassificationSaveException("iuf may be not null or blank");

        IufClassificationActivityResult iufClassificationActivityResult = new IufClassificationActivityResult();
        List<PaymentsReportingDTO> paymentsReportingDTOS = paymentsReportingDao.findByOrganizationIdFlowIdentifierCode(organizationId, iuf);

        for (TreasuryDTO treasuryDTO : treasuryDao.searchByIuf(iuf)) {
            try {
                setAndSave(paymentsReportingDTOS.size(), treasuryDTO.getMygovFlussoTesoreriaId());
            }
            catch (PaymentsClassificationSaveException paymentsClassificationSaveException) {
                log.error("Error saving classification");
                throw new PaymentsClassificationSaveException("Error saving classification");
            }
        }
        return iufClassificationActivityResult.toBuilder()
                .paymentsReportingDTOS(paymentsReportingDTOS)
                .success(true)
                .build();
    }

    /**
     *
     * @param treasuryId primary key of Treasury
     * @throws PaymentsClassificationSaveException specific exception
     */


    /**
     *
     * @param paymentsReportingSize size of the list of payments reporting found
     * @param treasuryId primary key of Treasury
     * @throws PaymentsClassificationSaveException exception
     */
    private void setAndSave(int paymentsReportingSize, Long treasuryId) throws PaymentsClassificationSaveException {
        if (paymentsReportingSize==0 && treasuryId!=null) {
            ClassifyDTO classifyDTO = ClassifyDTO.builder()
                    .classificationCode(TES_NO_MATCH)
                    .treasuryId(treasuryId)
                    .build();
            try {
                classifyDao.save(classifyDTO);
            }
            catch (Exception e) {
                throw new PaymentsClassificationSaveException("ClassifySaving exception error: "+e.getMessage());
            }
        }
    }

}