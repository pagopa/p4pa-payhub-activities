package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
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
			.creationDate(Instant.now())
			.lastUpdateDate(Instant.now())
			.acquiringDate(ingestionFlowFileDTO.getCreationDate().atZone(ZoneId.systemDefault()).toLocalDate())
			.organizationId(ingestionFlowFileDTO.getOrg().getOrganizationId())
			.ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
			.pspIdentifier(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.iuf(ctFlussoRiversamento.getIdentificativoFlusso())
			.flowDateTime(ctFlussoRiversamento.getDataOraFlusso().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
			.regulationUniqueIdentifier(ctFlussoRiversamento.getIdentificativoUnivocoRegolamento())
			.regulationDate(ctFlussoRiversamento.getDataRegolamento().toGregorianCalendar().toZonedDateTime().toLocalDate())
			.senderPspName(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.senderPspCode(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getCodiceIdentificativoUnivoco())
			.senderPspType(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getTipoIdentificativoUnivoco().value())
			.receiverOrganizationName(ctFlussoRiversamento.getIstitutoRicevente().getDenominazioneRicevente())
			.receiverOrganizationCode(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco())
			.receiverOrganizationType(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getTipoIdentificativoUnivoco().value())
			.totalAmountCents(ctFlussoRiversamento.getImportoTotalePagamenti().movePointRight(2).longValueExact())
			.totalPayments(ctFlussoRiversamento.getNumeroTotalePagamenti().longValueExact())
			.bicCodePouringBank(ctFlussoRiversamento.getCodiceBicBancaDiRiversamento());

		return ctFlussoRiversamento.getDatiSingoliPagamenti().stream()
			.map(item -> builder
				.iuv(item.getIdentificativoUnivocoVersamento())
				.iur(item.getIdentificativoUnivocoRiscossione())
				.transferIndex(item.getIndiceDatiSingoloPagamento())
				.amountPaidCents(item.getSingoloImportoPagato().movePointRight(2).longValueExact())
				.paymentOutcomeCode(item.getCodiceEsitoSingoloPagamento())
				.payDate(item.getDataEsitoSingoloPagamento().toGregorianCalendar().toZonedDateTime().toLocalDate())
				.build())
			.toList();
	}

	/**
	 * Maps a `PaymentsReportingDTO` object into `TransferSemanticKeyDTO`.
	 *
	 * @param paymentsReportingDTO the `PaymentsReportingDTO` object containing the data to be mapped.
	 * @return a `TransferSemanticKeyDTO` object containing the mapped data.
	 */
	public TransferSemanticKeyDTO mapToTransferSemanticKeyDto(PaymentsReportingDTO paymentsReportingDTO) {
		return TransferSemanticKeyDTO.builder()
			.orgId(paymentsReportingDTO.getOrganizationId())
			.iuv(paymentsReportingDTO.getIuv())
			.iur(paymentsReportingDTO.getIur())
			.transferIndex(paymentsReportingDTO.getTransferIndex())
			.build();
	}
}
