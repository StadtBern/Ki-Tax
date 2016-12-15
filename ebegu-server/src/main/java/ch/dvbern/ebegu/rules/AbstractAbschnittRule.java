package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Wir teilen die Regeln noch auf so dass eine einzelne Regel grundsaetzlich entweder nur neue Abschnitte macht oder
 * nur Daten berechnet und setzt. Dadurch bekommen wir mehr Kontrolle wann was gemacht wird.
 * Die AbstractEbeguRule definiert aber jeweils beide Schritte. Daher machen wir jeweils noch eine Abstract rule die nichts macht
 * fuer den nicht benoetigten Schritt
 */
public abstract class AbstractAbschnittRule extends AbstractEbeguRule {


	public AbstractAbschnittRule(@Nonnull RuleKey ruleKey, @Nonnull RuleType ruleType, @Nonnull DateRange validityPeriod) {
		super(ruleKey, ruleType, validityPeriod);
	}

	//Subklassen dieser Abstrakten Klasse benoetigen diese Methode nicht da sie nur Abschnitte erstellen. Daher hier NOP
	@Override
	protected final void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		//NOP
	}
}
