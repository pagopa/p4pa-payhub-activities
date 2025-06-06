package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ReceiptPagoPaEmailConfigurerServiceTest {

  private ReceiptPagoPaEmailConfigurerService receiptPagopaEmailConfigurerService;

  @BeforeEach
  void init(TestInfo info) {
    EmailTemplatesConfiguration emailTemplatesConfigurationMock = Mockito.mock(EmailTemplatesConfiguration.class);
    if(info.getTags().contains("needEmailTemplate")) {
      Mockito.lenient().when(emailTemplatesConfigurationMock.getReceivedPagopaReceipt()).thenReturn(
        EmailTemplate.builder()
          .subject("subject")
          .body("body %noticeNumber%")
          .build()
      );
    }
    receiptPagopaEmailConfigurerService = new ReceiptPagoPaEmailConfigurerService();
  }

  //region retrieveRecipients

  @ParameterizedTest
  @MethodSource("provideRetrieveRecipientsTestCases")
  void givenValidReceiptAndInstallmentWhenRetrieveRecipientsThenOk(ReceiptWithAdditionalNodeDataDTO receiptDTO, InstallmentDTO installmentDTO, List<String> expectedRecipients) {
    // when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptDTO, installmentDTO);
    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedRecipients.size(), result.size());
    Assertions.assertIterableEquals(expectedRecipients, result);
  }

  private static Stream<Arguments> provideRetrieveRecipientsTestCases() {
    return Stream.of(
      Arguments.of(
        new ReceiptWithAdditionalNodeDataDTO()
          .debtor(new PersonDTO().email("receiptDebtor@mail.it"))
          .payer(new PersonDTO().email("receiptPayer@mail.it")),
        new InstallmentDTO()
          .debtor(new PersonDTO().email("installmentDebtor@mail.it")),
        List.of("installmentDebtor@mail.it", "receiptPayer@mail.it")
      ),
      Arguments.of(
        new ReceiptWithAdditionalNodeDataDTO()
          .debtor(new PersonDTO().email("receiptDebtor@mail.it"))
          .payer(new PersonDTO().email("receiptPayer@mail.it")),
        new InstallmentDTO()
          .debtor(new PersonDTO().email(null)),
        List.of("receiptDebtor@mail.it", "receiptPayer@mail.it")
      ),
      Arguments.of(
        new ReceiptWithAdditionalNodeDataDTO()
          .debtor(new PersonDTO().email(null))
          .payer(new PersonDTO().email("receiptPayer@mail.it")),
        new InstallmentDTO()
          .debtor(new PersonDTO().email("installmentDebtor@mail.it")),
        List.of("installmentDebtor@mail.it", "receiptPayer@mail.it")
      )
    );
  }

  @Test
  void givenValidReceiptWithNoMailAndInstallmentWhenRetrieveRecipientsThenOk() {
    //given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO()
      .debtor(new PersonDTO().email(null))
      .payer(new PersonDTO().email(null));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email("installmentDebtor@mail.it"));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("installmentDebtor@mail.it", result.getFirst());
  }

  @Test
  void givenValidReceiptWithNoMailAndInstallmentWithNoMailWhenRetrieveRecipientsThenEmpty() {
    //given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO()
      .debtor(new PersonDTO().email(null))
      .payer(new PersonDTO().email(null));
    InstallmentDTO installmentDTO = new InstallmentDTO()
      .debtor(new PersonDTO().email(null));
    //when
    List<String> result = receiptPagopaEmailConfigurerService.retrieveRecipients(receiptWithAdditionalNodeDataDTO, installmentDTO);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.size());
  }
  //endregion

  @Test
  @Tag("needEmailTemplate")
  void givenValidTemplateWhenBuildTemplateParamsThenOk() {
    //given
    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO()
      .companyName("NAME")
      .orgFiscalCode("ORGFC")
      .noticeNumber("NAV")
      .paymentAmountCents(123456L)
      .paymentDateTime(OffsetDateTime.of(2025, 2, 21, 10, 30, 23, 0, ZoneOffset.UTC));

    //when
    Map<String, String> result = receiptPagopaEmailConfigurerService.buildTemplateParams(receiptWithAdditionalNodeDataDTO);

    //verify
    Assertions.assertEquals(
            Map.of(
                    "companyName", "NAME",
                    "orgFiscalCode", "ORGFC",
                    "noticeNumber", "NAV",
                    "amount", "1.234,56 €",
                    "paymentDate", "21/02/2025 10:30:23"
            ),
            result
    );
  }

}
