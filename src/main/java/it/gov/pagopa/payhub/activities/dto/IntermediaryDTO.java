package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.enums.PagoPaInteractionModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntermediaryDTO implements Serializable {

    private Long intermediaryId;
    private String intermediaryFiscalCode;
    private String orgFiscalCode;
    private String intermediaryStationId;
    private byte[] subscriptionKey;
    private byte[] gpdKey;
    private String domain;
    private String personalisationFe;
    private String intermediaryStationBroadcastId;
    private PagoPaInteractionModel pagoPaInteractionModel;
    private OrganizationDTO org;
}
