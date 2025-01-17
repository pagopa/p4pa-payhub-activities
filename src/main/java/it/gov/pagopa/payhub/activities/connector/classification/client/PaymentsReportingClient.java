package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.PaymentsReportingApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class PaymentsReportingClient {

    private final PaymentsReportingApisHolder paymentsReportingApisHolder;

    public PaymentsReportingClient(PaymentsReportingApisHolder classificationApisHolder) {
        this.paymentsReportingApisHolder = classificationApisHolder;
    }

    public Integer saveAll(List<PaymentsReporting> dtos, String accessToken) {
        return paymentsReportingApisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken)
                .saveAll1(dtos);
    }
    public CollectionModelPaymentsReporting getByOrganizationIdAndIuf(Long organizationId, String iuf, String accessToken) {
        return paymentsReportingApisHolder.getPaymentsReportingSearchApi(accessToken)
                .crudPaymentsReportingFindByOrganizationIdAndIuf(organizationId, iuf);
    }


    public CollectionModelPaymentsReporting getBySemanticKey(Long orgId, String iuv, String iur, int transferIndex, String accessToken) {
        return paymentsReportingApisHolder.getPaymentsReportingSearchApi(accessToken)
                .crudPaymentsReportingFindByOrganizationIdAndIuvAndIurAndTransferIndex(orgId, iuv, iur, transferIndex);
    }

}
