package it.gov.pagopa.payhub.activities.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPrimaryOrganizationDTO {

    private String denominazioneBeneficiario;
    private String codiceIdentificativoUnivoco;
    private String ibanAddebito;
    private String indirizzoBeneficiario;
    private String civicoBeneficiario;
    private String capBeneficiario;
    private String nazioneBeneficiario;
    private String provinciaBeneficiario;
    private String localitaBeneficiario;
    private String importo;

    @JsonIgnore
    private Long idDovuto;
}
