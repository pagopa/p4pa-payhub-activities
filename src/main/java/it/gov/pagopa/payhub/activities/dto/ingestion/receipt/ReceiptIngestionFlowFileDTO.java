package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import it.gov.pagopa.pu.debtposition.dto.generated.EntityTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptIngestionFlowFileDTO {

    @CsvBindByName(column = "IUF")
    private String iuf;

    @CsvBindByName(column = "numRigaFlusso")
    private Integer numRigaFlusso;

    @CsvBindByName(column = "codIud", required = true)
    private String codIud;

    @CsvBindByName(column = "codIuv", required = true)
    private String codIuv;

    @CsvBindByName(column = "versioneOggetto")
    private String versioneOggetto;

    @CsvBindByName(column = "identificativoDominio", required = true)
    private String identificativoDominio;

    @CsvBindByName(column = "identificativoStazioneRichiedente")
    private String identificativoStazioneRichiedente;

    @CsvBindByName(column = "identificativoMessaggioRicevuta", required = true)
    private String identificativoMessaggioRicevuta;

    @CsvBindByName(column = "dataOraMessaggioRicevuta", required = true)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataOraMessaggioRicevuta;

    @CsvBindByName(column = "riferimentoMessaggioRichiesta")
    private String riferimentoMessaggioRichiesta;

    @CsvBindByName(column = "riferimentoDataRichiesta")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate riferimentoDataRichiesta;

    @CsvBindByName(column = "tipoIdentificativoUnivoco")
    private TipoIUEnum tipoIdentificativoUnivoco;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", required = true)
    private String codiceIdentificativoUnivoco;

    @CsvBindByName(column = "denominazioneAttestante", required = true)
    private String denominazioneAttestante;

    @CsvBindByName(column = "codiceUnitOperAttestante")
    private String codiceUnitOperAttestante;

    @CsvBindByName(column = "denomUnitOperAttestante")
    private String denomUnitOperAttestante;

    @CsvBindByName(column = "indirizzoAttestante")
    private String indirizzoAttestante;

    @CsvBindByName(column = "civicoAttestante")
    private String civicoAttestante;

    @CsvBindByName(column = "capAttestante")
    private String capAttestante;

    @CsvBindByName(column = "localitaAttestante")
    private String localitaAttestante;

    @CsvBindByName(column = "provinciaAttestante")
    private String provinciaAttestante;

    @CsvBindByName(column = "nazioneAttestante")
    private String nazioneAttestante;

    @CsvBindByName(column = "enteBenefTipoIdentificativoUnivoco")
    private TipoEnteIUEnum enteBenefTipoIdentificativoUnivoco;

    @CsvBindByName(column = "enteBenefCodiceIdentificativoUnivoco")
    private String enteBenefCodiceIdentificativoUnivoco;

    @CsvBindByName(column = "denominazioneBeneficiario", required = true)
    private String denominazioneBeneficiario;

    @CsvBindByName(column = "codiceUnitOperBeneficiario")
    private String codiceUnitOperBeneficiario;

    @CsvBindByName(column = "denomUnitOperBeneficiario")
    private String denomUnitOperBeneficiario;

    @CsvBindByName(column = "indirizzoBeneficiario")
    private String indirizzoBeneficiario;

    @CsvBindByName(column = "civicoBeneficiario")
    private String civicoBeneficiario;

    @CsvBindByName(column = "capBeneficiario")
    private String capBeneficiario;

    @CsvBindByName(column = "localitaBeneficiario")
    private String localitaBeneficiario;

    @CsvBindByName(column = "provinciaBeneficiario")
    private String provinciaBeneficiario;

    @CsvBindByName(column = "nazioneBeneficiario")
    private String nazioneBeneficiario;

    @CsvBindByName(column = "soggVersTipoIdentificativoUnivoco")
    private EntityTypeEnum soggVersTipoIdentificativoUnivoco;

    @CsvBindByName(column = "soggVersCodiceIdentificativoUnivoco")
    private String soggVersCodiceIdentificativoUnivoco;

    @CsvBindByName(column = "anagraficaVersante")
    private String anagraficaVersante;

    @CsvBindByName(column = "indirizzoVersante")
    private String indirizzoVersante;

    @CsvBindByName(column = "civicoVersante")
    private String civicoVersante;

    @CsvBindByName(column = "capVersante")
    private String capVersante;

    @CsvBindByName(column = "localitaVersante")
    private String localitaVersante;

    @CsvBindByName(column = "provinciaVersante")
    private String provinciaVersante;

    @CsvBindByName(column = "nazioneVersante")
    private String nazioneVersante;

    @CsvBindByName(column = "emailVersante")
    private String emailVersante;

    @CsvBindByName(column = "soggPagTipoIdentificativoUnivoco", required = true)
    private EntityTypeEnum soggPagTipoIdentificativoUnivoco;

    @CsvBindByName(column = "soggPagCodiceIdentificativoUnivoco", required = true)
    private String soggPagCodiceIdentificativoUnivoco;

    @CsvBindByName(column = "anagraficaPagatore", required = true)
    private String anagraficaPagatore;

    @CsvBindByName(column = "indirizzoPagatore")
    private String indirizzoPagatore;

    @CsvBindByName(column = "civicoPagatore")
    private String civicoPagatore;

    @CsvBindByName(column = "capPagatore")
    private String capPagatore;

    @CsvBindByName(column = "localitaPagatore")
    private String localitaPagatore;

    @CsvBindByName(column = "provinciaPagatore")
    private String provinciaPagatore;

    @CsvBindByName(column = "nazionePagatore")
    private String nazionePagatore;

    @CsvBindByName(column = "emailPagatore")
    private String emailPagatore;

    @CsvBindByName(column = "codiceEsitoPagamento", required = true)
    private String codiceEsitoPagamento;

    @CsvBindByName(column = "importoTotalePagato", required = true)
    private Long importoTotalePagato;

    @CsvBindByName(column = "identificativoUnivocoVersamento", required = true)
    private String identificativoUnivocoVersamento;

    @CsvBindByName(column = "codiceContestoPagamento")
    private String codiceContestoPagamento;

    @CsvBindByName(column = "singoloImportoPagato")
    private String singoloImportoPagato;

    @CsvBindByName(column = "esitoSingoloPagamento")
    private String esitoSingoloPagamento;

    @CsvBindByName(column = "dataEsitoSingoloPagamento")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate dataEsitoSingoloPagamento;

    @CsvBindByName(column = "identificativoUnivocoRiscoss")
    private String identificativoUnivocoRiscoss;

    @CsvBindByName(column = "causaleVersamento", required = true)
    private String causaleVersamento;

    @CsvBindByName(column = "datiSpecificiRiscossione")
    private String datiSpecificiRiscossione;

    @CsvBindByName(column = "tipoDovuto")
    private String tipoDovuto;

    @CsvBindByName(column = "tipoFirma")
    private String tipoFirma;

    @CsvBindByName(column = "rt")
    private String rt;

    @CsvBindByName(column = "indiceDatiSingoloPagamento", required = true)
    private Integer indiceDatiSingoloPagamento;

    @CsvBindByName(column = "numRtDatiPagDatiSingPagCommissioniApplicatePsp")
    private BigDecimal numRtDatiPagDatiSingPagCommissioniApplicatePsp;

    @CsvBindByName(column = "codRtDatiPagDatiSingPagAllegatoRicevutaTipo")
    private String codRtDatiPagDatiSingPagAllegatoRicevutaTipo;

    @CsvBindByName(column = "blbRtDatiPagDatiSingPagAllegatoRicevutaTest")
    private String blbRtDatiPagDatiSingPagAllegatoRicevutaTest;

    @CsvBindByName(column = "bilancio")
    private String bilancio;

    @CsvBindByName(column = "cod_fiscale_pa1", required = true)
    private String codFiscalePa1;

    @CsvBindByName(column = "de_nome_pa1")
    private String deNomePa1;

    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1", required = true)
    private String codTassonomicoDovutoPa1;

    public enum TipoIUEnum {
        A, B
    }

    public enum TipoEnteIUEnum {
        G
    }
}

