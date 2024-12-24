package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TreasuryOpi161ValidatorService implements TreasuryValidatorService<FlussoGiornaleDiCassa> {

    private static final String NOT_AVAILABLE = "Not available";

    List<TreasuryErrorDTO> treasuryErrorDTOList;

    public TreasuryOpi161ValidatorService() {
        treasuryErrorDTOList = new ArrayList<>();
    }

    @Override
    public List<TreasuryErrorDTO> validateData(FlussoGiornaleDiCassa fGC, String fileName) {
        maxLengthFields(fGC, fileName);
        mandatoryFields(fGC, fileName);

        return treasuryErrorDTOList;
    }

    private void maxLengthFields(FlussoGiornaleDiCassa fGC161, String fileName) {

        fGC161.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza -> {
            informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
                String iuf = TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF);
                String codBolletta = NOT_AVAILABLE;
                String codEsercizio = NOT_AVAILABLE;
                codBolletta = movimentoContoEvidenza.getNumeroBollettaQuietanza() != null ? movimentoContoEvidenza.getNumeroBollettaQuietanza().toString() : codBolletta;
                codEsercizio = !fGC161.getEsercizio().isEmpty() ? fGC161.getEsercizio().get(0).toString() : codEsercizio;
                if (StringUtils.isNotBlank(iuf) && iuf.length() > 34) {
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(fileName)
                            .deAnnoBolletta(codEsercizio)
                            .codBolletta(codBolletta)
                            .errorCode("PAA_IUF_TOO_LONG")
                            .errorMessage("Codice univoco Flusso exceed max length of 35 chars")
                            .build());
                }
            });
        });

    }

    private void mandatoryFields(FlussoGiornaleDiCassa fGC161, String fileName) {

        fGC161.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza ->
                informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
                    String codBolletta = NOT_AVAILABLE;
                    String codEsercizio = NOT_AVAILABLE;
                    codBolletta = movimentoContoEvidenza.getNumeroBollettaQuietanza() != null ? movimentoContoEvidenza.getNumeroBollettaQuietanza().toString() : codBolletta;
                    codEsercizio = !fGC161.getEsercizio().isEmpty() ? fGC161.getEsercizio().get(0).toString() : codEsercizio;
                    if (fGC161.getEsercizio() == null || fGC161.getEsercizio().isEmpty())
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(null)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_ESERCIZIO_NOT_FOUND")
                                .errorMessage("Esercizio field is not valorized but it is required")
                                .build());
                    if (fGC161.getPagineTotali() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_PAGINE_TOTALI_NOT_FOUND")
                                .errorMessage("Pagine totali field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getTipoMovimento() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_TIPO_MOVIMENTO_NOT_FOUND")
                                .errorMessage("Tipo movimento field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getTipoDocumento() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_TIPO_DOCUMENTO_NOT_FOUND")
                                .errorMessage("Tipo documento field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getTipoOperazione() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_TIPO_OPERAZIONE_NOT_FOUND")
                                .errorMessage("Tipo operazione field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getImporto() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_IMPORTO_NOT_FOUND")
                                .errorMessage("Importo field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getDataMovimento() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_DATA_MOVIMENTO_NOT_FOUND")
                                .errorMessage("Data movimento field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getCliente() == null || movimentoContoEvidenza.getCliente().getAnagraficaCliente() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_ANAGRAFICA_CLIENTE_NOT_FOUND")
                                .errorMessage("Anagrafica cliente field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getCausale() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_CAUSALE_NOT_FOUND")
                                .errorMessage("Causale field is not valorized but it is required")
                                .build());
                    if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF) == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_IUF_NOT_FOUND")
                                .errorMessage("Iuf field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getSospesoDaRegolarizzare() == null || movimentoContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_DATA_EFFETTIVA_SOSPESO_NOT_FOUND")
                                .errorMessage("Data effettiva sospeso field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getSospesoDaRegolarizzare() == null || movimentoContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_CODICE_GESTIONALE_PROVVISORIO_NOT_FOUND")
                                .errorMessage("Codice gestionale provvisorio field is not valorized but it is required")
                                .build());
                    if (movimentoContoEvidenza.getEndToEndId() == null)
                        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                                .nomeFile(fileName)
                                .deAnnoBolletta(codEsercizio)
                                .codBolletta(codBolletta)
                                .errorCode("PAA_END_TO_END_ID_NOT_FOUND")
                                .errorMessage("End to end id field is not valorized but it is required")
                                .build());
                }));


    }

    @Override
    public boolean validatePageSize(FlussoGiornaleDiCassa fGC161, int sizeZipFile) {
        boolean isValid = false;
        if (fGC161 == null || fGC161.getPagineTotali() == null || fGC161.getPagineTotali().isEmpty())
            return isValid;
        int pageTotalNumber = fGC161.getPagineTotali().get(0);
        if (pageTotalNumber == sizeZipFile)
            isValid = true;
        return isValid;
    }


}
