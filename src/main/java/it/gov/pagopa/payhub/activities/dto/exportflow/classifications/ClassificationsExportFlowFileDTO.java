package it.gov.pagopa.payhub.activities.dto.exportflow.classifications;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import it.gov.pagopa.payhub.activities.dto.exportflow.ExportFileVersions;
import it.gov.pagopa.pu.classification.dto.generated.PersonEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificationsExportFlowFileDTO {

    @CsvBindByName(column = "de_nome_flusso_e")
    private String recFileName;

    @CsvBindByName(column = "num_riga_flusso_e")
    private Integer flowRowNumber;

    @CsvBindByName(column = "cod_iud_e")
    private String recIud;

    @CsvBindByName(column = "cod_rp_silinviarp_id_univoco_versamento_e")
    private String recIuv;

    @CsvBindByName(column = "de_e_versione_oggetto_e")
    private String objectVersion;

    @CsvBindByName(column = "cod_e_dom_id_dominio_e")
    private String recOrgFiscalCode;

    @CsvBindByName(column = "cod_e_dom_id_stazione_richiedente_e")
    private String requestingStationId;

    @CsvBindByName(column = "cod_e_id_messaggio_ricevuta_e")
    private String recPaymentReceiptId;

    @CsvBindByName(column = "dt_e_data_ora_messaggio_ricevuta_e")
    private OffsetDateTime recPaymentDateTime;

    @CsvBindByName(column = "cod_e_riferimento_messaggio_richiesta_e")
    private String requestMessageReferenceId;

    @CsvBindByName(column = "dt_e_riferimento_data_richiesta_e")
    private OffsetDateTime requestReferenceDate;

    @CsvBindByName(column = "cod_e_istit_att_id_univ_att_tipo_id_univoco_e")
    private String institutionAttTypeUniqueId;

    @CsvBindByName(column = "cod_e_istit_att_id_univ_att_codice_id_univoco_e")
    private String recPspId;

    @CsvBindByName(column = "de_e_istit_att_denominazione_attestante_e")
    private String recPspCompanyName;

    @CsvBindByName(column = "cod_e_istit_att_codice_unit_oper_attestante_e")
    private String institutionAttOperatingUnitCode;

    @CsvBindByName(column = "de_e_istit_att_denom_unit_oper_attestante_e")
    private String institutionAttOperatingUnitName;

    @CsvBindByName(column = "de_e_istit_att_indirizzo_attestante_e")
    private String institutionAttAddress;

    @CsvBindByName(column = "de_e_istit_att_civico_attestante_e")
    private String institutionAttCivicNumber;

    @CsvBindByName(column = "cod_e_istit_att_cap_attestante_e")
    private String institutionAttPostalCode;

    @CsvBindByName(column = "de_e_istit_att_localita_attestante_e")
    private String institutionAttCity;

    @CsvBindByName(column = "de_e_istit_att_provincia_attestante_e")
    private String institutionAttProvince;

    @CsvBindByName(column = "cod_e_istit_att_nazione_attestante_e")
    private String institutionAttCountry;

    @CsvBindByName(column = "cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e")
    private String beneficiaryUniqueIdType;

    @CsvBindByName(column = "cod_e_ente_benef_id_univ_benef_codice_id_univoco_e")
    private String beneficiaryUniqueIdCode;

    @CsvBindByName(column = "de_e_ente_benef_denominazione_beneficiario_e")
    private String recBeneficiaryName;

    @CsvBindByName(column = "cod_e_ente_benef_codice_unit_oper_beneficiario_e")
    private String beneficiaryOperatingUnitCode;

    @CsvBindByName(column = "de_e_ente_benef_denom_unit_oper_beneficiario_e")
    private String beneficiaryOperatingUnitName;

    @CsvBindByName(column = "de_e_ente_benef_indirizzo_beneficiario_e")
    private String beneficiaryAddress;

    @CsvBindByName(column = "de_e_ente_benef_civico_beneficiario_e")
    private String beneficiaryCivicNumber;

    @CsvBindByName(column = "cod_e_ente_benef_cap_beneficiario_e")
    private String beneficiaryPostalCode;

    @CsvBindByName(column = "de_e_ente_benef_localita_beneficiario_e")
    private String beneficiaryCity;

    @CsvBindByName(column = "de_e_ente_benef_provincia_beneficiario_e")
    private String beneficiaryProvince;

    @CsvBindByName(column = "cod_e_ente_benef_nazione_beneficiario_e")
    private String beneficiaryCountry;

    @CsvBindByName(column = "cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e")
    private PersonEntityType payerUniqueIdType;

    @CsvBindByName(column = "cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e")
    private PersonEntityType payerUniqueIdCode;

    @CsvBindByName(column = "cod_e_sogg_vers_anagrafica_versante_e")
    private String payerFullName;

    @CsvBindByName(column = "de_e_sogg_vers_indirizzo_versante_e")
    private String payerAddress;

    @CsvBindByName(column = "de_e_sogg_vers_civico_versante_e")
    private String payerCivicNumber;

    @CsvBindByName(column = "cod_e_sogg_vers_cap_versante_e")
    private String payerPostalCode;

    @CsvBindByName(column = "de_e_sogg_vers_localita_versante_e")
    private String payerLocation;

    @CsvBindByName(column = "de_e_sogg_vers_provincia_versante_e")
    private String payerProvince;

    @CsvBindByName(column = "cod_e_sogg_vers_nazione_versante_e")
    private String payerNation;

    @CsvBindByName(column = "de_e_sogg_vers_email_versante_e")
    private String payerEmail;

    @CsvBindByName(column = "cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e")
    private PersonEntityType debtorUniqueIdType;

    @CsvBindByName(column = "cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e")
    private PersonEntityType debtorUniqueIdCode;

    @CsvBindByName(column = "cod_e_sogg_pag_anagrafica_pagatore_e")
    private String debtorFullName;

    @CsvBindByName(column = "de_e_sogg_pag_indirizzo_pagatore_e")
    private String debtorAddress;

    @CsvBindByName(column = "de_e_sogg_pag_civico_pagatore_e")
    private String debtorCivicNumber;

    @CsvBindByName(column = "cod_e_sogg_pag_cap_pagatore_e")
    private String debtorPostalCode;

    @CsvBindByName(column = "de_e_sogg_pag_localita_pagatore_e")
    private String debtorLocation;

    @CsvBindByName(column = "de_e_sogg_pag_provincia_pagatore_e")
    private String debtorProvince;

    @CsvBindByName(column = "cod_e_sogg_pag_nazione_pagatore_e")
    private String debtorNation;

    @CsvBindByName(column = "de_e_sogg_pag_email_pagatore_e")
    private String debtorEmail;

    @CsvBindByName(column = "cod_e_dati_pag_codice_esito_pagamento_e")
    private String paymentOutcomeCode;

    @CsvBindByName(column = "num_e_dati_pag_importo_totale_pagato_e")
    private BigDecimal recPaymentAmount;

    @CsvBindByName(column = "cod_e_dati_pag_id_univoco_versamento_e")
    private String uniquePaymentId;

    @CsvBindByName(column = "cod_e_dati_pag_codice_contesto_pagamento_e")
    private String paymentContextCode;

    @CsvBindByName(column = "num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e")
    private BigDecimal recTransferAmount;

    @CsvBindByName(column = "de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e")
    private String singlePaymentOutcomeE;

    @CsvBindByName(column = "dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e")
    private OffsetDateTime singlePaymentOutcomeDateE;

    @CsvBindByName(column = "cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e")
    private String uniqueCollectionIdE;

    @CsvBindByName(column = "de_e_dati_pag_dati_sing_pag_causale_versamento_e")
    private String recTransferRemittanceInformation;

    @CsvBindByName(column = "de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e")
    private String recTransferCategory;

    @CsvBindByName(column = "cod_tipo_dovuto_e")
    private String dueTypeCode;

    @CsvBindByName(column = "dt_acquisizione_e")
    private OffsetDateTime recCreationDate;

    @CsvBindByName(column = "bilancioE")
    private String recInstallmentBalance;

    @CsvBindByName(column = "versione_oggetto_r")
    private String objectVersionR;

    @CsvBindByName(column = "cod_identificativo_flusso_r")
    private String payRepIuf;

    @CsvBindByName(column = "dt_data_ora_flusso_r")
    private OffsetDateTime payRepFlowDateTime;

    @CsvBindByName(column = "cod_identificativo_univoco_regolamento_r")
    private String uniqueRegulationCodeR;

    @CsvBindByName(column = "dt_data_regolamento_r")
    private LocalDate regulationDateR;

    @CsvBindByName(column = "cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r")
    private String senderInstitutionUniqueIdType;

    @CsvBindByName(column = "cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r")
    private String senderInstitutionUniqueId;

    @CsvBindByName(column = "de_ist_mitt_denominazione_mittente_r")
    private String senderInstitutionName;

    @CsvBindByName(column = "cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r")
    private String receiverInstitutionUniqueIdType;

    @CsvBindByName(column = "cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r")
    private String receiverInstitutionUniqueId;

    @CsvBindByName(column = "de_ist_ricev_denominazione_ricevente_r")
    private String receiverInstitutionName;

    @CsvBindByName(column = "num_numero_totale_pagamenti_r")
    private Long totalPaymentsNumberR;

    @CsvBindByName(column = "num_importo_totale_pagamenti_r")
    private BigDecimal totalPaymentsAmountR;

    @CsvBindByName(column = "cod_dati_sing_pagam_identificativo_univoco_versamento_r")
    private String payRepIuv;

    @CsvBindByName(column = "cod_dati_sing_pagam_identificativo_univoco_riscossione_r")
    private String payRepIur;

    @CsvBindByName(column = "num_dati_sing_pagam_singolo_importo_pagato_r")
    private BigDecimal singlePaymentAmountR;

    @CsvBindByName(column = "cod_dati_sing_pagam_codice_esito_singolo_pagamento_r")
    private String singlePaymentOutcomeCodeR;

    @CsvBindByName(column = "dt_dati_sing_pagam_data_esito_singolo_pagamento_r")
    private LocalDate singlePaymentOutcomeDateR;

    @CsvBindByName(column = "dt_acquisizione_r")
    private OffsetDateTime acquisitionDateR;

    @CsvBindByName(column = "cod_abi_t")
    private String tresAbiCode;

    @CsvBindByName(column = "cod_cab_t")
    private String tresCabCode;

    @CsvBindByName(column = "cod_conto_tesoreria")
    private String tresAccountRegistryCode;

    @CsvBindByName(column = "cod_divisa_t")
    private String currencyCode;

    @CsvBindByName(column = "dt_data_contabile_t")
    private LocalDate tresBillDate;

    @CsvBindByName(column = "dt_data_valuta_t")
    private LocalDate tresRegionValueDate;

    @CsvBindByName(column = "num_importo_tesoreria")
    private BigDecimal tresBillAmount;

    @CsvBindByName(column = "cod_segno_t")
    private String signCode;

    @CsvBindByName(column = "de_causale_t")
    private String tresRemittanceCode;

    @CsvBindByName(column = "cod_numero_assegno_t")
    private String checkNumber;

    @CsvBindByName(column = "cod_riferimento_banca_t")
    private String bankReferenceCode;

    @CsvBindByName(column = "cod_riferimento_cliente_t")
    private String clientReferenceCode;

    @CsvBindByName(column = "dt_data_ordine_t")
    private OffsetDateTime orderDate;

    @CsvBindByName(column = "de_descrizione_ordinante_t")
    private String tresLastName;

    @CsvBindByName(column = "cod_or1_t")
    private String tresOrCode;

    @CsvBindByName(column = "cod_id_univoco_flusso_t")
    private String tresIuf;

    @CsvBindByName(column = "cod_id_univoco_versamento_t")
    private String tresIuv;

    @CsvBindByName(column = "dt_acquisizione_t")
    private OffsetDateTime tresAcquisitionDateT;

    @CsvBindByName(column = "de_anno_bolletta_t")
    private String tresBillYear;

    @CsvBindByName(column = "cod_bolletta_t")
    private String tresBillCode;

    @CsvBindByName(column = "cod_id_dominio_t")
    private String domainUniqueId;

    @CsvBindByName(column = "dt_ricezione_t")
    private OffsetDateTime tresReceiptDate;

    @CsvBindByName(column = "de_anno_documento_t")
    private String tresDocumentYear;

    @CsvBindByName(column = "cod_documento_t")
    private String tresDocumentCode;

    @CsvBindByName(column = "de_anno_provvisorio_t")
    private String tresProvisionalAe;

    @CsvBindByName(column = "cod_provvisorio_t")
    private String tresProvisionalCode;

    @CsvBindByName(column = "dt_effettiva_sospeso_t")
    private LocalDate tresActualSuspensionDate;

    @CsvBindByName(column = "codice_gestionale_provvisorio_t")
    private String tresManagementProvisionalCode;

    @CsvBindByName(column = "classificazione_completezza")
    private String completenessClassification;

    @CsvBindByName(column = "dt_data_ultimo_aggiornamento")
    private LocalDate lastClassificationDate;


    @CsvBindByName(column = "cod_iud_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticeIud;

    @CsvBindByName(column = "cod_rp_silinviarp_id_univoco_versamento_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticeIuv;

    @CsvBindByName(column = "cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private PersonEntityType payerUniqueIdTypeI;

    @CsvBindByName(column = "cod_rp_sogg_pag_id_univ_pag_codice_id_univoco_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private PersonEntityType payerUniqueIdCodeI;

    @CsvBindByName(column = "de_rp_sogg_pag_anagrafica_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerFullNameI;

    @CsvBindByName(column = "de_rp_sogg_pag_indirizzo_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerAddressI;

    @CsvBindByName(column = "de_rp_sogg_pag_civico_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerCivicNumberI;

    @CsvBindByName(column = "cod_rp_sogg_pag_cap_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerPostalCodeI;

    @CsvBindByName(column = "de_rp_sogg_pag_localita_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerLocationI;

    @CsvBindByName(column = "de_rp_sogg_pag_provincia_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerProvinceI;

    @CsvBindByName(column = "cod_rp_sogg_pag_nazione_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerCountryI;

    @CsvBindByName(column = "de_rp_sogg_pag_email_pagatore_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payerEmailI;

    @CsvBindByName(column = "dt_rp_dati_vers_data_esecuzione_pagamento_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private LocalDate payNoticePaymentExecutionDate;

    @CsvBindByName(column = "cod_rp_dati_vers_tipo_versamento_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticePaymentType;

    @CsvBindByName(column = "num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private BigDecimal singlePaymentAmountI;

    @CsvBindByName(column = "num_rp_dati_vers_dati_sing_vers_commissione_carico_pa_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private BigDecimal payNoticePaCommission;

    @CsvBindByName(column = "de_rp_dati_vers_dati_sing_vers_causale_versamento_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticeRemittanceInformation;

    @CsvBindByName(column = "de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticeTransferCategory;

    @CsvBindByName(column = "cod_tipo_dovuto_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticeDebtPositionTypeOrgCode;

    @CsvBindByName(column = "bilancio_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String payNoticeBalance;

    @CsvBindByName(column = "dt_acquisizione_i")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_4_WITHOUT_NOTIFICATION})
    private String acquisitionDateI;


    @CsvBindByName(column = "cod_tipo_dovuto_pa1")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITH_NOTIFICATION})
    private String dueTypeCodePa1;

    @CsvBindByName(column = "de_tipo_dovuto_pa1")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITH_NOTIFICATION})
    private String dueTypeDescriptionPa1;

    @CsvBindByName(column = "cod_tassonomico_dovuto_pa1")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITH_NOTIFICATION})
    private String taxonomicCodePa1;

    @CsvBindByName(column = "cod_fiscale_pa1")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITH_NOTIFICATION})
    private String fiscalCodePa1;

    @CsvBindByName(column = "de_nome_pa1")
    @CsvIgnore(profiles = {ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITHOUT_NOTIFICATION, ExportFileVersions.CLASSIFICATIONS_VERSION_V1_3_WITH_NOTIFICATION})
    private String namePa1;

}
