package it.gov.pagopa.payhub.activities.dto.export.debtposition;

import com.opencsv.bean.CsvBindByName;
import it.gov.pagopa.payhub.activities.enums.EntityIdentifierType;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentExportFlowFileDTO {

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
    private EntityIdentifierType beneficiaryEntityType;
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
    private EntityIdentifierType payerEntityType;
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
    private EntityIdentifierType debtorEntityType;
    @CsvBindByName(column = "soggPagCodiceIdentificativoUnivoco", required = true)
    private String debtorIndentifierCode;
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
    private String paymentReason;
    @CsvBindByName(column = "datiSpecificiRiscossione", required = true)
    private String collectionSpecificData;
    @CsvBindByName(column = "tipoDovuto", required = true)
    private String dueType;
    @CsvBindByName(column = "tipoFirma", required = true)
    private Integer signatureType;
    @CsvBindByName(column = "rt")
    private String rt;
    @CsvBindByName(column = "indiceDatiSingoloPagamento", required = true)
    private Integer singlePaymentDataIndex;
    @CsvBindByName(column = "numRtDatiPagDatiSingPagCommissioniApplicatePsp")
    private BigDecimal pspAppliedFees;
    @CsvBindByName(column = "codRtDatiPagDatiSingPagAllegatoRicevutaTipo")
    private String receiptAttachmentType;
    @CsvBindByName(column = "blbRtDatiPagDatiSingPagAllegatoRicevutaTest")
    private String receiptAttachmentTest;
    @CsvBindByName(column = "bilancio")
    private String balance;
    @CsvBindByName(column = "cod_fiscale_pa1", required = true)
    private String orgFiscalCode;
    @CsvBindByName(column = "de_nome_pa1")
    private String orgName;
    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1")
    private String dueTaxonomicCode;

}
