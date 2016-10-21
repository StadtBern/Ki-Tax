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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import ch.dvbern.ebegu.vorlagen.finanziellesituation.FinanzielleSituationEinkommensverschlechterungPrintMergeSource;

/**
 * @deprecated stattdessen wird die {@link FinanzielleSituationEinkommensverschlechterungPrintMergeSource} benutzt
 */
@Deprecated
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

		return PrintUtil.getGesuchstellerName(gesuch);
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */
	@Override
	public String getGesuchstellerStrasse() {

		return PrintUtil.getGesuchstellerStrasse(gesuch);
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */
	@Override
	public String getGesuchstellerPLZStadt() {

		return PrintUtil.getGesuchstellerPLZStadt(gesuch);
	}

	@Override
	public List<BerechnungsblattPrint> getBerechnungsblatt() {

		List<BerechnungsblattPrint> result = new ArrayList<>();
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				Optional<Verfuegung> verfuegung = extractVerfuegung(betreuung);
				if (verfuegung.isPresent()) {
					List<VerfuegungZeitabschnitt> zeitabschnitten = verfuegung.get().getZeitabschnitte();
					for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitten) {
						result.add(new BerechnungsblattPrintImpl(zeitabschnitt));
					}
				}
				// Von jedem Kind nur eine Betreuung nehmmen
				break;
			}
			break;
		}

		return result;

	}

	@Nonnull
	private Optional<Verfuegung> extractVerfuegung(Betreuung betreuung) {

		Verfuegung verfuegung = betreuung.getVerfuegung();
		if (verfuegung != null) {
			return Optional.of(verfuegung);
		}
		return Optional.empty();
	}
}
