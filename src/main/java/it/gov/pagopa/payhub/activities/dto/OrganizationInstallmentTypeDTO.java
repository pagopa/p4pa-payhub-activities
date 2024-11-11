package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationInstallmentTypeDTO implements Serializable {


  private Long mygovEnteTipoDovutoId;
  private OrganizationDTO mygovEnteId;
  private String codTipo;
  private String deTipo;
  private String ibanAccreditoPi;
  private String bicAccreditoPi;
  private String ibanAppoggioPi;
  private String bicAppoggioPi;
  private String ibanAccreditoPsp;
  private String bicAccreditoPsp;
  private String ibanAppoggioPsp;
  private String bicAppoggioPsp;
  private String codContoCorrentePostale;
  private String codXsdCausale;
  private boolean bicAccreditoPiSeller;
  private boolean bicAccreditoPspSeller;
  private boolean spontaneo;
  private BigDecimal importo;
  private String deUrlPagamentoDovuto;
  private String deBilancioDefault;
  private boolean flgCfAnonimo;
  private boolean flgScadenzaObbligatoria;
  private boolean flgStampaDataScadenza;
  private String deIntestatarioCcPostale;
  private String deSettoreEnte;
  private boolean flgNotificaIo;
  private boolean flgNotificaEsitoPush;
  private Integer maxTentativiInoltroEsito;
  private Long mygovEnteSilId;
  private boolean flgAttivo;
  private String codiceContestoPagamento;
  private boolean flgDisabilitaStampaAvviso;
  private String macroArea;
  private String tipoServizio;
  private String motivoRiscossione;
  private String codTassonomico;
  private String urlNotificaPnd;
  private String userPnd;
  private String pswPnd;
  private String urlNotificaAttualizzazionePnd;
}
