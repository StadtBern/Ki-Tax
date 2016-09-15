package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 *
 * Als allerletzte Reduktionsregel läuft eine Regel die das Feld "AnspruchberechtigtesPensum"
 * mit dem Feld "AnspruchspensumRest" vergleicht. Wenn letzteres -1 ist gilt der Wert im Feld "AnspruchsberechtigtesPensum,
 * ansonsten wir das Minimum der beiden Felder in das Feld "AnspruchberechtigtesPensum" gesetzt wenn es sich um eine
 * Kita/Kleinkinder-Betreuung handelt
 * Dadurch wird das Anspruchspensum limitiert auf den Maximal moeglichen Restanspruch
 *
 *
 */
public class RestanspruchLimitCalcRule extends AbstractCalcRule {

	public RestanspruchLimitCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.RESTANSPRUCH, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Fuer Kleinkinderangebote den Restanspruch bereucksichtigen
		if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			int anspruchberechtigtesPensum = verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
			int verfuegbarerRestanspruch = verfuegungZeitabschnitt.getAnspruchspensumRest();
			//wir muessen nur was machen wenn wir schon einen Restanspruch gesetzt haben
			if(verfuegbarerRestanspruch != -1){
				if (verfuegbarerRestanspruch < anspruchberechtigtesPensum) {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.RESTANSPRUCH, MsgKey.RESTANSPRUCH_MSG, anspruchberechtigtesPensum , verfuegbarerRestanspruch);
					verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(verfuegbarerRestanspruch);
				}
			}
		}
		// fuer Schulkinder wird nichts gemacht
	}
}
