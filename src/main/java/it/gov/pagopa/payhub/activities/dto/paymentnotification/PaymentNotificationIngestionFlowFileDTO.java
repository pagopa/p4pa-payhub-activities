package it.gov.pagopa.payhub.activities.dto.paymentnotification;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentNotificationIngestionFlowFileDTO {

    @CsvBindByName(column = "cod_iud", required = true, profiles = "legacy")
    @CsvBindByName(column = "iud", required = true)
    private String iud;

    @CsvBindByName(column = "cod_rp_silinviarp_id_univoco_versamento", required = true, profiles = "legacy")
    @CsvBindByName(column = "iuv", required = true)
    private String iuv;

    @CsvBindByName(column = "cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco ", required = true, profiles = "legacy")
    @CsvBindByName(column = "debtorUniqueIdentifierType", required = true)
    private String debtorUniqueIdentifierType;

    @CsvBindByName(column = "cod_rp_sogg_pag_id_univ_pag_codice_id_univoco ", required = true, profiles = "legacy")
    @CsvBindByName(column = "debtorUniqueIdentifierCode", required = true)
    private String debtorUniqueIdentifierCode;

    @CsvBindByName(column = "de_rp_sogg_pag_anagrafica_pagatore ", required = true, profiles = "legacy")
    @CsvBindByName(column = "debtorFullName", required = true)
    private String debtorFullName;

    @CsvBindByName(column = "de_rp_sogg_pag_indirizzo_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorAddress")
    private String debtorAddress;

    @CsvBindByName(column = "de_rp_sogg_pag_civico_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorCivic")
    private String debtorCivic;

    @CsvBindByName(column = "cod_rp_sogg_pag_cap_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorPostalCode")
    private String debtorPostalCode;

    @CsvBindByName(column = "de_rp_sogg_pag_localita_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorLocation")
    private String debtorLocation;

    @CsvBindByName(column = "de_rp_sogg_pag_provincia_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorProvince")
    private String debtorProvince;

    @CsvBindByName(column = "cod_rp_sogg_pag_nazione_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorNation")
    private String debtorNation;

    @CsvBindByName(column = "de_rp_sogg_pag_email_pagatore ", profiles = "legacy")
    @CsvBindByName(column = "debtorEmail")
    private String debtorEmail;

    @CsvBindByName(column = "dt_rp_dati_vers_data_esecuzione_pagamento ", required = true, profiles = "legacy")
    @CsvBindByName(column = "paymentExecutionDate", required = true)
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate paymentExecutionDate;

    @CsvBindByName(column = "num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento ", required = true, profiles = "legacy")
    @CsvBindByName(column = "amountPaidCents", required = true)
    private BigDecimal amountPaidCents;

    @CsvBindByName(column = "num_rp_dati_vers_dati_sing_vers_commissione_carico_pa ", required = true, profiles = "legacy")
    @CsvBindByName(column = "paCommissionCents", required = true)
    private BigDecimal paCommissionCents;

    @CsvBindByName(column = "cod_tipo_dovuto ", required = true, profiles = "legacy")
    @CsvBindByName(column = "debtPositionTypeOrgCode", required = true)
    private String debtPositionTypeOrgCode;

    @CsvBindByName(column = "cod_rp_dati_vers_tipo_versamento ", required = true, profiles = "legacy")
    @CsvBindByName(column = "paymentType", required = true)
    private String paymentType;

    @CsvBindByName(column = "de_rp_dati_vers_dati_sing_vers_causale_versamento ", required = true, profiles = "legacy")
    @CsvBindByName(column = "remittanceInformation", required = true)
    private String remittanceInformation;

    @CsvBindByName(column = "de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione ", required = true, profiles = "legacy")
    @CsvBindByName(column = "transferCategory", required = true)
    private String transferCategory;

    @CsvBindByName(column = "bilancio", profiles = "legacy")
    @CsvBindByName(column = "balance")
    private String balance;
}