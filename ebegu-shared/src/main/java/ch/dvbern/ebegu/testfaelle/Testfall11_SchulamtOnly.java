/*
 * Copyright © 2018 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.testfaelle;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.enums.Kinderabzug;

/**
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/11
 * https://ebegu.dvbern.ch/ebegu/api/v1/testfaelle/testfall/11
 */
public class Testfall11_SchulamtOnly extends AbstractTestfall {

	private static final String FAMILIENNAME = "Schmid";

	public Testfall11_SchulamtOnly(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList, boolean betreuungenBestaetigt) {
		super(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt);
	}

	public Testfall11_SchulamtOnly(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList, false);
	}

	@Override
	public Gesuch fillInGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend();
		GesuchstellerContainer gesuchsteller1 = createGesuchstellerContainer(FAMILIENNAME, "Pirmin");
		gesuch.setGesuchsteller1(gesuchsteller1);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensumGS1 = createErwerbspensum(60, 0);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensumGS1);
		// Kinder
		KindContainer kind1 = createKind(Geschlecht.MAENNLICH, FAMILIENNAME, "Luan", LocalDate.of(2006, Month
			.DECEMBER, 25), Kinderabzug.HALBER_ABZUG, true);
		kind1.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind1);
		KindContainer kind2 = createKind(Geschlecht.MAENNLICH, FAMILIENNAME, "Laurin", LocalDate.of(2011, Month.MARCH,
			29), Kinderabzug.HALBER_ABZUG, true);
		kind2.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind2);

		// Betreuungen
		// Kind 1: Tagesschule Bern
		Betreuung betreuungTagesschuleBern = createBetreuung(BetreuungsangebotTyp.TAGESSCHULE, ID_INSTITUTION_BERN,
			betreuungenBestaetigt);
		betreuungTagesschuleBern.setKind(kind1);
		kind1.getBetreuungen().add(betreuungTagesschuleBern);
		BetreuungspensumContainer betreuungspensumTagiAaregg = createBetreuungspensum(100, LocalDate.of(gesuchsperiode.getBasisJahrPlus1(), Month.AUGUST, 1), LocalDate.of(gesuchsperiode.getBasisJahrPlus2(), Month.JULY, 31));
		betreuungspensumTagiAaregg.setBetreuung(betreuungTagesschuleBern);
		betreuungTagesschuleBern.getBetreuungspensumContainers().add(betreuungspensumTagiAaregg);
		// Kind 2: Ferieninsel Guarda
		Betreuung betreuungFerieninselGuarda = createBetreuung(BetreuungsangebotTyp.FERIENINSEL, ID_INSTITUTION_GUARDA,
			betreuungenBestaetigt);
		betreuungFerieninselGuarda.setKind(kind2);
		kind2.getBetreuungen().add(betreuungFerieninselGuarda);
		BetreuungspensumContainer betreuungspensumKitaAaregg = createBetreuungspensum(100, LocalDate.of(gesuchsperiode.getBasisJahrPlus1(), Month.AUGUST, 1), LocalDate.of(gesuchsperiode.getBasisJahrPlus2(), Month.JULY, 31));
		betreuungspensumKitaAaregg.setBetreuung(betreuungFerieninselGuarda);
		betreuungFerieninselGuarda.getBetreuungspensumContainers().add(betreuungspensumKitaAaregg);

		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationGS1 = createFinanzielleSituationContainer();
		finanzielleSituationGS1.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationGS1);

		createEmptyEKVInfoContainer(gesuch);

		return gesuch;
	}

	@Override
	public String getNachname() {
		return FAMILIENNAME;
	}

	@Override
	public String getVorname() {
		return "Pirmin";
	}
}
