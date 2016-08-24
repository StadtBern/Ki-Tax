package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

/**
 * Setzt fuer die Zeitabschnitte das Massgebende Einkommen. Sollte der Maximalwert uebschritte werden so wird das Pensum auf 0 gesetzt
 * ACHTUNG: Diese Regel gilt nur fuer Kita und Tageseltern Kleinkinder.  Bei Tageseltern Schulkinder und Tagesstaetten
 * gibt es keine Reduktion des Anspruchs.
 * Regel 16.7 Maximales Einkommen
 */
public class EinkommenCalcRule extends AbstractCalcRule {


	private BigDecimal maximalesEinkommen;


	public EinkommenCalcRule(DateRange validityPeriod, BigDecimal maximalesEinkommen) {
		super(RuleKey.EINKOMMEN, RuleType.REDUKTIONSREGEL, validityPeriod);
		this.maximalesEinkommen = maximalesEinkommen;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			if (verfuegungZeitabschnitt.getMassgebendesEinkommen().compareTo(maximalesEinkommen) > 0) {
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN.name() + ": Maximales Einkommen überschritten");
			}
		}
	}
}
