package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker.buildTreasuryIuf;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TesNoMatchClassifierTest {
	private TesNoMatchClassifier classifier = new TesNoMatchClassifier();


	@ParameterizedTest
	@MethodSource("provideClassifierScenarios")
	void classificationCoversAllCombinations(TreasuryIuf treasury, ClassificationsEnum expected) {
		ClassificationsEnum result = classifier.classify(null, null, null, treasury);
		assertEquals(expected, result);
	}

	private static Stream<Arguments> provideClassifierScenarios() {
		TreasuryIuf treasury = buildTreasuryIuf();
		TreasuryIuf treasuryNoIuf = treasury.toBuilder().iuf(null).build();
		TreasuryIuf treasuryNoIuv = treasury.toBuilder().iuv(null).build();
		TreasuryIuf treasuryNoIufNoIuv = treasury.toBuilder().iuv(null).iuf(null).build();
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(treasury, null),
			Arguments.of(treasuryNoIuf, null),
			Arguments.of(treasuryNoIuv, null),
			Arguments.of(treasuryNoIufNoIuv, ClassificationsEnum.TES_NO_MATCH)
		);
	}
}