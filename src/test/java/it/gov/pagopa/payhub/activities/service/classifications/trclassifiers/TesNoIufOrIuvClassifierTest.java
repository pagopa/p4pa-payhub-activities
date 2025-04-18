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
class TesNoIufOrIuvClassifierTest {
	private TesNoIufOrIuvClassifier classifier = new TesNoIufOrIuvClassifier();

	@ParameterizedTest
	@MethodSource("provideClassifierScenarios")
	void classificationCoversAllCombinations(TreasuryIuf treasury, ClassificationsEnum expected) {
		ClassificationsEnum result = classifier.classify(null, null, null, treasury);
		assertEquals(expected, result);
	}

	private static Stream<Arguments> provideClassifierScenarios() {
		TreasuryIuf matchingTreasury = buildTreasuryIuf();
		TreasuryIuf treasuryNoIuf = matchingTreasury.toBuilder().iuf(null).build();
		TreasuryIuf treasuryNoIuv = matchingTreasury.toBuilder().iuv(null).build();
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(treasuryNoIuf, ClassificationsEnum.TES_NO_IUF_OR_IUV),
			Arguments.of(treasuryNoIuv, ClassificationsEnum.TES_NO_IUF_OR_IUV),
			Arguments.of(matchingTreasury, null)
		);
	}
}