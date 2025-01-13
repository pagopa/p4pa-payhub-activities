package it.gov.pagopa.payhub.activities.service.treasury.opi14;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TreasuryMapperOpi14Service implements TreasuryMapperService<FlussoGiornaleDiCassa> {

    @Override
    public Map<TreasuryOperationEnum, List<TreasuryDTO>> apply(FlussoGiornaleDiCassa fGC, IngestionFlowFileDTO ingestionFlowFileDTO) {
        Organization organizationDTO = ingestionFlowFileDTO.getOrg();

        return fGC.getInformazioniContoEvidenza().stream()
                .flatMap(infoContoEvidenza -> infoContoEvidenza.getMovimentoContoEvidenzas().stream())
                .filter(movContoEvidenza -> movContoEvidenza.getTipoMovimento().equals("ENTRATA")
                        && movContoEvidenza.getTipoDocumento().equals("SOSPESO ENTRATA")
                        && (movContoEvidenza.getTipoOperazione().equals("ESEGUITO")
                        || movContoEvidenza.getTipoOperazione().equals("STORNATO")))
                .map(movContoEvidenza -> {
                    InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = movContoEvidenza.getCliente();

                    Date regionValueDate = Optional.ofNullable(movContoEvidenza.getDataValutaEnte())
                            .map(d -> d.toGregorianCalendar().getTime())
                            .orElseGet(() -> movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime());

                    TreasuryDTO treasuryDTO = TreasuryDTO.builder()
                            .billYear(fGC.getEsercizio().getFirst().toString())
                            .billCode(movContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .billIpNumber(movContoEvidenza.getImporto())
                            .billDate(movContoEvidenza.getDataMovimento().toGregorianCalendar().getTime())
                            .receptionDate(new Date())
                            .documentCode(String.valueOf(movContoEvidenza.getNumeroDocumento()))
                            .regionValueDate(regionValueDate)
                            .organizationId(organizationDTO.getOrganizationId())
                            .iuf(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUF))
                            .iuv(null)
                            .creationDate(new Date())
                            .lastUpdateDate(new Date())
                            .ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
                            .actualSuspensionDate(Optional.ofNullable(movContoEvidenza.getSospesoDaRegolarizzare())
                                    .map(s -> s.getDataEffettivaSospeso().toGregorianCalendar().getTime())
                                    .orElse(null))
                            .managementProvisionalCode(Optional.ofNullable(movContoEvidenza.getSospesoDaRegolarizzare())
                                    .map(InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare::getCodiceGestionaleProvvisorio)
                                    .orElse(null))
                            .endToEndId(movContoEvidenza.getEndToEndId())
                            .lastName(cliente.getAnagraficaCliente())
                            .address(cliente.getIndirizzoCliente())
                            .postalCode(cliente.getCapCliente())
                            .city(cliente.getLocalitaCliente())
                            .fiscalCode(cliente.getCodiceFiscaleCliente())
                            .vatNumber(cliente.getPartitaIvaCliente())
                            .build();

                    TreasuryOperationEnum operation = "ESEGUITO".equals(movContoEvidenza.getTipoOperazione())
                            ? TreasuryOperationEnum.INSERT
                            : TreasuryOperationEnum.DELETE;

                    return Map.entry(operation, treasuryDTO);
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }
}
