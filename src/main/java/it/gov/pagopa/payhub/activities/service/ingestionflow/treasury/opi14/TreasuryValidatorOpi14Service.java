package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi14;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryValidatorService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TreasuryValidatorOpi14Service implements TreasuryValidatorService<FlussoGiornaleDiCassa> {

    private static final String NOT_AVAILABLE = "Not available";


    @Override
    public List<TreasuryErrorDTO> validateData(FlussoGiornaleDiCassa fGC14, String fileName) {

        List<TreasuryErrorDTO> treasuryErrorDTOList = new ArrayList<>();

        fGC14.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza ->
                informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
                    String codBolletta = Optional.ofNullable(movimentoContoEvidenza.getNumeroBollettaQuietanza())
                            .map(BigInteger::toString)
                            .orElse(NOT_AVAILABLE);
                    String codEsercizio = NOT_AVAILABLE;
                    String iuf = TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF);
                    codEsercizio = !fGC14.getEsercizio().isEmpty() ? fGC14.getEsercizio().getFirst().toString() : codEsercizio;
                    if (fGC14.getEsercizio() == null || fGC14.getEsercizio().isEmpty())
                        addError(treasuryErrorDTOList, fileName, null, codBolletta, "PAA_ESERCIZIO_NOT_FOUND", "Esercizio field is not valorized but it is required");
                    if (movimentoContoEvidenza.getTipoMovimento() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_TIPO_MOVIMENTO_NOT_FOUND", "Tipo movimento field is not valorized but it is required");
                    if (movimentoContoEvidenza.getTipoDocumento() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_TIPO_DOCUMENTO_NOT_FOUND", "Tipo documento field is not valorized but it is required");
                    if (movimentoContoEvidenza.getTipoOperazione() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_TIPO_OPERAZIONE_NOT_FOUND", "Tipo operazione field is not valorized but it is required");
                    if (movimentoContoEvidenza.getImporto() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_IMPORTO_NOT_FOUND", "Importo field is not valorized but it is required");
                    if (movimentoContoEvidenza.getDataMovimento() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_DATA_MOVIMENTO_NOT_FOUND", "Data movimento field is not valorized but it is required");
                    if (movimentoContoEvidenza.getCliente() == null || movimentoContoEvidenza.getCliente().getAnagraficaCliente() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_ANAGRAFICA_CLIENTE_NOT_FOUND", "Anagrafica cliente field is not valorized but it is required");
                    if (movimentoContoEvidenza.getCausale() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_CAUSALE_NOT_FOUND", "Causale field is not valorized but it is required");
                    if (iuf == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_IUF_NOT_FOUND", "Iuf field is not valorized but it is required");
                    if (StringUtils.isNotBlank(iuf) && iuf.length() > 35)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_IUF_TOO_LONG", "Codice univoco Flusso exceed max length of 35 chars");
                    if (movimentoContoEvidenza.getSospesoDaRegolarizzare() == null || movimentoContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_DATA_EFFETTIVA_SOSPESO_NOT_FOUND", "Data effettiva sospeso field is not valorized but it is required");
                    if (movimentoContoEvidenza.getSospesoDaRegolarizzare() == null || movimentoContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_CODICE_GESTIONALE_PROVVISORIO_NOT_FOUND", "Codice gestionale provvisorio field is not valorized but it is required");
                    if (movimentoContoEvidenza.getEndToEndId() == null)
                        addError(treasuryErrorDTOList, fileName, codEsercizio, codBolletta, "PAA_END_TO_END_ID_NOT_FOUND", "End to end id field is not valorized but it is required");
                }));

        return treasuryErrorDTOList;
    }

    private void addError(List<TreasuryErrorDTO> treasuryErrorDTOList, String fileName, String codEsercizio, String codBolletta, String errorCode, String errorMessage) {
        treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                .fileName(fileName)
                .billYear(codEsercizio)
                .billCode(codBolletta)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build());
    }


    @Override
    public boolean validatePageSize(FlussoGiornaleDiCassa fGC, int sizeZipFile) {
        boolean isValid = false;
        if (fGC == null || fGC.getPagineTotali() == null || fGC.getPagineTotali().isEmpty())
            return isValid;
        int pageTotalNumber = fGC.getPagineTotali().getFirst();
        if (pageTotalNumber == sizeZipFile)
            isValid = true;
        return isValid;
    }

}
