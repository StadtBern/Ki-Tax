package ch.dvbern.ebegu.vorlagen.begleitschreiben;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 12.08.2016
*/

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;

import javax.annotation.Nonnull;
import java.util.Optional;

import static ch.dvbern.ebegu.vorlagen.PrintUtil.getGesuchstellerAdresse;

/**
 * Transferobjekt
 */
public class BegleitschreibenPrintImpl implements BegleitschreibenPrint {

	private Gesuch gesuch;

	/**
	 * @param gesuch
	 */
	public BegleitschreibenPrintImpl(Gesuch gesuch) {

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

	/**
	 * @return Gesuchsteller-ReferenzNummer
	 */
	@Override
	public long getFallnummer() {

		return gesuch.getFall().getFallNummer();
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

}
