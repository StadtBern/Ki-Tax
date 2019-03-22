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

package ch.dvbern.ebegu.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSortedSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Allgemeine Utils fuer EBEGU
 */
public class EbeguUtil {

	/**
	 * Berechnet ob die Daten bei der Familiensituation von einem GS auf 2 GS geaendert wurde.
	 */
	public static boolean fromOneGSToTwoGS(FamiliensituationContainer familiensituationContainer) {
		Validate.notNull(familiensituationContainer);
		Validate.notNull(familiensituationContainer.getFamiliensituationJA());
		Validate.notNull(familiensituationContainer.getFamiliensituationErstgesuch());

		return fromOneGSToTwoGS(familiensituationContainer.getFamiliensituationErstgesuch(), familiensituationContainer.getFamiliensituationJA());
	}

	public static boolean fromOneGSToTwoGS(Familiensituation oldFamiliensituation, Familiensituation newFamiliensituation) {
		Validate.notNull(oldFamiliensituation);
		Validate.notNull(newFamiliensituation);
		return !oldFamiliensituation.hasSecondGesuchsteller() && newFamiliensituation.hasSecondGesuchsteller();
	}

	/**
	 * Gibt aus einer Liste von Gesuchen nur das jeweils neueste (aktuellstes Jahr und hoechste Laufummer) pro Fall zurueck.
	 * Die Rueckgabe erfolgt in einer Map mit GesuchId-Gesuch
	 */
	public static Map<String, Gesuch> groupByFallAndSelectNewestAntrag(List<Gesuch> allGesuche) {
		ArrayListMultimap<Fall, Gesuch> fallToAntragMultimap = ArrayListMultimap.create();
		allGesuche.forEach(gesuch -> fallToAntragMultimap.put(gesuch.getFall(), gesuch));
		// map erstellen in der nur noch das gesuch mit der hoechsten laufnummer drin ist
		return fallToAntragMultimap.asMap().values().stream()
			.map(gesuche -> ImmutableSortedSet.copyOf(getNewestGesuchComparator(), gesuche).last())
			.collect(Collectors.toMap(Gesuch::getId, Function.identity()));
	}

	private static Comparator<Gesuch> getNewestGesuchComparator() {
		return (g1, g2) -> ComparisonChain.start()
			.compare(g1.getGesuchsperiode().getBasisJahr(), g2.getGesuchsperiode().getBasisJahr())
			.compare(g1.getLaufnummer(), g2.getLaufnummer()).result();
	}

	public static boolean isSameObject(@Nullable AbstractEntity thisEntity, @Nullable AbstractEntity otherEntity) {
		return (thisEntity == null && otherEntity == null)
			|| (thisEntity != null && otherEntity != null && thisEntity.isSame(otherEntity));
	}

	/**
	 * Returns true if both strings have the same content or both are null or emptystrings
	 * or one is emptystring and the other is null
	 */
	public static boolean isSameOrNullStrings(@Nullable String thisString, @Nullable String otherString) {
		return (StringUtils.isBlank(thisString) && StringUtils.isBlank(otherString))
			|| Objects.equals(thisString, otherString);
	}

	/**
	 * Returns true if both strings have the same content or both are null or emptystrings
	 * or one is emptystring and the other is null
	 */
	public static boolean isSameOrNullBoolean(@Nullable Boolean thisBoolean, @Nullable Boolean otherBoolean) {
		return (isNullOrFalse(thisBoolean) && isNullOrFalse(otherBoolean))
			|| Objects.equals(thisBoolean, otherBoolean);
	}

	public static boolean isNullOrFalse(@Nullable Boolean value) {
		return value == null || !value;
	}

	/**
	 * Returns true if both list are null or if they have the same number of elements
	 */
	public static boolean areListsSameSize(@Nullable Set<Dokument> dokumente, @Nullable Set<Dokument> otherDokumente) {
		if (dokumente == null && otherDokumente == null) {
			return true;
		}
		if (dokumente != null && otherDokumente != null) {
			return dokumente.size() == otherDokumente.size();
		}
		return false;
	}

	public static boolean isFinanzielleSituationRequired(@Nonnull Gesuch gesuch) {
		return !gesuch.getGesuchsperiode().hasTagesschulenAnmeldung() ||
			(
				(
					gesuch.getGesuchsperiode().hasTagesschulenAnmeldung()
						&& gesuch.hasBetreuungOfJugendamt()
						&& !gesuch.getGesuchsperiode().isVerpflegungenActive()
				)
				|| (
					gesuch.getFamiliensituationContainer() != null
					&& gesuch.getFamiliensituationContainer().getFamiliensituationJA() != null
					&& Objects.equals(false, gesuch.getFamiliensituationContainer().getFamiliensituationJA().getSozialhilfeBezueger())
					&& Objects.equals(true, gesuch.getFamiliensituationContainer().getFamiliensituationJA().getVerguenstigungGewuenscht())
				)
			);
	}

	public static boolean isSozialhilfeBezuegerNull(@Nonnull Gesuch gesuch) {
		return (gesuch.getGesuchsperiode().hasTagesschulenAnmeldung() || gesuch.getGesuchsperiode().isVerpflegungenActive())
			&& (gesuch.getFamiliensituationContainer() != null && gesuch.getFamiliensituationContainer().getFamiliensituationJA() != null
			&& gesuch.getFamiliensituationContainer().getFamiliensituationJA().getSozialhilfeBezueger() == null);
	}

	public static boolean isFinanzielleSituationNotIntroduced(@Nonnull Gesuch gesuch) {
		return gesuch.getGesuchsteller1() == null
			|| (gesuch.getGesuchsteller1().getFinanzielleSituationContainer() == null
			&& gesuch.getEinkommensverschlechterungInfoContainer() == null);
	}
}
