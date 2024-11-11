package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentsOperatorDTO implements Serializable {
  private Long id;
  private String codFiscale;
  private String iud;
  private String iuv;
  private String causale;
  private String causaleVisualizzata;
  private String importo;
  private LocalDate dataScadenza;
  private String stato;
  private String codStato;
  private LocalDateTime dataStato;
  private boolean hasAvviso;
  private boolean hasRicevuta;


  //details
  private OrganizationTypeInstallmentDTO tipoDovuto;
  private String anagrafica;
  private String tipoSoggetto;
  private boolean flgAnagraficaAnonima;
  private boolean hasCodFiscale;
  private String email;
  private String indirizzo;
  private String numCiv;
  private String cap;
  private NationDTO nazione;
  private ProvinceDTO prov;
  private CityDTO comune;
  private boolean flgGenerateIuv;
  private String iuf;

  //datails dovutoElaborato
  private LocalDateTime dataInizioTransazione;
  private String identificativoTransazione;
  private String intestatario;
  private String pspScelto;

  private String dovutoType; // "debito" or "pagato"

  private String invalidDesc; // Message thrown by ValidatorException when insertion, update.
  
  private boolean flgMultibeneficiario;
  private boolean flgIuvVolatile;

  //Ente primario detail
  private InstallmentPrimaryOrganizationDTO entePrimarioDetail;
  private InstallmentElaboratedPrimaryOrganization entePrimarioElaboratoDetail;
  
  //dovuto multibeneficiario
  private InstallmentMultiBeneficiaryDTO dovutoMultibeneficiario;
  private InstallmentMultiBeneficiaryElaborated dovutoMultibeneficiarioElaborato;
  
}
