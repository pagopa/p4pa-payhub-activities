package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.enums.PagoPaInteractionModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BrokerDTO implements Serializable {

    private Long brokerId;
    private String brokerFiscalCode;
    private String orgFiscalCode;
    private String brokerStationId;
    private byte[] subscriptionKey;
    private byte[] gpdKey;
    private byte [] acaKey;
    private String domain;
    private String personalisationFe;
    private String brokerStationBroadcastId;
    private PagoPaInteractionModel pagoPaInteractionModel;
    private OrganizationDTO org;
}
