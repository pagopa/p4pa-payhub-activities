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

import static it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileVersions.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptIngestionFlowFileDTO {

    @CsvBindByName(column = "iuf", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String sourceFlowName;

    @CsvBindByName(column = "numRigaFlusso", profiles = {V1_0, V1_1, V1_2, V1_3})
    private Integer flowRowNumber;

    @CsvBindByName(column = "codIud", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String iud;

    @CsvBindByName(column = "codIuv", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String noticeNumber;

    @CsvBindByName(column = "versioneOggetto", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String objectVersion;

    @CsvBindByName(column = "identificativoDominio", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String orgFiscalCode;

    @CsvBindByName(column = "identificativoStazioneRichiedente", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String requestingStationId;

    @CsvBindByName(column = "identificativoMessaggioRicevuta", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String paymentReceiptId;

    @CsvBindByName(column = "dataOraMessaggioRicevuta", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDateTime;

    @CsvBindByName(column = "riferimentoMessaggioRichiesta", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String requestMessageReference;

    @CsvBindByName(column = "riferimentoDataRichiesta", profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate requestDateReference;

    @CsvBindByName(column = "tipoIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String uniqueIdType;

    @CsvBindByName(column = "codiceIdentificativoUnivoco", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String idPsp;

    @CsvBindByName(column = "denominazioneAttestante", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String pspCompanyName;

    @CsvBindByName(column = "codiceUnitOperAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierOperationalUnitCode;

    @CsvBindByName(column = "denomUnitOperAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierOperationalUnitName;

    @CsvBindByName(column = "indirizzoAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierAddress;

    @CsvBindByName(column = "civicoAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierCivicNumber;

    @CsvBindByName(column = "capAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierPostalCode;

    @CsvBindByName(column = "localitaAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierLocation;

    @CsvBindByName(column = "provinciaAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierProvince;

    @CsvBindByName(column = "nazioneAttestante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String certifierNation;

    @CsvBindByName(column = "enteBenefTipoIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3})
    private EntityIdType beneficiaryEntityIdType;

    @CsvBindByName(column = "enteBenefCodiceIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryEntityIdCode;

    @CsvBindByName(column = "denominazioneBeneficiario", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryCompanyName;

    @CsvBindByName(column = "codiceUnitOperBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryOperationalUnitCode;

    @CsvBindByName(column = "denomUnitOperBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryOperationalUnitName;

    @CsvBindByName(column = "indirizzoBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryAddress;

    @CsvBindByName(column = "civicoBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryCivic;

    @CsvBindByName(column = "capBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryPostalCode;

    @CsvBindByName(column = "localitaBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryCity;

    @CsvBindByName(column = "provinciaBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryProvince;

    @CsvBindByName(column = "nazioneBeneficiario", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String beneficiaryNation;

    @CsvBindByName(column = "soggVersTipoIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3})
    private PersonEntityType payerEntityType;

    @CsvBindByName(column = "soggVersCodiceIdentificativoUnivoco", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerFiscalCode;

    @CsvBindByName(column = "anagraficaVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerFullName;

    @CsvBindByName(column = "indirizzoVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerAddress;

    @CsvBindByName(column = "civicoVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerCivic;

    @CsvBindByName(column = "capVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerPostalCode;

    @CsvBindByName(column = "localitaVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerLocation;

    @CsvBindByName(column = "provinciaVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerProvince;

    @CsvBindByName(column = "nazioneVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerNation;

    @CsvBindByName(column = "emailVersante", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String payerEmail;

    @CsvBindByName(column = "soggPagTipoIdentificativoUnivoco", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private PersonEntityType debtorEntityType;

    @CsvBindByName(column = "soggPagCodiceIdentificativoUnivoco", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorFiscalCode;

    @CsvBindByName(column = "anagraficaPagatore", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorFullName;

    @CsvBindByName(column = "indirizzoPagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorAddress;

    @CsvBindByName(column = "civicoPagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorCivic;

    @CsvBindByName(column = "capPagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorPostalCode;

    @CsvBindByName(column = "localitaPagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorLocation;

    @CsvBindByName(column = "provinciaPagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorProvince;

    @CsvBindByName(column = "nazionePagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorNation;

    @CsvBindByName(column = "emailPagatore", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String debtorEmail;

    @CsvBindByName(column = "codiceEsitoPagamento", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String outcome;

    @CsvBindByName(column = "importoTotalePagato", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private BigDecimal paymentAmountCents;

    @CsvBindByName(column = "identificativoUnivocoVersamento", required = true, profiles = {V1_0, V1_1, V1_2, V1_3})
    private String creditorReferenceId;

    @CsvBindByName(column = "codiceContestoPagamento", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String paymentContextCode;

    @CsvBindByName(column = "singoloImportoPagato", profiles = {V1_0, V1_1, V1_2, V1_3})
    private BigDecimal singlePaymentAmount;

    @CsvBindByName(column = "esitoSingoloPagamento", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String singlePaymentOutcome;

    @CsvBindByName(column = "dataEsitoSingoloPagamento", profiles = {V1_0, V1_1, V1_2, V1_3})
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate singlePaymentOutcomeDate;

    @CsvBindByName(column = "identificativoUnivocoRiscoss", profiles = {V1_0, V1_1, V1_2, V1_3})
    private String uniqueCollectionId;

    @CsvBindByName(column = "causaleVersamento", required = true, profiles = {V1_1, V1_2, V1_3})
    private String remittanceInformation;

    @CsvBindByName(column = "datiSpecificiRiscossione", profiles = {V1_1, V1_2, V1_3})
    private String paymentNote;

    @CsvBindByName(column = "tipoDovuto", profiles = {V1_1, V1_2, V1_3})
    private String debtPositionTypeOrgCode;

    @CsvBindByName(column = "tipoFirma", profiles = {V1_1, V1_2, V1_3})
    private String signatureType;

    @CsvBindByName(column = "rt", profiles = {V1_1, V1_2, V1_3})
    private String rt;

    @CsvBindByName(column = "indiceDatiSingoloPagamento", required = true, profiles = {V1_1, V1_2, V1_3})
    private Integer idTransfer;

    @CsvBindByName(column = "numRtDatiPagDatiSingPagCommissioniApplicatePsp", profiles = {V1_1, V1_2, V1_3})
    private BigDecimal feeCents;

    @CsvBindByName(column = "codRtDatiPagDatiSingPagAllegatoRicevutaTipo", profiles = {V1_1, V1_2, V1_3})
    private String receiptAttachmentTypeCode;

    @CsvBindByName(column = "blbRtDatiPagDatiSingPagAllegatoRicevutaTest", profiles = {V1_1, V1_2, V1_3})
    private String mbdAttachment;

    @CsvBindByName(column = "bilancio", profiles = {V1_2, V1_3})
    private String balance;

    @CsvBindByName(column = "cod_fiscale_pa1", required = true, profiles = {V1_3})
    private String fiscalCodePA;

    @CsvBindByName(column = "de_nome_pa1", profiles = {V1_3})
    private String companyName;

    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1", required = true, profiles = {V1_3})
    private String transferCategory;

    public enum EntityIdType {
        G
    }
}

