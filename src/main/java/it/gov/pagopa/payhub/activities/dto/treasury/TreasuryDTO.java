package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for the TreasuryDto
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryDTO {
    private Long mygovFlussoTesoreriaId;
    private String deAnnoBolletta;
    private String codBolletta;
    private String codConto;
    private String codIdDominio;
    private String codTipoMovimento;
    private String codCausale;
    private String deCausale;
    private BigDecimal numIpBolletta;
    private Date dtBolletta;
    private Date dtRicezione;
    private String deAnnoDocumento;
    private String codDocumento;
    private String codBollo;
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
    private String deAeProvvisorio;
    private String codProvvisorio;
    private String codIban;
    private Character codTipoConto;
    private String codProcesso;
    private String codPgEsecuzione;
    private String codPgTrasferimento;
    private Long numPgProcesso;
    private Date dtDataValutaRegione;
    private Long mygovEnteId;
    private String codIdUnivocoFlusso;
    private String codIdUnivocoVersamento;
    private Date dtCreazione;
    private Date dtUltimaModifica;
    private boolean flgRegolarizzata;
    private Long mygovManageFlussoId;
    private Date dtEffettivaSospeso;
    private String codiceGestionaleProvvisorio;
    private String endToEndId;
}
