package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonEntityType;
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

    @CsvBindByName(column = "iuf")
    private String sourceFlowName;

    @CsvBindByName(column = "numRigaFlusso")
    private Integer flowRowNumber;

    @CsvBindByName(column = "codIud", required = true)
    private String iud;

    @CsvBindByName(column = "codIuv", required = true)
    private String noticeNumber;

    @CsvBindByName(column = "versioneOggetto")
    private String objectVersion;

    @CsvBindByName(column = "identificativoDominio", required = true)
    private String orgFiscalCode;

    @CsvBindByName(column = "identificativoStazioneRichiedente")
    private String requestingStationId;

    @CsvBindByName(column = "identificativoMessaggioRicevuta", required = true)
    private String paymentReceiptId;

    @CsvBindByName(column = "dataOraMessaggioRicevuta", required = true)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDateTime;

    @CsvBindByName(column = "riferimentoMessaggioRichiesta")
    private String requestMessageReference;

    @CsvBindByName(column = "riferimentoDataRichiesta")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate requestDateReference;

    @CsvBindByName(column = "tipoIdentificativoUnivoco")
    private String uniqueIdType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", required = true)
    private String idPsp;

    @CsvBindByName(column = "denominazioneAttestante", required = true)
    private String pspCompanyName;

    @CsvBindByName(column = "codiceUnitOperAttestante")
    private String certifierOperationalUnitCode;

    @CsvBindByName(column = "denomUnitOperAttestante")
    private String certifierOperationalUnitName;

    @CsvBindByName(column = "indirizzoAttestante")
    private String certifierAddress;

    @CsvBindByName(column = "civicoAttestante")
    private String certifierCivicNumber;

    @CsvBindByName(column = "capAttestante")
    private String certifierPostalCode;

    @CsvBindByName(column = "localitaAttestante")
    private String certifierLocation;

    @CsvBindByName(column = "provinciaAttestante")
    private String certifierProvince;

    @CsvBindByName(column = "nazioneAttestante")
    private String certifierNation;

    @CsvBindByName(column = "enteBenefTipoIdentificativoUnivoco")
    private EntityIdType beneficiaryEntityIdType;

    @CsvBindByName(column = "enteBenefCodiceIdentificativoUnivoco")
    private String beneficiaryEntityIdCode;

    @CsvBindByName(column = "denominazioneBeneficiario", required = true)
    private String beneficiaryCompanyName;

    @CsvBindByName(column = "codiceUnitOperBeneficiario")
    private String beneficiaryOperationalUnitCode;

    @CsvBindByName(column = "denomUnitOperBeneficiario")
    private String beneficiaryOperationalUnitName;

    @CsvBindByName(column = "indirizzoBeneficiario")
    private String beneficiaryAddress;

    @CsvBindByName(column = "civicoBeneficiario")
    private String beneficiaryCivic;

    @CsvBindByName(column = "capBeneficiario")
    private String beneficiaryPostalCode;

    @CsvBindByName(column = "localitaBeneficiario")
    private String beneficiaryCity;

    @CsvBindByName(column = "provinciaBeneficiario")
    private String beneficiaryProvince;

    @CsvBindByName(column = "nazioneBeneficiario")
    private String beneficiaryNation;

    @CsvBindByName(column = "soggVersTipoIdentificativoUnivoco")
    private PersonEntityType payerEntityType;

    @CsvBindByName(column = "soggVersCodiceIdentificativoUnivoco")
    private String payerFiscalCode;

    @CsvBindByName(column = "anagraficaVersante")
    private String payerFullName;

    @CsvBindByName(column = "indirizzoVersante")
    private String payerAddress;

    @CsvBindByName(column = "civicoVersante")
    private String payerCivic;

    @CsvBindByName(column = "capVersante")
    private String payerPostalCode;

    @CsvBindByName(column = "localitaVersante")
    private String payerLocation;

    @CsvBindByName(column = "provinciaVersante")
    private String payerProvince;

    @CsvBindByName(column = "nazioneVersante")
    private String payerNation;

    @CsvBindByName(column = "emailVersante")
    private String payerEmail;

    @CsvBindByName(column = "soggPagTipoIdentificativoUnivoco", required = true)
    private PersonEntityType debtorEntityType;

    @CsvBindByName(column = "soggPagCodiceIdentificativoUnivoco", required = true)
    private String debtorFiscalCode;

    @CsvBindByName(column = "anagraficaPagatore", required = true)
    private String debtorFullName;

    @CsvBindByName(column = "indirizzoPagatore")
    private String debtorAddress;

    @CsvBindByName(column = "civicoPagatore")
    private String debtorCivic;

    @CsvBindByName(column = "capPagatore")
    private String debtorPostalCode;

    @CsvBindByName(column = "localitaPagatore")
    private String debtorLocation;

    @CsvBindByName(column = "provinciaPagatore")
    private String debtorProvince;

    @CsvBindByName(column = "nazionePagatore")
    private String debtorNation;

    @CsvBindByName(column = "emailPagatore")
    private String debtorEmail;

    @CsvBindByName(column = "codiceEsitoPagamento", required = true)
    private String outcome;

    @CsvBindByName(column = "importoTotalePagato", required = true)
    private BigDecimal paymentAmountCents;

    @CsvBindByName(column = "identificativoUnivocoVersamento", required = true)
    private String creditorReferenceId;

    @CsvBindByName(column = "codiceContestoPagamento")
    private String paymentContextCode;

    @CsvBindByName(column = "singoloImportoPagato")
    private BigDecimal singlePaymentAmount;

    @CsvBindByName(column = "esitoSingoloPagamento")
    private String singlePaymentOutcome;

    @CsvBindByName(column = "dataEsitoSingoloPagamento")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate singlePaymentOutcomeDate;

    @CsvBindByName(column = "identificativoUnivocoRiscoss")
    private String uniqueCollectionId;

    @CsvBindByName(column = "causaleVersamento", required = true)
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione")
    private String paymentNote;

    @CsvBindByName(column = "tipoDovuto")
    private String debtPositionTypeOrgCode;

    @CsvBindByName(column = "tipoFirma")
    private String signatureType;

    @CsvBindByName(column = "rt")
    private String rt;

    @CsvBindByName(column = "indiceDatiSingoloPagamento", required = true)
    private Integer idTransfer;

    @CsvBindByName(column = "numRtDatiPagDatiSingPagCommissioniApplicatePsp")
    private BigDecimal feeCents;

    @CsvBindByName(column = "codRtDatiPagDatiSingPagAllegatoRicevutaTipo")
    private String receiptAttachmentTypeCode;

    @CsvBindByName(column = "blbRtDatiPagDatiSingPagAllegatoRicevutaTest")
    private String mbdAttachment;

    @CsvBindByName(column = "bilancio")
    private String balance;

    @CsvBindByName(column = "cod_fiscale_pa1", required = true)
    private String fiscalCodePA;

    @CsvBindByName(column = "de_nome_pa1")
    private String companyName;

    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1", required = true)
    private String transferCategory;

    public enum EntityIdType {
        G
    }
}

