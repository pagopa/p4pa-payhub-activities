package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@Slf4j
public class TreasuryValidatorService {
    public static final String V_14 = "v14";
    public static final String V_161 = "v161";
    private static final String NOT_AVAILABLE = "Not available";

    private List<TreasuryErrorDTO> treasuryErrorDTOList;

    public TreasuryValidatorService() {
        treasuryErrorDTOList = new ArrayList<>();
    }

    public List<TreasuryErrorDTO> validateData(Object fGC, File file, String version) {
        maxLengthFields(fGC, file, version);
        mandatoryFields(fGC, file, version);
        return treasuryErrorDTOList;
    }

    private void maxLengthFields(Object fGC, File file, String version) {
        try {
            Method getInformazioniContoEvidenza = fGC.getClass().getMethod("getInformazioniContoEvidenza");
            Method getEsercizio = fGC.getClass().getMethod("getEsercizio");

            List<?> informazioniContoEvidenzaList = (List<?>) getInformazioniContoEvidenza.invoke(fGC);
            List<?> esercizioList = (List<?>) getEsercizio.invoke(fGC);

            for (Object informazioniContoEvidenza : informazioniContoEvidenzaList) {
                Method getMovimentoContoEvidenzas = informazioniContoEvidenza.getClass().getMethod("getMovimentoContoEvidenzas");
                List<?> movimentoContoEvidenzasList = (List<?>) getMovimentoContoEvidenzas.invoke(informazioniContoEvidenza);

                for (Object movimentoContoEvidenza : movimentoContoEvidenzasList) {
                    validateFieldLengths(movimentoContoEvidenza, esercizioList, file);
                }
            }
        } catch (Exception e) {
            log.error("Error during maxLengthFields validation", e);
        }
    }

    private void validateFieldLengths(Object movimentoContoEvidenza, List<?> esercizioList, File file) throws Exception {
        String iuf = TreasuryUtils.getIdentificativo(
                (String) movimentoContoEvidenza.getClass().getMethod("getCausale").invoke(movimentoContoEvidenza), TreasuryUtils.IUF);
        String iuv = TreasuryUtils.getIdentificativo(
                (String) movimentoContoEvidenza.getClass().getMethod("getCausale").invoke(movimentoContoEvidenza), TreasuryUtils.IUV);
        String codBolletta = NOT_AVAILABLE;
        String codEsercizio = esercizioList != null && !esercizioList.isEmpty() ? esercizioList.get(0).toString() : NOT_AVAILABLE;

        Method getNumeroBollettaQuietanza = movimentoContoEvidenza.getClass().getMethod("getNumeroBollettaQuietanza");
        if (getNumeroBollettaQuietanza.invoke(movimentoContoEvidenza) != null) {
            codBolletta = getNumeroBollettaQuietanza.invoke(movimentoContoEvidenza).toString();
        }

        if (StringUtils.isNotBlank(iuf) && iuf.length() > 34) {
            treasuryErrorDTOList.add(buildErrorDTO(file, codEsercizio, codBolletta, "PAA_IUF_TOO_LONG", "Codice univoco Flusso exceed max length of 35 chars"));
        }

        if (StringUtils.isNotBlank(iuv) && iuv.length() > 34) {
            treasuryErrorDTOList.add(buildErrorDTO(file, codEsercizio, codBolletta, "PAA_IUV_TOO_LONG", "Codice univoco Versamento exceed max length of 35 chars"));
        }
    }

    private void mandatoryFields(Object fGC, File file, String version) {
        try {
            Method getInformazioniContoEvidenza = fGC.getClass().getMethod("getInformazioniContoEvidenza");
            List<?> informazioniContoEvidenzaList = (List<?>) getInformazioniContoEvidenza.invoke(fGC);

            for (Object informazioniContoEvidenza : informazioniContoEvidenzaList) {
                Method getMovimentoContoEvidenzas = informazioniContoEvidenza.getClass().getMethod("getMovimentoContoEvidenzas");
                List<?> movimentoContoEvidenzasList = (List<?>) getMovimentoContoEvidenzas.invoke(informazioniContoEvidenza);

                for (Object movimentoContoEvidenza : movimentoContoEvidenzasList) {
                    validateMandatoryFields(movimentoContoEvidenza, file);
                }
            }
        } catch (Exception e) {
            log.error("Error during mandatoryFields validation", e);
        }
    }

    private void validateMandatoryFields(Object movimentoContoEvidenza, File file) throws Exception {
        String[] mandatoryFields = {
                "getTipoMovimento", "getTipoDocumento", "getTipoOperazione", "getImporto", "getDataMovimento", "getCausale", "getEndToEndId"
        };

        for (String methodName : mandatoryFields) {
            Method method = movimentoContoEvidenza.getClass().getMethod(methodName);
            Object value = method.invoke(movimentoContoEvidenza);
            if (value == null || (value instanceof String && StringUtils.isBlank((String) value))) {
                treasuryErrorDTOList.add(buildErrorDTO(file, NOT_AVAILABLE, NOT_AVAILABLE, "PAA_" + methodName.replace("get","").toUpperCase() + "_NOT_FOUND", methodName.replace("get","") + " field is not valorized but it is required"));
            }
        }

        Method getCausale = movimentoContoEvidenza.getClass().getMethod("getCausale");
        String causale = (String) getCausale.invoke(movimentoContoEvidenza);

        if (TreasuryUtils.getIdentificativo(causale, TreasuryUtils.IUF) == null) {
            treasuryErrorDTOList.add(buildErrorDTO(file, NOT_AVAILABLE, NOT_AVAILABLE, "PAA_IUF_NOT_FOUND", "Iuf field is not valorized but it is required"));
        }

        if (TreasuryUtils.getIdentificativo(causale, TreasuryUtils.IUV) == null) {
            treasuryErrorDTOList.add(buildErrorDTO(file, NOT_AVAILABLE, NOT_AVAILABLE, "PAA_IUV_NOT_FOUND", "Iuv field is not valorized but it is required"));
        }

        Method getSospesoDaRegolarizzare = movimentoContoEvidenza.getClass().getMethod("getSospesoDaRegolarizzare");
        Object sospesoDaRegolarizzare = getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza);

        if (sospesoDaRegolarizzare != null) {
            Method getDataEffettivaSospeso = sospesoDaRegolarizzare.getClass().getMethod("getDataEffettivaSospeso");
            if (getDataEffettivaSospeso.invoke(sospesoDaRegolarizzare) == null) {
                treasuryErrorDTOList.add(buildErrorDTO(file, NOT_AVAILABLE, NOT_AVAILABLE, "PAA_DATA_EFFETTIVA_SOSPESO_NOT_FOUND", "Data effettiva sospeso field is not valorized but it is required"));
            }

            Method getCodiceGestionaleProvvisorio = sospesoDaRegolarizzare.getClass().getMethod("getCodiceGestionaleProvvisorio");
            if (getCodiceGestionaleProvvisorio.invoke(sospesoDaRegolarizzare) == null) {
                treasuryErrorDTOList.add(buildErrorDTO(file, NOT_AVAILABLE, NOT_AVAILABLE, "PAA_CODICE_GESTIONALE_PROVVISORIO_NOT_FOUND", "Codice gestionale provvisorio field is not valorized but it is required"));
            }
        }
    }

    private TreasuryErrorDTO buildErrorDTO(File file, String codEsercizio, String codBolletta, String errorCode, String errorMessage) {
        return TreasuryErrorDTO.builder()
                .nomeFile(file.getName())
                .deAnnoBolletta(codEsercizio)
                .codBolletta(codBolletta)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public boolean validatePageSize(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa fGC14, it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa fGC161, int sizeZipFile, String version) {
        boolean valid = false;
        if (version.equals(V_14) && fGC14 != null) {
            int pageTotalNumber = fGC14.getPagineTotali().get(0);
            if (pageTotalNumber == sizeZipFile)
                valid = true;
        } else if (version.equals(V_161) && fGC161 != null){
            int pageTotalNumber = fGC161.getPagineTotali().get(0);
            if (pageTotalNumber == sizeZipFile)
                valid = true;
        }
        return valid;
    }

}
