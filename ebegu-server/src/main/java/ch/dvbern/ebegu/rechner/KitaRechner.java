/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.rechner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.MathUtil;

/**
 * Berechnet die Vollkosten, den Elternbeitrag und die Vergünstigung für einen Zeitabschnitt (innerhalb eines Monats)
 * einer Betreuung für das Angebot KITA.
 */
public class KitaRechner extends AbstractBGRechner {


	@Override
	public VerfuegungZeitabschnitt calculate(VerfuegungZeitabschnitt verfuegungZeitabschnitt, Verfuegung verfuegung, BGRechnerParameterDTO parameterDTO) {
		// Benoetigte Daten
		LocalDate von = verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb();
		LocalDate bis = verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis();
		LocalDate geburtsdatum = verfuegung.getBetreuung().getKind().getKindJA().getGeburtsdatum();
		BigDecimal oeffnungsstunden = verfuegung.getBetreuung().getInstitutionStammdaten().getOeffnungsstunden();
		BigDecimal oeffnungstage = verfuegung.getBetreuung().getInstitutionStammdaten().getOeffnungstage();
		BigDecimal bgPensum = MathUtil.EXACT.pctToFraction(new BigDecimal(verfuegungZeitabschnitt.getBgPensum()));
		BigDecimal massgebendesEinkommen = verfuegungZeitabschnitt.getMassgebendesEinkommen();

		// Inputdaten validieren
		checkArguments(von, bis, bgPensum, massgebendesEinkommen);
		Objects.requireNonNull(geburtsdatum, "geburtsdatum darf nicht null sein");
		Objects.requireNonNull(oeffnungsstunden, "oeffnungsstunden darf nicht null sein");
		Objects.requireNonNull(oeffnungstage, "oeffnungstage darf nicht null sein");

		// Zwischenresultate
		BigDecimal faktor = von.isAfter(geburtsdatum.plusMonths(parameterDTO.getBabyAlterInMonaten()).with(TemporalAdjusters.lastDayOfMonth())) ? FAKTOR_KIND : parameterDTO.getBabyFaktor();
		BigDecimal anteilMonat = calculateAnteilMonat(von, bis);

		// Abgeltung pro Tag: Abgeltung des Kantons plus Beitrag der Stadt
		final BigDecimal beitragStadtProTagJahr = getBeitragStadtProTagJahr(parameterDTO, verfuegung.getBetreuung().extractGesuch().getGesuchsperiode(), von);
		BigDecimal abgeltungProTag = MathUtil.EXACT.add(parameterDTO.getBeitragKantonProTag(), beitragStadtProTagJahr);
		// Massgebendes Einkommen: Minimum und Maximum berücksichtigen


		BigDecimal massgebendesEinkommenBerechnet = (massgebendesEinkommen.max(parameterDTO.getMassgebendesEinkommenMinimal())).min(parameterDTO.getMassgebendesEinkommenMaximal());
		// Öffnungstage und Öffnungsstunden; Maximum berücksichtigen
		BigDecimal oeffnungstageBerechnet = oeffnungstage.min(parameterDTO.getAnzahlTageMaximal());
		BigDecimal oeffnungsstundenBerechnet = oeffnungsstunden.min(parameterDTO.getAnzahlStundenProTagMaximal());

		// Vollkosten
		BigDecimal vollkostenZaehler = MathUtil.EXACT.multiply(abgeltungProTag, oeffnungsstundenBerechnet, oeffnungstageBerechnet, bgPensum);
		BigDecimal vollkostenNenner = MathUtil.EXACT.multiply(parameterDTO.getAnzahlStundenProTagMaximal(), ZWOELF);
		BigDecimal vollkosten = MathUtil.EXACT.divide(vollkostenZaehler, vollkostenNenner);

		// Elternbeitrag
		BigDecimal kostenProStundeMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getKostenProStundeMaximalKitaTagi(), parameterDTO.getKostenProStundeMinimal());
		BigDecimal massgebendesEinkommenMinusMin = MathUtil.EXACT.subtract(massgebendesEinkommenBerechnet, parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal massgebendesEinkommenMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getMassgebendesEinkommenMaximal(), parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal param1 = MathUtil.EXACT.multiply(kostenProStundeMaxMinusMin, massgebendesEinkommenMinusMin);
		BigDecimal param2 = MathUtil.EXACT.multiply(parameterDTO.getKostenProStundeMinimal(), massgebendesEinkommenMaxMinusMin);
		BigDecimal param1Plus2 = MathUtil.EXACT.add(param1, param2);
		BigDecimal elternbeitragZaehler = MathUtil.EXACT.multiply(param1Plus2, NEUN, ZWANZIG, bgPensum, oeffnungstageBerechnet, oeffnungsstundenBerechnet);
		BigDecimal elternbeitragNenner = MathUtil.EXACT.multiply(massgebendesEinkommenMaxMinusMin, ZWEIHUNDERTVIERZIG, parameterDTO.getAnzahlStundenProTagMaximal());
		BigDecimal elternbeitrag = MathUtil.EXACT.divide(elternbeitragZaehler, elternbeitragNenner);

		// Runden und auf Zeitabschnitt zurückschreiben
		BigDecimal vollkostenIntervall = MathUtil.EXACT.multiply(vollkosten, faktor, anteilMonat);
		BigDecimal elternbeitragIntervall;
		if (verfuegungZeitabschnitt.isBezahltVollkosten()) {
			elternbeitragIntervall = vollkostenIntervall;
		} else {
			elternbeitragIntervall = MathUtil.EXACT.multiply(elternbeitrag, anteilMonat);
		}


		verfuegungZeitabschnitt.setVollkosten(MathUtil.roundToFrankenRappen(vollkostenIntervall));
		verfuegungZeitabschnitt.setElternbeitrag(MathUtil.roundToFrankenRappen(elternbeitragIntervall));
		return verfuegungZeitabschnitt;
	}

	/**
	 * Beitrag Stadt für erstes Halbjahr oder zweites Halbjahr holen gehen
	 */
	private BigDecimal getBeitragStadtProTagJahr(BGRechnerParameterDTO parameterDTO, Gesuchsperiode gesuchsperiode, LocalDate von) {
		if (von.getYear() == gesuchsperiode.getBasisJahrPlus1()) {
			return parameterDTO.getBeitragStadtProTagJahr1();
		}
		return parameterDTO.getBeitragStadtProTagJahr2();
	}
}
