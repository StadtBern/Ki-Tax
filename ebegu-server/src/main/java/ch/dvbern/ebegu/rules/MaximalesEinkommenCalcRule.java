package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

/**
 * Setzt fuer die Zeitabschnitte das Massgebende Einkommen. Sollte der Maximalwert uebschritte werden so wird das Pensum auf 0 gesetzt
 * ACHTUNG: Diese Regel gilt nur fuer Kita und Tageseltern Kleinkinder.  Bei Tageseltern Schulkinder und Tagesstaetten
 * gibt es keine Reduktion des Anspruchs.
 * Regel 16.7 Maximales Einkommen
 */
public class MaximalesEinkommenCalcRule extends AbstractCalcRule {


	private BigDecimal maximalesEinkommen;


	public MaximalesEinkommenCalcRule(DateRange validityPeriod, BigDecimal maximalesEinkommen) {
		super(RuleKey.MAXIMALES_EINKOMMEN, RuleType.REDUKTIONSREGEL, validityPeriod);
		this.maximalesEinkommen = maximalesEinkommen;
	}


	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		BetreuungsangebotTyp typ = betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp();
//todo team Alter Kind pruefen
//		if (BetreuungsangebotTyp.KITA.equals(typ) || BetreuungsangebotTyp.TAGESELTERN.equals(typ)) {
			if (verfuegungZeitabschnitt.getMassgebendesEinkommen().compareTo(maximalesEinkommen) > 0) {
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.MAXIMALES_EINKOMMEN.name() + ": Maximales Einkommen überschritten");
			}
//		}
	}
}
