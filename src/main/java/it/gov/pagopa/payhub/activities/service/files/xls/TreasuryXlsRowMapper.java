package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDate;
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
				.abiCode(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.ABI.getValue()), String::trim, null))
				.cabCode(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.CAB.getValue()), String::trim, null))
				.accountCode(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.CONTO.getValue()), String::trim, null))
				.currency(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.DIVISA.getValue()), String::trim, null))
				.billDate(map(cells, TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), this::parseLocalDate))
				.regionValueDate(mapOrElse(cells, TreasuryXlsHeadersEnum.DATA_VALUTA.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.DATA_VALUTA.getValue()), this::parseLocalDate, null))
				.billAmountCents(map(cells, TreasuryXlsHeadersEnum.IMPORTO.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.IMPORTO.getValue()), this::parseLong))
				.sign(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.SEGNO.getValue()), String::trim, null))
				.remittanceCode(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.CAUSALE.getValue()), String::trim, null))
				.checkNumber(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.NUM_ASSEGNO.getValue()), String::trim, null))
				.bankReference(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.RIF_BANCA.getValue()), String::trim, null))
				.clientReference(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.RIF_CLIENTE.getValue()), String::trim, null))
				.remittanceDescription(mapOrElse(cells, this.getHeaderIndex(TreasuryXlsHeadersEnum.DESCRIZIONE.getValue()), String::trim, null))
				.extendedRemittanceDescription(map(cells, TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue(), this.getHeaderIndex(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), String::trim))
				.build();
	}

	private LocalDate parseLocalDate(String key, String value) {
		try {
			return DateUtil.getLocalDateTime(Double.parseDouble(value.trim())).toLocalDate();
		} catch (Exception e) {
			String errorMessage = key!=null && !key.isBlank() ?
					"Error in parsing LocalDate from value \"%s\" for Xls cell \"%s\"".formatted(value, key) :
					"Error in parsing LocalDate from value \"%s\"".formatted(value);
			throw new IllegalStateException(errorMessage, e);
		}
	}

	private Long parseLong(String key, String value) {
		try {
			return (long) (Double.parseDouble(value.trim())*100);
		} catch (Exception e) {
			String errorMessage = key!=null && !key.isBlank() ?
					"Error in parsing Long from value \"%s\" for Xls cell \"%s\"".formatted(value, key) :
					"Error in parsing Long from value \"%s\"".formatted(value);
			throw new IllegalStateException(errorMessage, e);
		}
	}
}
