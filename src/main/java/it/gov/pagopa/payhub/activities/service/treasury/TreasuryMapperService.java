package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDto;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.InformazioniContoEvidenza;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

@Lazy
@Service
public class TreasuryMapperService implements BiFunction<FlussoGiornaleDiCassa, IngestionFlowFileDTO, TreasuryDto> {

    /**
     * Maps the given `CtFlussoRiversamento` and `IngestionFlowFileDTO` into a `PaymentsReportingDTO` object.
     *
     * @param flussoGiornaleDiCassa the flow data object containing information about the transaction flow.
     * @param ingestionFlowFileDTO the ingestion metadata containing information about the processing flow.
     * @return a fully populated `PaymentsReportingDTO` object containing mapped data.
     */
    @Override
    public TreasuryDto apply(FlussoGiornaleDiCassa flussoGiornaleDiCassa, IngestionFlowFileDTO ingestionFlowFileDTO) {
        InformazioniContoEvidenza informazioniContoEvidenza= flussoGiornaleDiCassa.getInformazioniContoEvidenza().get(0);
        InformazioniContoEvidenza.MovimentoContoEvidenza movimentoContoEvidenza = informazioniContoEvidenza.getMovimentoContoEvidenzas().get(0);


        return TreasuryDto.builder()
                .deAnnoBolletta(flussoGiornaleDiCassa.getEsercizio().get(0).toString())
                .codBolletta(movimentoContoEvidenza.getNumeroBollettaQuietanza().toString())
                .codConto()
                .codIdDominio()
                .codTipoMovimento()
                .codCausale()
                .deCausale()
                .numIpBolletta()
                .dtBolletta()
                .dtRicezione()
                .deAnnoDocumento()
                .codDocumento()
                .codBollo()
                .deCognome()
                .deNome()
                .deVia()
                .deCap()
                .deCitta()
                .codCodiceFiscale()
                .codPartitaIva()
                .codAbi()
                .codCab()
                .codContoAnagrafica()
                .deAeProvvisorio()
                .codProvvisorio()
                .codIban()
                .codTipoConto()
                .codProcesso()
                .codPgEsecuzione()
                .codPgTrasferimento()
                .numPgProcesso()
                .dtDataValutaRegione()
                .mygovEnteId()
                .codIdUnivocoFlusso()
                .codIdUnivocoVersamento()
                .dtCreazione()
                .dtUltimaModifica()
                .flgRegolarizzata()
                .mygovManageFlussoId() //context.mygov_manage_flusso_id
                .dtEffettivaSospeso()
                .codiceGestionaleProvvisorio()
                .endToEndId()
                .build();
    }



}
