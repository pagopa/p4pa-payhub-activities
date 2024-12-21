package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlussoTesoreriaPIIDTO {
  private String deCausale;
  private String deCognome;
  private String deNome;
  private String deVia;
  private String deCap;
  private String deCitta;
  private String codCodiceFiscale;
  private String codPartitaIva;
  private String codAbi;
  private String codCab;
  private String codContoAnagrafica;
  private String codIban;
}
