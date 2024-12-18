package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.service.cipher.DataCipherService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;

@Lazy
@Service
public class TreasuryOpi14MapperService implements BiFunction<FlussoGiornaleDiCassa, IngestionFlowFileDTO,  Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>>> {

  private static DataCipherService dataCipherService;
  public static final String insert = "INSERT";
  public static final String delete = "DELETE";

  public TreasuryOpi14MapperService(DataCipherService dataCipherService) {
    this.dataCipherService = dataCipherService;
  }


  /**
   * Maps the given `CtFlussoRiversamento` and `IngestionFlowFileDTO` into a `PaymentsReportingDTO` object.
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
                .billYear(flussoGiornaleDiCassa.getEsercizio().get(0).toString())
                .billCode(movContoEvidenza.getNumeroBollettaQuietanza().toString())
                .billIpNumber(movContoEvidenza.getImporto())
                .billDate(movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime())
                .receptionDate(new Date())
                .documentCode("" + movContoEvidenza.getNumeroDocumento())
                .regionValueDate(dataValutaRegione)
                .organizationId(organizationDTO.getOrgId())
                .flowIdentifierCode(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUF))
                .iuv(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUV))
                .creationDate(new Date())
                .lastUpdateDate(new Date())
                .ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
                .actualSuspensionDate(movContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso().toGregorianCalendar().getTime())
                .managementProvisionalCode(movContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio())
                .endToEndId(movContoEvidenza.getEndToEndId())
                .lastNameHash(dataCipherService.encryptObj(cliente.getAnagraficaCliente()))
                .build();


        if (movContoEvidenza.getTipoMovimento().equals("ENTRATA") && movContoEvidenza.getTipoDocumento().equals("SOSPESO ENTRATA"))
          if (movContoEvidenza.getTipoOperazione().equals("ESEGUITO")) {

            FlussoTesoreriaPIIDTO flussoTesoreriaPIIDTO = FlussoTesoreriaPIIDTO.builder()
                    .deCausale(movContoEvidenza.getCausale())
                    .deCognome(cliente.getAnagraficaCliente())
                    .build();

            insertList.add(Pair.of(treasuryDTO, flussoTesoreriaPIIDTO));
          } else if (movContoEvidenza.getTipoOperazione().equals("STORNATO"))
            deleteList.add(Pair.of(treasuryDTO, null));


      });
    });
    resultMap.put(insert, insertList);
    resultMap.put(delete, deleteList);
    return resultMap;
  }


}
