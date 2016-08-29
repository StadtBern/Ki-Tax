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

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;

/**
 * Transferobjekt
 */
public class BegleitschreibenPrintImpl implements BegleitschreibenPrint {

	private Gesuch gesuch;

	/**
	 * @param betreuung
	 */
	public BegleitschreibenPrintImpl(Gesuch gesuch) {

		this.gesuch = gesuch;
	}

	/**
	 * @return GesuchstellerName
	 */
	@Override
	public String getGesuchstellerName() {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1();
		if (gesuchsteller.isPresent()) {
			return gesuchsteller.get().getFullName();
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */
	@Override
	public String getGesuchstellerStrasse() {

		Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse();
		if (gesuchstellerAdresse.isPresent()) {
			return gesuchstellerAdresse.get().getStrasse();
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */
	@Override
	public String getGesuchstellerPLZStadt() {

		Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse();
		if (gesuchstellerAdresse.isPresent()) {
			return gesuchstellerAdresse.get().getPlz() + " " + gesuchstellerAdresse.get().getOrt();
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-ReferenzNummer
	 */
	@Override
	public int getFallnummer() {

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

	@Nonnull
	private Optional<GesuchstellerAdresse> getGesuchstellerAdresse() {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1();
		if (gesuchsteller.isPresent()) {
			List<GesuchstellerAdresse> adressen = gesuchsteller.get().getAdressen();
			GesuchstellerAdresse wohnadresse = null;
			for (GesuchstellerAdresse gesuchstellerAdresse : adressen) {
				if (gesuchstellerAdresse.getAdresseTyp().equals(AdresseTyp.KORRESPONDENZADRESSE)) {
					return Optional.of(gesuchstellerAdresse);
				}
				wohnadresse = gesuchstellerAdresse;
			}
			if (wohnadresse != null) {
				return Optional.of(wohnadresse);
			}
		}
		return Optional.empty();
	}
}
