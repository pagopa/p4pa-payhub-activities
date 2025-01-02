package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;

import java.util.*;

public class TreasuryOpi14MapperService implements TreasuryMapperService<FlussoGiornaleDiCassa, Map<TreasuryOperationEnum, List<TreasuryDTO>>> {
    @Override
    public Map<TreasuryOperationEnum, List<TreasuryDTO>> apply(FlussoGiornaleDiCassa fGC, IngestionFlowFileDTO ingestionFlowFileDTO) {
        Map<TreasuryOperationEnum, List<TreasuryDTO>> resultMap = new EnumMap<>(TreasuryOperationEnum.class);
        List<TreasuryDTO> insertList = new LinkedList<>();
        List<TreasuryDTO> deleteList = new LinkedList<>();

        fGC.getInformazioniContoEvidenza().forEach(infoContoEvidenza -> {
            infoContoEvidenza.getMovimentoContoEvidenzas().forEach(movContoEvidenza -> {


                OrganizationDTO organizationDTO = ingestionFlowFileDTO.getOrg();
                InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = movContoEvidenza.getCliente();

                Date dataValutaRegione = movContoEvidenza.getDataValutaEnte().toGregorianCalendar().getTime();

                if (dataValutaRegione == null)
                    dataValutaRegione = movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime();

                TreasuryDTO treasuryDTO = TreasuryDTO.builder()
                        .billYear(fGC.getEsercizio().get(0).toString())
                        .billCode(movContoEvidenza.getNumeroBollettaQuietanza().toString())
                        .billIpNumber(movContoEvidenza.getImporto())
                        .billDate(movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime())
                        .receptionDate(new Date())
                        .documentCode("" + movContoEvidenza.getNumeroDocumento())
                        .regionValueDate(dataValutaRegione)
                        .organizationId(organizationDTO.getOrgId())
                        .flowIdentifierCode(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUF))
                        .iuv(null)
                        .creationDate(new Date())
                        .lastUpdateDate(new Date())
                        .ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
                        .actualSuspensionDate(movContoEvidenza.getSospesoDaRegolarizzare().getDataEffettivaSospeso().toGregorianCalendar().getTime())
                        .managementProvisionalCode(movContoEvidenza.getSospesoDaRegolarizzare().getCodiceGestionaleProvvisorio())
                        .endToEndId(movContoEvidenza.getEndToEndId())
                        .lastName(cliente.getAnagraficaCliente())
                        .address(cliente.getIndirizzoCliente())
                        .postalCode(cliente.getCapCliente())
                        .city(cliente.getLocalitaCliente())
                        .fiscalCode(cliente.getCodiceFiscaleCliente())
                        .vatNumber(cliente.getPartitaIvaCliente())
                        .build();


                if (movContoEvidenza.getTipoMovimento().equals("ENTRATA") && movContoEvidenza.getTipoDocumento().equals("SOSPESO ENTRATA"))
                    if (movContoEvidenza.getTipoOperazione().equals("ESEGUITO")) {
                        insertList.add(treasuryDTO);
                    } else if (movContoEvidenza.getTipoOperazione().equals("STORNATO")) {
                            deleteList.add(treasuryDTO);
                        }



            });
        });
        resultMap.put(TreasuryOperationEnum.INSERT, insertList);
        resultMap.put(TreasuryOperationEnum.DELETE, deleteList);
        return resultMap;
    }
}
