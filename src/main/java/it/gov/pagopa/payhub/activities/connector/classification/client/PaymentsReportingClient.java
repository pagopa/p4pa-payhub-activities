package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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


    public CollectionModelPaymentsReporting getByTransferSemanticKey(Long orgId, String iuv, String iur, int transferIndex, String accessToken) {
            return classificationApisHolder.getPaymentsReportingSearchApi(accessToken)
                    .crudPaymentsReportingFindByTransferSemanticKey(orgId, iuv, iur, transferIndex);
    }

    public CollectionModelPaymentsReporting findDuplicates(Long organizationId, String iuv, int transferIndex, String orgFiscalCode, String accessToken) {
        return classificationApisHolder.getPaymentsReportingSearchApi(accessToken)
            .crudPaymentsReportingFindDuplicates(organizationId, iuv, transferIndex, orgFiscalCode);
    }

    public OffsetDateTime findLatestFlowDate(Long organizationId,  String accessToken) {
        return classificationApisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken)
                .findLatestFlowDate(organizationId);
    }

}
