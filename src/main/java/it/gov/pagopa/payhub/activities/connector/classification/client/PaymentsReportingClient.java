package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Lazy
@Service
@Slf4j
public class PaymentsReportingClient {

    private final ClassificationApisHolder classificationApisHolder;

    public PaymentsReportingClient(ClassificationApisHolder classificationApisHolder) {
        this.classificationApisHolder = classificationApisHolder;
    }

    public Integer saveAll(List<PaymentsReporting> dtos, String accessToken) {
        return classificationApisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken)
                .saveAll1(dtos);
    }

    public CollectionModelPaymentsReporting getByOrganizationIdAndIuf(Long organizationId, String iuf, String accessToken) {
        return classificationApisHolder.getPaymentsReportingSearchApi(accessToken)
                .crudPaymentsReportingFindByOrganizationIdAndIuf(organizationId, iuf);
    }


    public PaymentsReporting getBySemanticKey(Long orgId, String iuv, String iur, int transferIndex, String accessToken) {
        try {
            return classificationApisHolder.getPaymentsReportingSearchApi(accessToken)
                    .crudPaymentsReportingFindBySemanticKey(orgId, iuv, iur, transferIndex);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("PaymentsReporting not found: organizationId: {}, iuv: {}, iur: {}, transferIndex: {}", orgId, iuv, iur, transferIndex);
            return null;
        }
    }

}
