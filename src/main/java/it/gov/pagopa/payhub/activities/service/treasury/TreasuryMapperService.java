package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.FlussoTesoreriaPIIDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;

@Lazy
@Service
public class TreasuryMapperService implements BiFunction<Object, IngestionFlowFileDTO, Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>>> {

    public static final String INSERT = "INSERT";
    public static final String DELETE = "DELETE";


    @Override
    public Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> apply(Object flussoGiornaleDiCassa, IngestionFlowFileDTO ingestionFlowFileDTO) {
        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> resultMap = new HashMap<>();
        List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> insertList = new LinkedList<>();
        List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> deleteList = new LinkedList<>();

        try {
            Method getInformazioniContoEvidenza = flussoGiornaleDiCassa.getClass().getMethod("getInformazioniContoEvidenza");
            Method getEsercizio = flussoGiornaleDiCassa.getClass().getMethod("getEsercizio");

            List<?> informazioniContoEvidenzaList = (List<?>) getInformazioniContoEvidenza.invoke(flussoGiornaleDiCassa);
            List<?> esercizioList = (List<?>) getEsercizio.invoke(flussoGiornaleDiCassa);

            for (Object informazioniContoEvidenza : informazioniContoEvidenzaList) {
                Method getMovimentoContoEvidenzas = informazioniContoEvidenza.getClass().getMethod("getMovimentoContoEvidenzas");
                List<?> movimentoContoEvidenzasList = (List<?>) getMovimentoContoEvidenzas.invoke(informazioniContoEvidenza);

                for (Object movimentoContoEvidenza : movimentoContoEvidenzasList) {
                    processMovimentoContoEvidenza(movimentoContoEvidenza, esercizioList, ingestionFlowFileDTO, insertList, deleteList);
                }
            }
        } catch (Exception e) {
            throw new ActivitiesException("Error during mapping " + e.getMessage());
        }

        resultMap.put(INSERT, insertList);
        resultMap.put(DELETE, deleteList);
        return resultMap;
    }

    private void processMovimentoContoEvidenza(Object movimentoContoEvidenza, List<?> esercizioList, IngestionFlowFileDTO ingestionFlowFileDTO, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> insertList, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> deleteList) throws ActivitiesException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method getCliente = movimentoContoEvidenza.getClass().getMethod("getCliente");
        Method getDataValutaEnte = movimentoContoEvidenza.getClass().getMethod("getDataValutaEnte");
        Method getDataMovimento = movimentoContoEvidenza.getClass().getMethod("getDataMovimento");
        Method getTipoMovimento = movimentoContoEvidenza.getClass().getMethod("getTipoMovimento");
        Method getTipoDocumento = movimentoContoEvidenza.getClass().getMethod("getTipoDocumento");
        Method getTipoOperazione = movimentoContoEvidenza.getClass().getMethod("getTipoOperazione");
        Method getNumeroBollettaQuietanza = movimentoContoEvidenza.getClass().getMethod("getNumeroBollettaQuietanza");
        Method getImporto = movimentoContoEvidenza.getClass().getMethod("getImporto");
        Method getNumeroDocumento = movimentoContoEvidenza.getClass().getMethod("getNumeroDocumento");
        Method getCausale = movimentoContoEvidenza.getClass().getMethod("getCausale");
        Method getSospesoDaRegolarizzare = movimentoContoEvidenza.getClass().getMethod("getSospesoDaRegolarizzare");
        Method getEndToEndId = movimentoContoEvidenza.getClass().getMethod("getEndToEndId");

        Object cliente = getCliente.invoke(movimentoContoEvidenza);
        Date regionValueDate;

        XMLGregorianCalendar billDateGC = (XMLGregorianCalendar) getDataMovimento.invoke(movimentoContoEvidenza);
        Date billDate=billDateGC.toGregorianCalendar().getTime();

        if(getDataValutaEnte.invoke(movimentoContoEvidenza) != null){
            XMLGregorianCalendar regionValueDateGC = (XMLGregorianCalendar) getDataValutaEnte.invoke(movimentoContoEvidenza);
            regionValueDate= regionValueDateGC.toGregorianCalendar().getTime();
        }
        else{
            regionValueDate= billDate;
        }

        TreasuryDTO treasuryDTO = TreasuryDTO.builder()
                .billYear(esercizioList != null && !esercizioList.isEmpty() ? esercizioList.get(0).toString() : null)
                .billCode(getNumeroBollettaQuietanza.invoke(movimentoContoEvidenza).toString())
                .billIpNumber((BigDecimal) getImporto.invoke(movimentoContoEvidenza))
                .billDate(billDate)
                .receptionDate(new Date())
                .documentCode(getNumeroDocumento.invoke(movimentoContoEvidenza).toString())
                .regionValueDate(regionValueDate)
                .organizationId(ingestionFlowFileDTO.getOrg().getOrgId())
                .flowIdentifierCode(TreasuryUtils.getIdentificativo((String) getCausale.invoke(movimentoContoEvidenza), TreasuryUtils.IUF))
                .iuv(null)
                .creationDate(new Date())
                .lastUpdateDate(new Date())
                .ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
                .actualSuspensionDate(getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza) != null
                        ? ((GregorianCalendar) getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza).getClass().getMethod("getDataEffettivaSospeso").invoke(getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza))).getTime()
                        : null)
                .managementProvisionalCode(getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza) != null
                        ? getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza).getClass().getMethod("getCodiceGestionaleProvvisorio").invoke(getSospesoDaRegolarizzare.invoke(movimentoContoEvidenza)).toString()
                        : null)
                .endToEndId((String) getEndToEndId.invoke(movimentoContoEvidenza))
                .lastName((String) (cliente.getClass().getMethod("getAnagraficaCliente").invoke(cliente)))
                .address((String) (cliente.getClass().getMethod("getIndirizzoCliente").invoke(cliente)))
                .postalCode((String) (cliente.getClass().getMethod("getCapCliente").invoke(cliente)))
                .city((String) (cliente.getClass().getMethod("getLocalitaCliente").invoke(cliente)))
                .fiscalCode((String) (cliente.getClass().getMethod("getCodiceFiscaleCliente").invoke(cliente)))
                .vatNumber((String) (cliente.getClass().getMethod("getPartitaIvaCliente").invoke(cliente)))
                .build();

        if (getTipoMovimento.invoke(movimentoContoEvidenza).equals("ENTRATA") && getTipoDocumento.invoke(movimentoContoEvidenza).equals("SOSPESO ENTRATA")) {
            if (getTipoOperazione.invoke(movimentoContoEvidenza).equals("ESEGUITO")) {
                FlussoTesoreriaPIIDTO flussoTesoreriaPIIDTO = FlussoTesoreriaPIIDTO.builder()
                        .deCausale(getCausale.invoke(movimentoContoEvidenza).toString())
                        .deCognome(cliente.getClass().getMethod("getAnagraficaCliente").invoke(cliente).toString())
                        .build();
                insertList.add(Pair.of(treasuryDTO, flussoTesoreriaPIIDTO));
            } else if (getTipoOperazione.invoke(movimentoContoEvidenza).equals("STORNATO")) {
                deleteList.add(Pair.of(treasuryDTO, null));
            }
        }
    }
}
