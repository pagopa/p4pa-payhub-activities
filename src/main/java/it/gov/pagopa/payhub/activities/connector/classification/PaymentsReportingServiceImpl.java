package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.PaymentsReportingClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelPaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class PaymentsReportingServiceImpl implements PaymentsReportingService {

    private final PaymentsReportingClient paymentsReportingClient;
    private final AuthnService authnService;

    public PaymentsReportingServiceImpl(PaymentsReportingClient paymentsReportingClient, AuthnService authnService) {
        this.paymentsReportingClient = paymentsReportingClient;
        this.authnService = authnService;
    }


    @Override
    public Integer saveAll(List<PaymentsReporting> dtos) {
        return paymentsReportingClient.saveAll(dtos, authnService.getAccessToken());
    }

    @Override
    public CollectionModelPaymentsReporting getByOrganizationIdAndIuf(Long organizationId, String iuf) {
        return paymentsReportingClient.getByOrganizationIdAndIuf(organizationId, iuf, authnService.getAccessToken());
    }

    @Override
    public CollectionModelPaymentsReporting getBySemanticKey(TransferSemanticKeyDTO tSKDTO) {
        return paymentsReportingClient.getBySemanticKey(tSKDTO.getOrgId(), tSKDTO.getIuv(), tSKDTO.getIur(), tSKDTO.getTransferIndex(), authnService.getAccessToken());
    }

}
