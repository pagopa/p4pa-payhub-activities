package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.List;

public class TreasuryXlsRowMapper extends XlsRowMapper<TreasuryXlsIngestionFlowFileDTO> {

	public TreasuryXlsRowMapper(List<String> headers) {
		super(headers);
	}

	@Override
	public TreasuryXlsIngestionFlowFileDTO map(List<String> cells) {
		if(cells == null || cells.isEmpty()) {
			return null;
		}
		return TreasuryXlsIngestionFlowFileDTO.builder()
				.abiCode(mapOrElse(cells, this.getHeaderIndex("ABI"), String::trim, null))
				.cabCode(mapOrElse(cells, this.getHeaderIndex("CAB"), String::trim, null))
				.accountCode(mapOrElse(cells, this.getHeaderIndex("CONTO"), String::trim, null))
				.currency(mapOrElse(cells, this.getHeaderIndex("DIVISA"), String::trim, null))
				.billDate(mapOrElse(cells, this.getHeaderIndex("DATA CONTABILE"), s -> DateUtil.getLocalDateTime(Double.parseDouble(s.trim())).toLocalDate(), null))
				.regionValueDate(mapOrElse(cells, this.getHeaderIndex("DATA VALUTA"), s -> DateUtil.getLocalDateTime(Double.parseDouble(s.trim())).toLocalDate(), null))
				.billAmountCents(mapOrElse(cells, this.getHeaderIndex("IMPORTO"), s -> (long) (Double.parseDouble(s.trim())*100), null))
				.sign(mapOrElse(cells, this.getHeaderIndex("SEGNO"), String::trim, null))
				.remittanceCode(mapOrElse(cells, this.getHeaderIndex("CAUSALE"), String::trim, null))
				.checkNumber(mapOrElse(cells, this.getHeaderIndex("NUM. ASSEGNO"), String::trim, null))
				.bankReference(mapOrElse(cells, this.getHeaderIndex("RIF. BANCA"), String::trim, null))
				.clientReference(mapOrElse(cells, this.getHeaderIndex("RIF. CLIENTE"), String::trim, null))
				.remittanceDescription(mapOrElse(cells, this.getHeaderIndex("DESCRIZIONE"), String::trim, null))
				.extendedRemittanceDescription(mapOrElse(cells, this.getHeaderIndex("DESCRIZIONE ESTESA"), String::trim, null))
				.build();
	}
}
