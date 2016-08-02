package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Wir teilen die Regeln noch auf so dass eine einzelne Regel grundsaetzlich entweder nur neue Abschnitte macht oder
 * nur Daten berechnet und setzt. Dadurch bekommen wir mehr Kontrolle wann was gemacht wird.
 * Die AbstractEbeguRule definiert aber jeweils beide Schritte. Daher machen wir jeweils noch eine Abstract rule die nichts macht
 * fuer den nicht benoetigten Schritt
 */
public abstract class AbstractCalcRule extends AbstractEbeguRule {


	public AbstractCalcRule(@Nonnull RuleKey ruleKey, @Nonnull RuleType ruleType, @Nonnull DateRange validityPeriod) {
		super(ruleKey, ruleType, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		return new ArrayList<>();
	}
}
