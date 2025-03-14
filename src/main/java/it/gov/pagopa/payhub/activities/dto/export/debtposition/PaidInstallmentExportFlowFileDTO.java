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
public class PaidInstallmentExportFlowFileDTO {
    private static final String EXPORT_PAID_VERSION_V1= "v1.0";
    private static final String EXPORT_PAID_VERSION_V1_1= "v1.1";
    private static final String EXPORT_PAID_VERSION_V1_2= "v1.2";
    private static final String EXPORT_PAID_VERSION_V1_3= "v1.3";

    @CsvBindByName(column = "IUF", profiles = EXPORT_PAID_VERSION_V1 )
    private String iuf;
    @CsvBindByName(column = "numRigaFlusso", profiles = EXPORT_PAID_VERSION_V1)
    private Integer flowRowNumber;
    @CsvBindByName(column = "codIud", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String iud;
    @CsvBindByName(column = "codIuv", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String iuv;
    @CsvBindByName(column = "versioneOggetto", profiles = EXPORT_PAID_VERSION_V1)
    private Integer objectVersion;
    @CsvBindByName(column = "identificativoDominio", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String domainIdentifier;
    @CsvBindByName(column = "identificativoStazioneRichiedente", profiles = EXPORT_PAID_VERSION_V1)
    private String requestingStationIdentifier;
    @CsvBindByName(column = "identificativoMessaggioRicevuta", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String receiptMessageIdentifier;
    @CsvBindByName(column = "dataOraMessaggioRicevuta", profiles = EXPORT_PAID_VERSION_V1)
    private OffsetDateTime receiptMessageDateTime;
    @CsvBindByName(column = "riferimentoMessaggioRichiesta", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String requestMessageReference;
    @CsvBindByName(column = "riferimentoDataRichiesta", profiles = EXPORT_PAID_VERSION_V1)
    private OffsetDateTime requestDateTimeReference;
    @CsvBindByName(column = "tipoIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1)
    private UniqueIdentifierType uniqueIdentifierType;
    @CsvBindByName(column = "codiceIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1)
    private String uniqueIdentifierCode;
    @CsvBindByName(column = "denominazioneAttestante", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String attestingName;
    @CsvBindByName(column = "codiceUnitOperAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingUnitOperCode;
    @CsvBindByName(column = "denomUnitOperAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingUnitOperName;
    @CsvBindByName(column = "indirizzoAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingAddress;
    @CsvBindByName(column = "civicoAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingStreetNumber;
    @CsvBindByName(column = "capAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingPostalCode;
    @CsvBindByName(column = "localitaAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingCity;
    @CsvBindByName(column = "provinciaAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingProvince;
    @CsvBindByName(column = "nazioneAttestante", profiles = EXPORT_PAID_VERSION_V1)
    private String attestingCountry;
    @CsvBindByName(column = "enteBenefTipoIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1)
    private EntityIdentifierType beneficiaryEntityType;
    @CsvBindByName(column = "enteBenefCodiceIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryUniqueIdentifierCode;
    @CsvBindByName(column = "denominazioneBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryName;
    @CsvBindByName(column = "codiceUnitOperBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryUnitOperCode;
    @CsvBindByName(column = "denomUnitOperBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryUnitOperName;
    @CsvBindByName(column = "indirizzoBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryAddress;
    @CsvBindByName(column = "civicoBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryStreetNumber;
    @CsvBindByName(column = "capBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryPostalCode;
    @CsvBindByName(column = "localitaBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryCity;
    @CsvBindByName(column = "provinciaBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryProvince;
    @CsvBindByName(column = "nazioneBeneficiario", profiles = EXPORT_PAID_VERSION_V1)
    private String beneficiaryCountry;
    @CsvBindByName(column = "soggVersTipoIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1)
    private EntityIdentifierType payerEntityType;
    @CsvBindByName(column = "soggVersCodiceIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1)
    private String payerUniqueIdentifierCode;
    @CsvBindByName(column = "anagraficaVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerFullName;
    @CsvBindByName(column = "indirizzoVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerAddress;
    @CsvBindByName(column = "civicoVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerStreetNumber;
    @CsvBindByName(column = "capVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerPostalCode;
    @CsvBindByName(column = "localitaVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerCity;
    @CsvBindByName(column = "provinciaVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerProvince;
    @CsvBindByName(column = "nazioneVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerCountry;
    @CsvBindByName(column = "emailVersante", profiles = EXPORT_PAID_VERSION_V1)
    private String payerEmail;
    @CsvBindByName(column = "soggPagTipoIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private EntityIdentifierType debtorEntityType;
    @CsvBindByName(column = "soggPagCodiceIdentificativoUnivoco", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String debtorUniqueIdentifierCode;
    @CsvBindByName(column = "anagraficaPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorFullName;
    @CsvBindByName(column = "indirizzoPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorAddress;
    @CsvBindByName(column = "civicoPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorStreetNumber;
    @CsvBindByName(column = "capPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorPostalCode;
    @CsvBindByName(column = "localitaPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorCity;
    @CsvBindByName(column = "provinciaPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorProvince;
    @CsvBindByName(column = "nazionePagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorCountry;
    @CsvBindByName(column = "emailPagatore", profiles = EXPORT_PAID_VERSION_V1)
    private String debtorEmail;
    @CsvBindByName(column = "codiceEsitoPagamento", profiles = EXPORT_PAID_VERSION_V1)
    private Integer paymentOutcomeCode;
    @CsvBindByName(column = "importoTotalePagato", profiles = EXPORT_PAID_VERSION_V1)
    private BigDecimal totalAmountPaid;
    @CsvBindByName(column = "identificativoUnivocoVersamento", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String uniquePaymentIdentifier;
    @CsvBindByName(column = "codiceContestoPagamento", profiles = EXPORT_PAID_VERSION_V1)
    private String paymentContextCode;
    @CsvBindByName(column = "singoloImportoPagato", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private BigDecimal singleAmountPaid;
    @CsvBindByName(column = "esitoSingoloPagamento", profiles = EXPORT_PAID_VERSION_V1)
    private String singlePaymentOutcome;
    @CsvBindByName(column = "dataEsitoSingoloPagamento", profiles = EXPORT_PAID_VERSION_V1)
    private OffsetDateTime singlePaymentOutcomeDateTime;
    @CsvBindByName(column = "identificativoUnivocoRiscos", profiles = EXPORT_PAID_VERSION_V1, required = true)
    private String uniqueCollectionIdentifier;
    @CsvBindByName(column = "causaleVersamento", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1}, required = true)
    private String paymentReason;
    @CsvBindByName(column = "datiSpecificiRiscossione", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} , required = true)
    private String collectionSpecificData;
    @CsvBindByName(column = "tipoDovuto", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} , required = true)
    private String dueType;
    @CsvBindByName(column = "tipoFirma", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} , required = true)
    private Integer signatureType;
    @CsvBindByName(column = "rt", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} )
    private String rt;
    @CsvBindByName(column = "indiceDatiSingoloPagamento", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} , required = true)
    private Integer singlePaymentDataIndex;
    @CsvBindByName(column = "numRtDatiPagDatiSingPagCommissioniApplicatePsp", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} )
    private BigDecimal pspAppliedFees;
    @CsvBindByName(column = "codRtDatiPagDatiSingPagAllegatoRicevutaTipo", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} )
    private String receiptAttachmentType;
    @CsvBindByName(column = "blbRtDatiPagDatiSingPagAllegatoRicevutaTest", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1} )
    private String receiptAttachmentTest;
    @CsvBindByName(column = "bilancio", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2} )
    private String balance;
    @CsvBindByName(column = "cod_fiscale_pa1", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2, EXPORT_PAID_VERSION_V1_3}, required = true)
    private String orgFiscalCode;
    @CsvBindByName(column = "de_nome_pa1", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2, EXPORT_PAID_VERSION_V1_3})
    private String orgName;
    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1", profiles = {EXPORT_PAID_VERSION_V1, EXPORT_PAID_VERSION_V1_1, EXPORT_PAID_VERSION_V1_2, EXPORT_PAID_VERSION_V1_3})
    private String dueTaxonomicCode;

}
