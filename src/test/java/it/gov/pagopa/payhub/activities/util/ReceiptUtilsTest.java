package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.Stream;

class ReceiptUtilsTest {

	@ParameterizedTest
	@MethodSource("provideInvalidReceiptsDTOCases")
	void givenNullReceiptWithAdditionalNodeDataDTOWhenBuildReceiptFileNameThenOk(ReceiptWithAdditionalNodeDataDTO receiptDto) {
		//GIVEN
		String originalFilename = "original_filename.pdf";
		//WHEN
		String actualFileName = ReceiptUtils.buildReceiptFileName(receiptDto, originalFilename);
		//THEN
		Assertions.assertEquals(originalFilename, actualFileName);
	}

	private static Stream<ReceiptWithAdditionalNodeDataDTO> provideInvalidReceiptsDTOCases() {
		return Stream.of(
				null,
				new ReceiptWithAdditionalNodeDataDTO()
		);
	}

	@Test
	void givenValidReceiptAndNullOriginalFilenameWithAdditionalNodeDataDTOWhenBuildReceiptFileNameThenOk() {
		//GIVEN
		String originalFilename = null;
		LocalDate paymentDate = LocalDate.of(2026, 4, 1);
		ReceiptWithAdditionalNodeDataDTO receiptDto = new ReceiptWithAdditionalNodeDataDTO();
		receiptDto.setPaymentDateTime(paymentDate.atStartOfDay().atOffset(ZoneOffset.UTC));
		receiptDto.setNoticeNumber("noticeNumber");
		String expectedFilename = paymentDate + "-" + receiptDto.getNoticeNumber() + ".pdf";
		//WHEN
		String actualFileName = ReceiptUtils.buildReceiptFileName(receiptDto, originalFilename);
		//THEN
		Assertions.assertEquals(expectedFilename, actualFileName);
	}

	@Test
	void givenValidReceiptAndValidOriginalFilenameWithAdditionalNodeDataDTOWhenBuildReceiptFileNameThenOk() {
		//GIVEN
		String originalFilename = "original_filename.pdf";
		LocalDate paymentDate = LocalDate.of(2026, 4, 1);
		ReceiptWithAdditionalNodeDataDTO receiptDto = new ReceiptWithAdditionalNodeDataDTO();
		receiptDto.setPaymentDateTime(paymentDate.atStartOfDay().atOffset(ZoneOffset.UTC));
		receiptDto.setNoticeNumber("noticeNumber");
		String expectedFilename = paymentDate + "-" + receiptDto.getNoticeNumber() + ".pdf";
		//WHEN
		String actualFileName = ReceiptUtils.buildReceiptFileName(receiptDto, originalFilename);
		//THEN
		Assertions.assertEquals(expectedFilename, actualFileName);
	}
}