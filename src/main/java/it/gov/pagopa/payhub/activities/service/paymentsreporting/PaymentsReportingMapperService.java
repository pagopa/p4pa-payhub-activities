package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class responsible for mapping flow data (`CtFlussoRiversamento`) and ingestion metadata
 * (`IngestionFlowFileDTO`) into a list of `PaymentsReportingDTO` objects.
 */
@Lazy
@Service
public class PaymentsReportingMapperService {

	/**
	 * Maps a `CtFlussoRiversamento` object and an `IngestionFlowFileDTO` into a list of `PaymentsReportingDTO` objects.
	 * Each `PaymentsReportingDTO` in the list corresponds to an individual payment within the flow.
	 *
	 * @param ctFlussoRiversamento the flow data object containing detailed information about the transaction flow,
	 *                             including details for multiple individual payments.
	 * @param ingestionFlowFileDTO the ingestion metadata containing information about the processing flow.
	 * @return a list of `PaymentsReportingDTO` objects, one for each individual payment in the flow.
	 */
	public List<PaymentsReportingDTO> mapToDtoList(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFileDTO ingestionFlowFileDTO) {

		PaymentsReportingDTO.PaymentsReportingDTOBuilder builder = PaymentsReportingDTO.builder()
			.orgId(ingestionFlowFileDTO.getOrg())
			.ingestionFlowFile(ingestionFlowFileDTO)
			.idPsp(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.flowIdentifierCode(ctFlussoRiversamento.getIdentificativoFlusso())
			.flowDateTime(ctFlussoRiversamento.getDataOraFlusso().toGregorianCalendar().getTime())
			.regulationUniqueIdentifier(ctFlussoRiversamento.getIdentificativoUnivocoRegolamento())
			.regulationDate(ctFlussoRiversamento.getDataRegolamento().toGregorianCalendar().getTime())
			.senderPspName(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.senderPspCode(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getCodiceIdentificativoUnivoco())
			.senderPspType(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getTipoIdentificativoUnivoco().value())
			.receiverOrganizationName(ctFlussoRiversamento.getIstitutoRicevente().getDenominazioneRicevente())
			.receiverOrganizationId(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco())
			.receiverOrganizationType(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getTipoIdentificativoUnivoco().value())
			.totalPayments(ctFlussoRiversamento.getDatiSingoliPagamenti().size())
			.sumPayments(ctFlussoRiversamento.getNumeroTotalePagamenti())
			.amountPaid(ctFlussoRiversamento.getImportoTotalePagamenti())
			.bicCodePouringBank(ctFlussoRiversamento.getCodiceBicBancaDiRiversamento());

		return ctFlussoRiversamento.getDatiSingoliPagamenti().stream()
			.map(item -> builder
				.creditorReferenceId(item.getIdentificativoUnivocoVersamento())
				.regulationId(item.getIdentificativoUnivocoRiscossione())
				.transferIndex(item.getIndiceDatiSingoloPagamento())
				.amountPaid(item.getSingoloImportoPagato())
				.paymentOutcomeCode(item.getCodiceEsitoSingoloPagamento())
				.payDate(item.getDataEsitoSingoloPagamento().toGregorianCalendar().getTime())
				.build())
			.toList();
	}
}
