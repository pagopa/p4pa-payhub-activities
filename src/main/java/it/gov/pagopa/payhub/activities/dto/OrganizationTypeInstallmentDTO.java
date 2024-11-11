package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTypeInstallmentDTO implements Serializable {
  private Long mygovEnteTipoDovutoId;
  private Long mygovEnteId;
  private String codIpaEnte;
  private String deNomeEnte;
  private String thumbLogoEnte;
  private String hashThumbLogoEnte;
  private String codTipo;
  private String deTipo;
  private String deUrlPagamentoDovuto;
  private boolean flgCfAnonimo;
  private boolean flgScadenzaObbligatoria;
  private boolean flgAttivo;
  private String importo;

  private LocalDateTime dtUltimaAbilitazione;
  private LocalDateTime dtUltimaDisabilitazione;
}
