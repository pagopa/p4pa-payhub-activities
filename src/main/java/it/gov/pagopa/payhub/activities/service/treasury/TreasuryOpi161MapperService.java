package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.service.cipher.DataCipherService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.InformazioniContoEvidenza;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;

@Lazy
@Service
public class TreasuryOpi161MapperService implements BiFunction<FlussoGiornaleDiCassa, IngestionFlowFileDTO,  Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>>> {

  private static DataCipherService dataCipherService;
  public static final String INSERT = "INSERT";
  public static final String DELETE = "DELETE";

  public TreasuryOpi161MapperService(DataCipherService dataCipherService) {
    TreasuryOpi161MapperService.dataCipherService = dataCipherService;
  }


  /**
   * Maps the given `flussoGiornaleDiCassa` and `IngestionFlowFileDTO` into a `PaymentsReportingDTO` object.
   *
   * @param flussoGiornaleDiCassa the flow data object containing information about the transaction flow.
   * @param ingestionFlowFileDTO  the ingestion metadata containing information about the processing flow.
   * @return a fully populated `PaymentsReportingDTO` object containing mapped data.
   */
  @Override
  public Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> apply(FlussoGiornaleDiCassa flussoGiornaleDiCassa, IngestionFlowFileDTO ingestionFlowFileDTO) {
    Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> resultMap = new HashMap<>();
    List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> insertList = new LinkedList<>();
    List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> deleteList = new LinkedList<>();

    flussoGiornaleDiCassa.getInformazioniContoEvidenza().forEach(infoContoEvidenza -> {
      infoContoEvidenza.getMovimentoContoEvidenzas().forEach(movContoEvidenza -> {

        InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = movContoEvidenza.getCliente();
        OrganizationDTO organizationDTO = ingestionFlowFileDTO.getOrg();

        Date dataValutaRegione = movContoEvidenza.getDataValutaEnte().toGregorianCalendar().getTime();

        if (dataValutaRegione == null)
          dataValutaRegione = movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime();

        TreasuryDTO treasuryDTO = TreasuryDTO.builder()
                .deAnnoBolletta(flussoGiornaleDiCassa.getEsercizio().get(0).toString())
                .codBolletta(movContoEvidenza.getNumeroBollettaQuietanza().toString())
                .numIpBolletta(movContoEvidenza.getImporto())
                .dtBolletta(movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime())
                .dtRicezione(new Date())
                .codDocumento("" + movContoEvidenza.getNumeroDocumento())
                .dtDataValutaRegione(dataValutaRegione)
                .mygovEnteId(organizationDTO.getOrgId())
                .codIdUnivocoFlusso(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUF))
                .codIdUnivocoVersamento(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUV))
                .dtCreazione(new Date())
                .dtUltimaModifica(new Date())
                .mygovManageFlussoId(ingestionFlowFileDTO.getIngestionFlowFileId())
                .dtEffettivaSospeso(movContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso().toGregorianCalendar().getTime())
                .codiceGestionaleProvvisorio(movContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio())
                .endToEndId(movContoEvidenza.getEndToEndId())
                .deCognomeHash(dataCipherService.encryptObj(cliente.getAnagraficaCliente()))
                .build();

        if (movContoEvidenza.getTipoMovimento().equals("ENTRATA") && movContoEvidenza.getTipoDocumento().equals("SOSPESO ENTRATA"))
          if (movContoEvidenza.getTipoOperazione().equals("ESEGUITO")) {

            FlussoTesoreriaPIIDTO flussoTesoreriaPIIDTO = FlussoTesoreriaPIIDTO.builder()
                    .deCausale(movContoEvidenza.getCausale())
                    .deCognome(cliente.getAnagraficaCliente())
                    .build();

            insertList.add(Pair.of(treasuryDTO, flussoTesoreriaPIIDTO));
          } else if (movContoEvidenza.getTipoOperazione().equals("STORNATO")) {
            deleteList.add(Pair.of(treasuryDTO, null));
          }

      });
    });
    resultMap.put(INSERT, insertList);
    resultMap.put(DELETE, deleteList);
    return resultMap;
  }
}
