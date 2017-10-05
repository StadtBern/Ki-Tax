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

package ch.dvbern.ebegu.vorlagen.verfuegung;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.Gueltigkeit;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;
import org.apache.commons.lang.StringUtils;

/**
 * Transferobjekt
 */
public class VerfuegungPrintImpl extends BriefPrintImpl implements VerfuegungPrint {

	private final Betreuung betreuung;

	//formatiert
	private final String letzteVerfuegungDatum;

	/**
	 * @param betreuung
	 */
	public VerfuegungPrintImpl(Betreuung betreuung, @Nullable LocalDate letzteVerfuegungDatum) {
		super(betreuung.extractGesuch());
		this.letzteVerfuegungDatum = letzteVerfuegungDatum != null ? Constants.DATE_FORMATTER.format(letzteVerfuegungDatum) : null;
		this.betreuung = betreuung;
	}

	@Override
	public String getAngebot() {

		return ServerMessageUtil.translateEnumValue(betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp());
	}

	@Override
	public String getInstitution() {

		return betreuung.getInstitutionStammdaten().getInstitution().getName();
	}

	/**
	 * @return Gesuchsteller-ReferenzNummer
	 */
	@Override
	public String getReferenznummer() {

		return betreuung.getBGNummer();
	}

	/**
	 * @return Gesuchsteller-Verfuegungsdatum
	 */
	@Override
	public String getVerfuegungsdatum() {
		return letzteVerfuegungDatum;
	}

	/**
	 * @return Name Vorname des Kindes
	 */
	@Override
	public String getKindNameVorname() {

		return extractKind().getFullName();
	}

	/**
	 * @return Geburtsdatum des Kindes
	 */
	@Override
	public String getKindGeburtsdatum() {

		return Constants.DATE_FORMATTER.format(betreuung.getKind().getKindJA().getGeburtsdatum());
	}

	/**
	 * @return Kita Name
	 */
	@Override
	public String getKitaBezeichnung() {

		return betreuung.getInstitutionStammdaten().getInstitution().getName();
	}

	/**
	 * @return AnspruchAb
	 */
	@Override
	public String getAnspruchAb() {

		return Constants.DATE_FORMATTER.format(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigAb());
	}

	/**
	 * @return AnspruchBis
	 */
	@Override
	public String getAnspruchBis() {

		return Constants.DATE_FORMATTER.format(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
	}

	/**
	 * @return VerfuegungZeitabschnitten
	 */
	@Override
	public List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt() {

		List<VerfuegungZeitabschnittPrint> result = new ArrayList<>();
		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent()) {

			result.addAll(verfuegung.get().getZeitabschnitte().stream()
				.sorted(Gueltigkeit.GUELTIG_AB_COMPARATOR.reversed())
				.map(VerfuegungZeitabschnittPrintImpl::new)
				.collect(Collectors.toList()));
			ListIterator<VerfuegungZeitabschnittPrint> listIterator = result.listIterator();
			while (listIterator.hasNext()) {
				VerfuegungZeitabschnittPrint zeitabschnitt = listIterator.next();
				if (zeitabschnitt.getBetreuung() <= 0) {
					listIterator.remove();
				} else {
					break;
				}
			}

			Collections.reverse(result);
			listIterator = result.listIterator();
			while (listIterator.hasNext()) {
				VerfuegungZeitabschnittPrint zeitabschnitt = listIterator.next();
				if (zeitabschnitt.getBetreuung() <= 0) {
					listIterator.remove();
				} else {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Wenn die Betreuung VERFUEGT ist -> manuelle Bemerkungen Wenn die Betreuung noch nicht VERFUEGT ist -> generated
	 * Bemerkungen
	 */
	@Override
	public List<AufzaehlungPrint> getManuelleBemerkungen() {

		List<AufzaehlungPrint> bemerkungen = new ArrayList<>();
		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent() && StringUtils.isNotEmpty(verfuegung.get().getManuelleBemerkungen())) {
			bemerkungen.addAll(splitBemerkungen(verfuegung.get().getManuelleBemerkungen()));
		}
		return bemerkungen;
	}

	/**
	 * Zerlegt die Bemerkungen (Delimiter \n) und bereitet die in einer Liste.
	 *
	 * @return List mit Bemerkungen
	 */
	private List<AufzaehlungPrint> splitBemerkungen(String bemerkungen) {

		List<AufzaehlungPrint> list = new ArrayList<>();
		// Leere Zeile werden mit diese Annotation [\\r\\n]+ entfernt
		String[] splitBemerkungenNewLine = bemerkungen.split("[" + System.getProperty("line.separator") + "]+");
		for (String bemerkung : splitBemerkungenNewLine) {
			list.add(new AufzaehlungPrintImpl(bemerkung));
		}
		return list;
	}

	/**
	 * @return true falls Pensum groesser 0 ist
	 */
	@Override
	public boolean isPensumGrosser0() {

		List<VerfuegungZeitabschnittPrint> vzList = getVerfuegungZeitabschnitt();
		int value = 0;
		for (VerfuegungZeitabschnittPrint verfuegungZeitabschnitt : vzList) {
			value = value + verfuegungZeitabschnitt.getBGPensum();
			// BG-Pensum
		}
		return value > 0;
	}

	@Override
	public boolean isPensumIst0() {

		return !isPensumGrosser0();
	}

	@Override
	public boolean isVorgaengerVerfuegt() {
		return letzteVerfuegungDatum != null;
	}

	@Override
	public boolean isPrintManuellebemerkung() {

		return !getManuelleBemerkungen().isEmpty();
	}

	@Nonnull
	private Kind extractKind() {

		return betreuung.getKind().getKindJA();
	}

	@Nonnull
	private Optional<Verfuegung> extractVerfuegung() {

		Verfuegung verfuegung = betreuung.getVerfuegung();
		if (verfuegung != null) {
			return Optional.of(verfuegung);
		}
		return Optional.empty();
	}

}
