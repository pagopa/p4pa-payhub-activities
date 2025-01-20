package it.gov.pagopa.payhub.activities.service.treasury.opi14;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TreasuryMapperOpi14Service implements TreasuryMapperService<FlussoGiornaleDiCassa> {

    @Override
    public Map<TreasuryOperationEnum, List<Treasury>> apply(FlussoGiornaleDiCassa fGC, IngestionFlowFileDTO ingestionFlowFileDTO) {
        Organization organizationDTO = ingestionFlowFileDTO.getOrg();

        return fGC.getInformazioniContoEvidenza().stream()
                .flatMap(infoContoEvidenza -> infoContoEvidenza.getMovimentoContoEvidenzas().stream())
                .filter(movContoEvidenza -> movContoEvidenza.getTipoMovimento().equals("ENTRATA")
                        && movContoEvidenza.getTipoDocumento().equals("SOSPESO ENTRATA")
                        && (movContoEvidenza.getTipoOperazione().equals("ESEGUITO")
                        || movContoEvidenza.getTipoOperazione().equals("STORNATO")))
                .map(movContoEvidenza -> {
                    InformazioniContoEvidenza.MovimentoContoEvidenza.Cliente cliente = movContoEvidenza.getCliente();

                    XMLGregorianCalendar regionValueDate = Optional.ofNullable(movContoEvidenza.getDataValutaEnte())
                            .orElseGet(movContoEvidenza::getDataMovimento);

                    Treasury treasuryDTO = Treasury.builder()
                            .billYear(fGC.getEsercizio().getFirst().toString())
                            .billCode(movContoEvidenza.getNumeroBollettaQuietanza().toString())
                            .billAmountCents(movContoEvidenza.getImporto().movePointRight(2).longValueExact())
                            .billDate(Utilities.convertToLocalDate(movContoEvidenza.getDataMovimento()))
                            .receptionDate(OffsetDateTime.now())
                            .documentCode(String.valueOf(movContoEvidenza.getNumeroDocumento()))
                            .regionValueDate(Utilities.convertToLocalDate(regionValueDate))
                            .organizationId(organizationDTO.getOrganizationId())
                            .remittanceDescription(movContoEvidenza.getCausale())
                            .iuf(TreasuryUtils.getIdentificativo(movContoEvidenza.getCausale(), TreasuryUtils.IUF))
                            .iuv(null)
                            .creationDate(OffsetDateTime.now())
                            .updateDate(OffsetDateTime.now())
                            .ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
                            .actualSuspensionDate(Optional.ofNullable(movContoEvidenza.getSospesoDaRegolarizzare())
                                    .map(s -> Utilities.convertToLocalDate(s.getDataEffettivaSospeso()))
                                    .orElse(null))
                            .managementProvisionalCode(Optional.ofNullable(movContoEvidenza.getSospesoDaRegolarizzare())
                                    .map(InformazioniContoEvidenza.MovimentoContoEvidenza.SospesoDaRegolarizzare::getCodiceGestionaleProvvisorio)
                                    .orElse(null))
                            .endToEndId(movContoEvidenza.getEndToEndId())
                            .pspLastName(cliente.getAnagraficaCliente())
                            .pspAddress(cliente.getIndirizzoCliente())
                            .pspPostalCode(cliente.getCapCliente())
                            .pspCity(cliente.getLocalitaCliente())
                            .pspFiscalCode(cliente.getCodiceFiscaleCliente())
                            .pspVatNumber(cliente.getPartitaIvaCliente())
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
