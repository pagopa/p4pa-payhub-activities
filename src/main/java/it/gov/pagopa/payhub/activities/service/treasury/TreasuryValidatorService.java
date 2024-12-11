package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Lazy
@Service
@Slf4j
public class TreasuryValidatorService {
  public static final String v14 = "v14";
  public static final String v161 = "v161";
  List<TreasuryErrorDTO> treasuryErrorDTOList;

  public List<TreasuryErrorDTO> validateData(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa fGC14, it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa fGC161, File file, String versione) {
    maxLengthFields(fGC14, fGC161, file, versione);
    mandatoryFields(fGC14, fGC161, file, versione);

    return treasuryErrorDTOList;
  }


  private void maxLengthFields(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa fGC14, it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa fGC161, File file, String versione) {
    if (versione.equals(v14)) {
      fGC14.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza -> {
        informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
          if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF).length() > 34) {
            treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                    .nomeFile(file.getName())
                    .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                    .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                    .errorCode("PAA_IUF_TOO_LONG")
                    .errorMessage("Codice univoco Flusso exceed max length of 35 chars")
                    .build());
          }
          if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUV).length() > 34) {
            treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                    .nomeFile(file.getName())
                    .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                    .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                    .errorCode("PAA_IUV_TOO_LONG")
                    .errorMessage("Codice univoco Versamento exceed max length of 35 chars")
                    .build());
          }
        });
      });
    } else {
      fGC161.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza -> {
        informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
          if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF).length() > 34) {
            treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                    .nomeFile(file.getName())
                    .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                    .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                    .errorCode("PAA_IUF_TOO_LONG")
                    .errorMessage("Codice univoco Flusso exceed max length of 35 chars")
                    .build());
          }
          if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUV).length() > 34) {
            treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                    .nomeFile(file.getName())
                    .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                    .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                    .errorCode("PAA_IUV_TOO_LONG")
                    .errorMessage("Codice univoco Versamento exceed max length of 35 chars")
                    .build());
          }
        });
      });
    }

  }

  private void mandatoryFields(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa fGC14, it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa fGC161, File file, String versione) {
    switch (versione) {
      case v14:
        fGC14.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza ->
                informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
                  if (fGC14.getEsercizio().get(0).toString().isEmpty())
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_ESERCIZIO_NOT_FOUND")
                            .errorMessage("Esercizio field is not valorized but it is required")
                            .build());
                  if (fGC14.getPagineTotali() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_PAGINE_TOTALI_NOT_FOUND")
                            .errorMessage("Pagine totali field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getTipoMovimento() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_TIPO_MOVIMENTO_NOT_FOUND")
                            .errorMessage("Tipo movimento field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getTipoDocumento() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_TIPO_DOCUMENTO_NOT_FOUND")
                            .errorMessage("Tipo documento field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getTipoOperazione() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_TIPO_OPERAZIONE_NOT_FOUND")
                            .errorMessage("Tipo operazione field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getImporto() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_IMPORTO_NOT_FOUND")
                            .errorMessage("Importo field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getDataMovimento() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_DATA_MOVIMENTO_NOT_FOUND")
                            .errorMessage("Data movimento field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getCliente().getAnagraficaCliente() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_ANAGRAFICA_CLIENTE_NOT_FOUND")
                            .errorMessage("Anagrafica cliente field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getCausale() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_CAUSALE_NOT_FOUND")
                            .errorMessage("Causale field is not valorized but it is required")
                            .build());
                  if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF) == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_IUF_NOT_FOUND")
                            .errorMessage("Iuf field is not valorized but it is required")
                            .build());
                  if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUV) == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_IUV_NOT_FOUND")
                            .errorMessage("Iuv field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_DATA_EFFETTIVA_SOSPESO_NOT_FOUND")
                            .errorMessage("Data effettiva sospeso field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_CODICE_GESTIONALE_PROVVISORIO_NOT_FOUND")
                            .errorMessage("Codice gestionale provvisorio field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getEndToEndId() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC14.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_END_TO_END_ID_NOT_FOUND")
                            .errorMessage("End to end id field is not valorized but it is required")
                            .build());
                }));
        break;
      case v161:
        fGC161.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza ->
                informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
                  if (fGC161.getEsercizio().get(0).toString().isEmpty())
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_ESERCIZIO_NOT_FOUND")
                            .errorMessage("Esercizio field is not valorized but it is required")
                            .build());
                  if (fGC161.getPagineTotali() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_PAGINE_TOTALI_NOT_FOUND")
                            .errorMessage("Pagine totali field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getTipoMovimento() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_TIPO_MOVIMENTO_NOT_FOUND")
                            .errorMessage("Tipo movimento field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getTipoDocumento() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_TIPO_DOCUMENTO_NOT_FOUND")
                            .errorMessage("Tipo documento field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getTipoOperazione() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_TIPO_OPERAZIONE_NOT_FOUND")
                            .errorMessage("Tipo operazione field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getImporto() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_IMPORTO_NOT_FOUND")
                            .errorMessage("Importo field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getDataMovimento() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_DATA_MOVIMENTO_NOT_FOUND")
                            .errorMessage("Data movimento field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getCliente().getAnagraficaCliente() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_ANAGRAFICA_CLIENTE_NOT_FOUND")
                            .errorMessage("Anagrafica cliente field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getCausale() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_CAUSALE_NOT_FOUND")
                            .errorMessage("Causale field is not valorized but it is required")
                            .build());
                  if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUF) == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_IUF_NOT_FOUND")
                            .errorMessage("Iuf field is not valorized but it is required")
                            .build());
                  if (TreasuryUtils.getIdentificativo(movimentoContoEvidenza.getCausale(), TreasuryUtils.IUV) == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_IUV_NOT_FOUND")
                            .errorMessage("Iuv field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_DATA_EFFETTIVA_SOSPESO_NOT_FOUND")
                            .errorMessage("Data effettiva sospeso field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_CODICE_GESTIONALE_PROVVISORIO_NOT_FOUND")
                            .errorMessage("Codice gestionale provvisorio field is not valorized but it is required")
                            .build());
                  if (movimentoContoEvidenza.getEndToEndId() == null)
                    treasuryErrorDTOList.add(TreasuryErrorDTO.builder()
                            .nomeFile(file.getName())
                            .deAnnoBolletta(fGC161.getEsercizio().get(0).toString())
                            .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .errorCode("PAA_END_TO_END_ID_NOT_FOUND")
                            .errorMessage("End to end id field is not valorized but it is required")
                            .build());
                }));

        break;
    }

  }


  public boolean validatePageSize(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa fGC14, it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa fGC161, int sizeZipFile, String versione) {
    boolean valid = true;
    if (versione.equals(v14)) {
      int pageTotalNumber = fGC14.getPagineTotali().get(0);
//      fGC14.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza -> {
//        informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
//          log.error("page total number from xml {} - size zip file {}", pageTotalNumber, sizeZipFile);
          if (pageTotalNumber != sizeZipFile)
            valid=false;

//        });
//      });
    } else {
      int pageTotalNumber = fGC161.getPagineTotali().get(0);
//      fGC161.getInformazioniContoEvidenza().forEach(informazioniContoEvidenza -> {
//        informazioniContoEvidenza.getMovimentoContoEvidenzas().forEach(movimentoContoEvidenza -> {
          log.error("page total number from xml {} - size zip file {}", pageTotalNumber, sizeZipFile);
          if (pageTotalNumber != sizeZipFile)
            valid = false;
//        });
//      });
    }
    return valid;
  }
}
