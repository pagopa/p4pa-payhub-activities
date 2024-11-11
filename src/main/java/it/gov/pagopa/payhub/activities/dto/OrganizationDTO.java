package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO implements Serializable {


  private Long mygovEnteId;
  private String codIpaEnte;
  private String codiceFiscaleEnte;
  private String deNomeEnte;
  private String emailAmministratore;
  private Timestamp dtCreazione;
  private Timestamp dtUltimaModifica;
  private String codRpDatiVersTipoVersamento;
  private BigDecimal numRpDatiVersDatiSingVersCommissioneCaricoPa;
  private String codRpDatiVersDatiSingVersIbanAccredito;
  private String codRpDatiVersDatiSingVersBicAccredito;
  private String codRpDatiVersDatiSingVersIbanAppoggio;
  private String codRpDatiVersDatiSingVersBicAppoggio;
  private String myboxClientKey;
  private String myboxClientSecret;
  private String enteSilInviaRispostaPagamentoUrl;
  private String codGlobalLocationNumber;
  private String dePassword;
  private Boolean codRpDatiVersDatiSingVersBicAccreditoSeller;
  private String deRpEnteBenefDenominazioneBeneficiario;
  private String deRpEnteBenefIndirizzoBeneficiario;
  private String deRpEnteBenefCivicoBeneficiario;
  private String codRpEnteBenefCapBeneficiario;
  private String deRpEnteBenefLocalitaBeneficiario;
  private String deRpEnteBenefProvinciaBeneficiario;
  private String codRpEnteBenefNazioneBeneficiario;
  private String deRpEnteBenefTelefonoBeneficiario;
  private String deRpEnteBenefSitoWebBeneficiario;
  private String deRpEnteBenefEmailBeneficiario;
  private String applicationCode;
  private String codCodiceInterbancarioCbill;
  private String deInformazioniEnte;
  private String deLogoEnte;
  private String deAutorizzazione;
  private RegistryStatusDTO cdStatoEnte;
  private String deUrlEsterniAttiva;
  private String linguaAggiuntiva;
  private String codTipoEnte;
  private LocalDate dtAvvio;
  private Long mygovIntermediarioId;
}
