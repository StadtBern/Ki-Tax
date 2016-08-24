package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.MathUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Berechnet die Vollkosten, den Elternbeitrag und die Verguenstigung für einen Zeitabschnitt (innerhalb eines Monats)
 * einer Betreuung für das Angebot TAGI (Tagesstätte für Schulkinder).
 */
public class TagiRechner extends AbstractBGRechner {


	public VerfuegungZeitabschnitt calculate(VerfuegungZeitabschnitt verfuegungZeitabschnitt, Verfuegung verfuegung, BGRechnerParameterDTO parameterDTO) {

		// Benoetigte Daten
		LocalDate von = verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb();
		LocalDate bis = verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis();
		BigDecimal anspruch = MathUtil.EXACT.pctToFraction(new BigDecimal(verfuegungZeitabschnitt.getBgPensum()));
		BigDecimal massgebendesEinkommen = verfuegungZeitabschnitt.getMassgebendesEinkommen();

		// Inputdaten validieren
		checkArguments(von, bis, anspruch, massgebendesEinkommen);

		// Zwischenresultate
		BigDecimal anteilMonat = calculateAnteilMonat(von, bis);
		BigDecimal anzahlTageProMonat = MathUtil.EXACT.divide(parameterDTO.getAnzahlTageTagi(), ZWOELF);
		BigDecimal betreuungsstundenProMonat = MathUtil.EXACT.multiply(anzahlTageProMonat, parameterDTO.getAnzahlStundenProTagTagi(), anspruch);
		BigDecimal betreuungsstundenIntervall = MathUtil.EXACT.multiply(betreuungsstundenProMonat, anteilMonat);

		// Kosten Betreuungsstunde
		BigDecimal kostenProBetreuungsstunde = calculateKostenBetreuungsstunde(parameterDTO.getKostenProStundeMaximalKitaTagi(), massgebendesEinkommen, anspruch, parameterDTO);

		// Vollkosten und Elternbeitrag
		BigDecimal vollkosten = MathUtil.EXACT.multiply(parameterDTO.getKostenProStundeMaximalKitaTagi(), betreuungsstundenIntervall);
		BigDecimal elternbeitrag;
		if (verfuegungZeitabschnitt.isBezahltVollkosten()) {
			elternbeitrag = vollkosten;
		} else {
			elternbeitrag = MathUtil.EXACT.multiply(kostenProBetreuungsstunde, betreuungsstundenIntervall);
		}


		// Runden und auf Zeitabschnitt zurückschreiben
		verfuegungZeitabschnitt.setVollkosten(MathUtil.roundToFrankenRappen(vollkosten));
		verfuegungZeitabschnitt.setElternbeitrag(MathUtil.roundToFrankenRappen(elternbeitrag));
		return verfuegungZeitabschnitt;
	}
}
