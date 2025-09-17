package it.gov.pagopa.payhub.activities.dto.exportflow.debtposition;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileVersions.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaidInstallmentExportFlowFileDTO {

    @CsvBindByName(column = "IUF")
    private String iuf;
    @CsvBindByName(column = "numRigaFlusso")
    private Integer flowRowNumber;
    @CsvBindByName(column = "codIud", required = true)
    private String iud;
    @CsvBindByName(column = "codIuv", required = true)
    private String iuv;
    @CsvBindByName(column = "versioneOggetto")
    private Integer objectVersion;
    @CsvBindByName(column = "identificativoDominio", required = true)
    private String domainIdentifier;
    @CsvBindByName(column = "identificativoStazioneRichiedente")
    private String requestingStationIdentifier;
    @CsvBindByName(column = "identificativoMessaggioRicevuta", required = true)
    private String receiptMessageIdentifier;
    @CsvBindByName(column = "dataOraMessaggioRicevuta")
    private OffsetDateTime receiptMessageDateTime;
    @CsvBindByName(column = "riferimentoMessaggioRichiesta", required = true)
    private String requestMessageReference;
    @CsvBindByName(column = "riferimentoDataRichiesta")
    private OffsetDateTime requestDateTimeReference;
    @CsvBindByName(column = "tipoIdentificativoUnivoco")
    private UniqueIdentifierType uniqueIdentifierType;
    @CsvBindByName(column = "codiceIdentificativoUnivoco")
    private String uniqueIdentifierCode;
    @CsvBindByName(column = "denominazioneAttestante", required = true)
    private String attestingName;
    @CsvBindByName(column = "codiceUnitOperAttestante")
    private String attestingUnitOperCode;
    @CsvBindByName(column = "denomUnitOperAttestante")
    private String attestingUnitOperName;
    @CsvBindByName(column = "indirizzoAttestante")
    private String attestingAddress;
    @CsvBindByName(column = "civicoAttestante")
    private String attestingStreetNumber;
    @CsvBindByName(column = "capAttestante")
    private String attestingPostalCode;
    @CsvBindByName(column = "localitaAttestante")
    private String attestingCity;
    @CsvBindByName(column = "provinciaAttestante")
    private String attestingProvince;
    @CsvBindByName(column = "nazioneAttestante")
    private String attestingCountry;
    @CsvBindByName(column = "enteBenefTipoIdentificativoUnivoco")
    private PersonEntityType beneficiaryEntityType;
    @CsvBindByName(column = "enteBenefCodiceIdentificativoUnivoco")
    private String beneficiaryUniqueIdentifierCode;
    @CsvBindByName(column = "denominazioneBeneficiario")
    private String beneficiaryName;
    @CsvBindByName(column = "codiceUnitOperBeneficiario")
    private String beneficiaryUnitOperCode;
    @CsvBindByName(column = "denomUnitOperBeneficiario")
    private String beneficiaryUnitOperName;
    @CsvBindByName(column = "indirizzoBeneficiario")
    private String beneficiaryAddress;
    @CsvBindByName(column = "civicoBeneficiario")
    private String beneficiaryStreetNumber;
    @CsvBindByName(column = "capBeneficiario")
    private String beneficiaryPostalCode;
    @CsvBindByName(column = "localitaBeneficiario")
    private String beneficiaryCity;
    @CsvBindByName(column = "provinciaBeneficiario")
    private String beneficiaryProvince;
    @CsvBindByName(column = "nazioneBeneficiario")
    private String beneficiaryCountry;
    @CsvBindByName(column = "soggVersTipoIdentificativoUnivoco")
    private PersonEntityType payerEntityType;
    @CsvBindByName(column = "soggVersCodiceIdentificativoUnivoco")
    private String payerUniqueIdentifierCode;
    @CsvBindByName(column = "anagraficaVersante")
    private String payerFullName;
    @CsvBindByName(column = "indirizzoVersante")
    private String payerAddress;
    @CsvBindByName(column = "civicoVersante")
    private String payerStreetNumber;
    @CsvBindByName(column = "capVersante")
    private String payerPostalCode;
    @CsvBindByName(column = "localitaVersante")
    private String payerCity;
    @CsvBindByName(column = "provinciaVersante")
    private String payerProvince;
    @CsvBindByName(column = "nazioneVersante")
    private String payerCountry;
    @CsvBindByName(column = "emailVersante")
    private String payerEmail;
    @CsvBindByName(column = "soggPagTipoIdentificativoUnivoco", required = true)
    private PersonEntityType debtorEntityType;
    @CsvBindByName(column = "soggPagCodiceIdentificativoUnivoco", required = true)
    private String debtorUniqueIdentifierCode;
    @CsvBindByName(column = "anagraficaPagatore")
    private String debtorFullName;
    @CsvBindByName(column = "indirizzoPagatore")
    private String debtorAddress;
    @CsvBindByName(column = "civicoPagatore")
    private String debtorStreetNumber;
    @CsvBindByName(column = "capPagatore")
    private String debtorPostalCode;
    @CsvBindByName(column = "localitaPagatore")
    private String debtorCity;
    @CsvBindByName(column = "provinciaPagatore")
    private String debtorProvince;
    @CsvBindByName(column = "nazionePagatore")
    private String debtorCountry;
    @CsvBindByName(column = "emailPagatore")
    private String debtorEmail;
    @CsvBindByName(column = "codiceEsitoPagamento")
    private Integer paymentOutcomeCode;
    @CsvBindByName(column = "importoTotalePagato")
    private BigDecimal totalAmountPaid;
    @CsvBindByName(column = "identificativoUnivocoVersamento", required = true)
    private String uniquePaymentIdentifier;
    @CsvBindByName(column = "codiceContestoPagamento")
    private String paymentContextCode;
    @CsvBindByName(column = "singoloImportoPagato", required = true)
    private BigDecimal singleAmountPaid;
    @CsvBindByName(column = "esitoSingoloPagamento")
    private String singlePaymentOutcome;
    @CsvBindByName(column = "dataEsitoSingoloPagamento")
    private OffsetDateTime singlePaymentOutcomeDateTime;
    @CsvBindByName(column = "identificativoUnivocoRiscos", required = true)
    private String uniqueCollectionIdentifier;
    @CsvBindByName(column = "causaleVersamento", required = true)
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private String paymentReason;
    @CsvBindByName(column = "datiSpecificiRiscossione", required = true)
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private String collectionSpecificData;
    @CsvBindByName(column = "tipoDovuto", required = true)
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private String dueType;
    @CsvBindByName(column = "tipoFirma")
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private Integer signatureType;
    @CsvBindByName(column = "rt")
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private String rt;
    @CsvBindByName(column = "indiceDatiSingoloPagamento" , required = true)
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private Integer singlePaymentDataIndex;
    @CsvBindByName(column = "numRtDatiPagDatiSingPagCommissioniApplicatePsp")
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private BigDecimal pspAppliedFees;
    @CsvBindByName(column = "codRtDatiPagDatiSingPagAllegatoRicevutaTipo")
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private String receiptAttachmentType;
    @CsvBindByName(column = "blbRtDatiPagDatiSingPagAllegatoRicevutaTest")
    @CsvIgnore(profiles = EXPORT_PAID_VERSION_V1)
    private String receiptAttachmentTest;
    @CsvBindByName(column = "bilancio")
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1})
    private String balance;
    @CsvBindByName(column = "cod_fiscale_pa1",  required = true)
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2})
    private String orgFiscalCode;
    @CsvBindByName(column = "de_nome_pa1")
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2})
    private String orgName;
    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1" )
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2})
    private String dueTaxonomicCode;
    @CsvBindByName(column = "codIun" )
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2, EXPORT_PAID_VERSION_V1_3})
    private String codIun;
    @CsvBindByName(column = "dataNotifica" )
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2, EXPORT_PAID_VERSION_V1_3})
    private OffsetDateTime notificationDate;
    @CsvBindByName(column = "costoNotifica" )
    @CsvIgnore(profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2, EXPORT_PAID_VERSION_V1_3})
    private Long notificationFeeCents;
}
