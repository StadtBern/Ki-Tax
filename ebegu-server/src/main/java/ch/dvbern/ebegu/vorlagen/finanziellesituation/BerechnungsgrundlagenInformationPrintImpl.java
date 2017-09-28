package ch.dvbern.ebegu.vorlagen.finanziellesituation;
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

import java.util.ArrayList;
import java.util.List;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;
import ch.dvbern.ebegu.vorlagen.PrintUtil;

import javax.annotation.Nullable;

/**
 * Implementiert den {@link BerechnungsgrundlagenInformationPrint}. Diese Klasse enthält die Daten fuer die
 * Berechnungsvorlage fest.
 */
public class BerechnungsgrundlagenInformationPrintImpl extends BriefPrintImpl implements BerechnungsgrundlagenInformationPrint {

	private final FinanzielleSituationPrint finanz;
	private EinkommensverschlechterungPrint ev1;
	private EinkommensverschlechterungPrint ev2;
	private final Gesuch gesuch;
	private final Verfuegung famGroessenVerfuegung;

	/**
	 * Konstruktor
	 */
	public BerechnungsgrundlagenInformationPrintImpl(Gesuch gesuch, Verfuegung famGroessenVerfuegung) {

		super(gesuch);

		this.gesuch = gesuch;
		this.famGroessenVerfuegung = famGroessenVerfuegung;
		// Finanzdaten abfuellen
		FinanzSituationPrintGesuchsteller fG1 = FinanzSituationPrintGesuchstellerHelper.getFinanzSituationGesuchsteller1(gesuch);
		FinanzSituationPrintGesuchsteller fG2 = FinanzSituationPrintGesuchstellerHelper.getFinanzSituationGesuchsteller2(gesuch);

		// FinanzielleSituation G1 und G2
		String finanzielleSituationJahr = Integer.toString(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear() - 1);
		String fallNummer = PrintUtil.createFallNummerString(gesuch);

		finanz = new FinanzielleSituationPrintImpl(fG1, fG2, finanzielleSituationJahr, fallNummer);

		if (fG1.getEinkommensverschlechterungInfo() != null && fG1.getEinkommensverschlechterungInfo().getEinkommensverschlechterung()) {
			// Einkommensverschleschtereung Jahr 1
			if (fG1.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus1() && !fG1.getEinkommensverschlechterungInfo().getEkvBasisJahrPlus1Annulliert()) {
				String einkommensverschlechterungJahr1 = Integer.toString(gesuch.getGesuchsperiode().getBasisJahrPlus1());
				String ereigniseintritt1 = "";
				if (fG1.getEinkommensverschlechterungInfo().getStichtagFuerBasisJahrPlus1() != null) {
					ereigniseintritt1 = Constants.DATE_FORMATTER.format(fG1.getEinkommensverschlechterungInfo().getStichtagFuerBasisJahrPlus1());
				}
				String grundEv1 = fG1.getEinkommensverschlechterungInfo().getGrundFuerBasisJahrPlus1();
				ev1 = new EinkommensverschlechterungPrintImpl(fG1, fG2, einkommensverschlechterungJahr1, ereigniseintritt1, grundEv1, 1);
			}

			// Einkommensverschleschtereung Jahr 2
			if (fG1.getEinkommensverschlechterungInfo().getEkvFuerBasisJahrPlus2() && !fG1.getEinkommensverschlechterungInfo().getEkvBasisJahrPlus2Annulliert()) {
				String einkommensverschlechterungJahr2 = Integer.toString(gesuch.getGesuchsperiode().getBasisJahrPlus2());
				String ereigniseintritt2 = "";
				if (fG1.getEinkommensverschlechterungInfo().getStichtagFuerBasisJahrPlus2() != null) {
					if (fG1.getEinkommensverschlechterungInfo().getStichtagFuerBasisJahrPlus2().getYear() < gesuch.getGesuchsperiode().getBasisJahrPlus2()) {
						// Das Ereignisdatum ist in diesem Fall 01.12.VORJAHR, dies wollen wir nicht so drucken
						ereigniseintritt2 = ServerMessageUtil.getMessage("Einkommensverschlechterung_VORJAHR");
					} else {
						ereigniseintritt2 = Constants.DATE_FORMATTER.format(fG1.getEinkommensverschlechterungInfo().getStichtagFuerBasisJahrPlus2());
					}
				}
				String grundEv2 = fG1.getEinkommensverschlechterungInfo().getGrundFuerBasisJahrPlus2();
				ev2 = new EinkommensverschlechterungPrintImpl(fG1, fG2, einkommensverschlechterungJahr2, ereigniseintritt2, grundEv2, 2);
			}
		}
	}


	@Nullable
	@Override
	public String getGesuchsteller1Name() {

		return gesuch.getGesuchsteller1() != null ? gesuch.getGesuchsteller1().extractFullName() : null;
	}

	@Override
	public String getGesuchsteller2Name() {

		//noinspection ConstantConditions
		return isExistGesuchsteller2() ? gesuch.getGesuchsteller2().extractFullName() : null;
	}

	@Override
	public boolean isExistGesuchsteller2() {

		return gesuch.getGesuchsteller2() != null;
	}

	@Override
	public FinanzielleSituationPrint getFinanz() {

		return finanz;
	}

	@Override
	public boolean isExistEv1() {

		return getEv1() != null;
	}

	@Override
	public boolean isExistEv2() {

		return getEv2() != null;
	}

	@Override
	public EinkommensverschlechterungPrint getEv1() {

		return ev1;
	}

	@Override
	public EinkommensverschlechterungPrint getEv2() {

		return ev2;
	}

	@Override
	public List<BerechnungsblattPrint> getBerechnungsblatt() {

		List<BerechnungsblattPrint> result = new ArrayList<>();

		if (famGroessenVerfuegung != null) {
			List<VerfuegungZeitabschnitt> zeitabschnitten = famGroessenVerfuegung.getZeitabschnitte();
			for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitten) {
				result.add(new BerechnungsblattPrintImpl(zeitabschnitt));
			}
		}


		return result;

	}

	@Override
	public boolean isPrintBerechnungsBlaetter() {

		return !getBerechnungsblatt().isEmpty();
	}
}
