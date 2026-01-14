package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.Utilities;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TreasuryXlsRowMapper extends XlsRowMapper<TreasuryXlsIngestionFlowFileDTO> {

	public TreasuryXlsRowMapper(List<String> headers) {
		super(headers);
	}

	@Override
	public TreasuryXlsIngestionFlowFileDTO map(List<String> cells, int rowIndex) {
		if(cells == null || cells.isEmpty()) {
			return null;
		}
		return TreasuryXlsIngestionFlowFileDTO.builder()
				.abiCode(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.ABI.getValue())))
				.cabCode(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.CAB.getValue())))
				.accountCode(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.CONTO.getValue())))
				.currency(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.DIVISA.getValue())))
				.billDate(map(cells, TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), this::parseLocalDate))
				.regionValueDate(mapOrElse(cells, TreasuryXlsHeadersEnum.DATA_VALUTA.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.DATA_VALUTA.getValue()), this::parseLocalDate, null))
				.billAmountCents(map(cells, TreasuryXlsHeadersEnum.IMPORTO.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.IMPORTO.getValue()), this::parseEuroToCents))
				.sign(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.SEGNO.getValue())))
				.remittanceCode(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.CAUSALE.getValue())))
				.checkNumber(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.NUM_ASSEGNO.getValue())))
				.bankReference(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.RIF_BANCA.getValue())))
				.clientReference(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.RIF_CLIENTE.getValue())))
				.remittanceDescription(mapString(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.DESCRIZIONE.getValue())))
				.extendedRemittanceDescription(map(cells, TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), String::trim))
				.build();
	}

	private LocalDate parseLocalDate(String key, String value) {
		try {
			return DateUtil.getLocalDateTime(Double.parseDouble(value.trim())).toLocalDate();
		} catch (Exception e) {
			throw new IllegalStateException("Impossibile convertire il valore \"%s\" in data per la cella \"%s\"".formatted(value, key), e);
		}
	}

	private Long parseEuroToCents(String key, String value) {
		try {
			return Utilities.bigDecimalEuroToLongCentsAmount(new BigDecimal(value));
		} catch (Exception e) {
			throw new IllegalStateException("Impossibile convertire il valore \"%s\" in centesimi per la cella \"%s\"".formatted(value, key), e);
		}
	}
}
