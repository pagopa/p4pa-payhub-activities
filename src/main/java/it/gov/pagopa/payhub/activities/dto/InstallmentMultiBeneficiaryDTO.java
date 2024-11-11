package it.gov.pagopa.payhub.activities.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentMultiBeneficiaryDTO implements Serializable {

	private String denominazioneBeneficiario;
	private String codiceIdentificativoUnivoco;
	private String ibanAddebito;
	private String indirizzoBeneficiario;
	private String civicoBeneficiario;
	private String capBeneficiario;
	private String nazioneBeneficiario;
	private String provinciaBeneficiario;
	private String localitaBeneficiario;
	private String importoSecondario;
	private String causaleMB;
	private String datiSpecificiRiscossione;

	@JsonIgnore
	private Long idDovuto;
}
