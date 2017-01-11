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

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Transferobjekt
 */
public class BegleitschreibenPrintImpl extends BriefPrintImpl implements BegleitschreibenPrint {

	private List<AufzaehlungPrint> beilagen = new ArrayList<>();

	/**
	 * @param gesuch
	 */
	public BegleitschreibenPrintImpl(Gesuch gesuch) {

		super(gesuch);

		Set<Betreuung> betreuungen = new TreeSet<>();

		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			betreuungen.addAll(kindContainer.getBetreuungen());
		}

		beilagen.addAll(betreuungen.stream()
			.filter(betreuung -> betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()
				&& !betreuung.getBetreuungsstatus().equals(Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG))
			.map(betreuung -> new AufzaehlungPrintImpl(ServerMessageUtil.getMessage("BegleitschreibenPrintImpl_VERFÜGUNG") + betreuung.getBGNummer()))
			.collect(Collectors.toList()));

		beilagen.addAll(betreuungen.stream()
			.filter(betreuung -> !betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()
				&& !betreuung.getBetreuungsstatus().equals(Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG))
			.map(betreuung -> new AufzaehlungPrintImpl(ServerMessageUtil.getMessage("BegleitschreibenPrintImpl_MITTEILUNG") + betreuung.getBGNummer()))
			.collect(Collectors.toList()));

	}

	@Override
	public List<AufzaehlungPrint> getBeilagen() {
		return beilagen;
	}

	@Override
	public boolean isHasFSDokument() {
		return gesuch.isHasFSDokument();
	}

	@Override
	public boolean isHasBeilagen() {
		return isHasFSDokument() || !beilagen.isEmpty();
	}
}
