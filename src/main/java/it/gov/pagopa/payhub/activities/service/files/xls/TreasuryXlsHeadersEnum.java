package it.gov.pagopa.payhub.activities.service.files.xls;

public enum TreasuryXlsHeadersEnum {
	ABI("ABI"),
	CAB("CAB"),
	CONTO("CONTO"),
	DIVISA("DIVISA"),
	DATA_CONTABILE("DATA CONTABILE"),
	DATA_VALUTA("DATA VALUTA"),
	IMPORTO("IMPORTO"),
	SEGNO("SEGNO"),
	CAUSALE("CAUSALE"),
	NUM_ASSEGNO("NUM. ASSEGNO"),
	RIF_BANCA("RIF. BANCA"),
	RIF_CLIENTE("RIF. CLIENTE"),
	DESCRIZIONE("DESCRIZIONE"),
	DESCRIZIONE_ESTESA("DESCRIZIONE ESTESA");

	private final String value;

	TreasuryXlsHeadersEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
