package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.PaymentNotificationClient;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class PaymentNotificationServiceImpl implements PaymentNotificationService{

  private final PaymentNotificationClient paymentNotificationClient;
  private final AuthnService authnService;

  public PaymentNotificationServiceImpl(PaymentNotificationClient paymentNotificationClient,
      AuthnService authnService) {
    this.paymentNotificationClient = paymentNotificationClient;
    this.authnService = authnService;
  }


  @Override
  public PaymentNotificationDTO createPaymentNotification(PaymentNotificationDTO dto) {
    return paymentNotificationClient.createPaymentNotification(dto, authnService.getAccessToken());
  }

  @Override
  public PaymentNotificationNoPII getByOrgIdAndIud(Long organizationId, String iud) {
    return paymentNotificationClient.getByOrgIdAndIud(organizationId, iud, authnService.getAccessToken());
  }
}
