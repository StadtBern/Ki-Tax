package ch.dvbern.ebegu.vorlagen.berechnungsblatt;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 03.10.2016
*/

import static ch.dvbern.ebegu.vorlagen.PrintUtil.getGesuchstellerAdresse;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;

public class FamilienSituaionPrintImpl implements FamilienSituaionPrint {

	private Gesuch gesuch;

	/**
	 * Konstruktor
	 *
	 * @param gesuch
	 */
	public FamilienSituaionPrintImpl(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	/**
	 * @return GesuchstellerName
	 */
	@Override
	public String getGesuchstellerName() {

		StringBuilder name = new StringBuilder();
		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1();
		if (gesuchsteller.isPresent()) {
			name.append(gesuchsteller.get().getFullName());
		}
		if (gesuch.getGesuchsteller2() != null) {
			Optional<Gesuchsteller> gesuchsteller2 = extractGesuchsteller2();
			if (gesuchsteller.isPresent()) {
				name.append("\n");
				name.append(gesuchsteller2.get().getFullName());
			}
		}
		return name.toString();
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */
	@Override
	public String getGesuchstellerStrasse() {

		if (extractGesuchsteller1().isPresent()) {
			Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse(extractGesuchsteller1().get());
			if (gesuchstellerAdresse.isPresent()) {
				return gesuchstellerAdresse.get().getStrasse();
			}
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */
	@Override
	public String getGesuchstellerPLZStadt() {

		if (extractGesuchsteller1().isPresent()) {
			Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse(extractGesuchsteller1().get());
			if (gesuchstellerAdresse.isPresent()) {
				return gesuchstellerAdresse.get().getPlz() + " " + gesuchstellerAdresse.get().getOrt();
			}
		}
		return "";
	}

	@Nonnull
	private Optional<Gesuchsteller> extractGesuchsteller1() {

		Gesuchsteller gs1 = gesuch.getGesuchsteller1();
		if (gs1 != null) {
			return Optional.of(gs1);
		}
		return Optional.empty();
	}

	@Nonnull
	private Optional<Gesuchsteller> extractGesuchsteller2() {

		Gesuchsteller gs2 = gesuch.getGesuchsteller2();
		if (gs2 != null) {
			return Optional.of(gs2);
		}
		return Optional.empty();
	}

	@Override
	public List<BerechnungsblattPrint> getBerechnungsblatt() {

		return null;
	}
}
