package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.MathUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Berechnet die Vollkosten, den Elternbeitrag und die Vergünstigung für einen Zeitabschnitt (innerhalb eines Monats)
 * einer Betreuung für das Angebot Tageseltern.
 */
public class TageselternRechner extends AbstractBGRechner {


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
		BigDecimal anzahlTageProMonat = MathUtil.EXACT.divide(parameterDTO.getAnzahlTageMaximal(), ZWOELF);
		BigDecimal betreuungsstundenProMonat = MathUtil.EXACT.multiply(anzahlTageProMonat, parameterDTO.getAnzahlStundenProTagMaximal(), anspruch);
		BigDecimal betreuungsstundenIntervall = MathUtil.EXACT.multiply(betreuungsstundenProMonat, anteilMonat);

        // Kosten Betreuungsstunde
		BigDecimal kostenProBetreuungsstunde = calculateKostenBetreuungsstunde(parameterDTO.getKostenProStundeMaximalTageseltern(), massgebendesEinkommen, anspruch, parameterDTO);

		// Vollkosten und Elternbeitrag
		BigDecimal vollkosten = MathUtil.EXACT.multiply(parameterDTO.getKostenProStundeMaximalTageseltern(), betreuungsstundenIntervall);
		BigDecimal elternbeitrag = MathUtil.EXACT.multiply(kostenProBetreuungsstunde, betreuungsstundenIntervall);

		// Runden und auf Zeitabschnitt zurückschreiben
		verfuegungZeitabschnitt.setVollkosten(MathUtil.roundToFrankenRappen(vollkosten));
		verfuegungZeitabschnitt.setElternbeitrag(MathUtil.roundToFrankenRappen(elternbeitrag));
		verfuegungZeitabschnitt.setBetreuungsstunden(MathUtil.EINE_NACHKOMMASTELLE.from(betreuungsstundenIntervall));
		return verfuegungZeitabschnitt;
	}
}
