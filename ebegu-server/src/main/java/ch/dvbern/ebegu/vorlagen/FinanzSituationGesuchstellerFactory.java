package ch.dvbern.ebegu.vorlagen;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 23.08.2016
*/

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;

public class FinanzSituationGesuchstellerFactory {

	private static FinanzSituationGesuchstellerFactory instance;

	/**
	 * Konstruktor
	 */
	private FinanzSituationGesuchstellerFactory() {
		// NOP
	}

	public static FinanzSituationGesuchstellerFactory getInstance() {

		if (instance == null) {
			instance = new FinanzSituationGesuchstellerFactory();
		}

		return instance;
	}

	/**
	 * Erstellt das FinanzSituationGesuchsteller fuer Gesuchsteller 1
	 *
	 * @param gesuch
	 * @return FinanzSituationGesuchsteller
	 */
	public FinanzSituationGesuchsteller getFinanzSituationGesuchsteller1(Gesuch gesuch) {

		Gesuchsteller gesuchsteller1 = gesuch.getGesuchsteller1();
		FinanzSituationGesuchsteller finanzSituationGesuchsteller = new FinanzSituationGesuchsteller(gesuchsteller1.getFinanzielleSituationContainer().getFinanzielleSituationJA(), //
				gesuchsteller1.getEinkommensverschlechterungContainer() != null ? gesuchsteller1.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() : null, //
				gesuchsteller1.getEinkommensverschlechterungContainer() != null ? gesuchsteller1.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() : null, //
				gesuch.getEinkommensverschlechterungInfo());

		return finanzSituationGesuchsteller;
	}

	/**
	 * Erstellt das FinanzSituationGesuchsteller fuer Gesuchsteller 2
	 *
	 * @param gesuch
	 * @return FinanzSituationGesuchsteller
	 */
	public FinanzSituationGesuchsteller getFinanzSituationGesuchsteller2(Gesuch gesuch) {

		Gesuchsteller gesuchsteller2 = gesuch.getGesuchsteller2();
		if (gesuchsteller2 != null) {

			FinanzSituationGesuchsteller finanzSituationGesuchsteller2 = new FinanzSituationGesuchsteller(
					gesuchsteller2.getFinanzielleSituationContainer().getFinanzielleSituationJA(), //
					gesuchsteller2.getEinkommensverschlechterungContainer() != null ? gesuchsteller2.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() : null, //
					gesuchsteller2.getEinkommensverschlechterungContainer() != null ? gesuchsteller2.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() : null, //
					gesuch.getEinkommensverschlechterungInfo());
			return finanzSituationGesuchsteller2;
		}
		return null;
	}
}
