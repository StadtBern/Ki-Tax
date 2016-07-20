package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.util.MathUtil;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

/**
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/1
 */
public class Testfall01_WaeltiDagmar extends AbstractTestfall {

	public Testfall01_WaeltiDagmar(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		super(gesuchsperiode, institutionStammdatenList);
	}

	public Gesuch createGesuch() {
		// Gesuch, Gesuchsteller
		Gesuch gesuch = createAlleinerziehend(1, LocalDate.of(2016, Month.JULY, 13));
		Gesuchsteller gesuchsteller1 = createGesuchsteller("Wälti", "Dagmar");
		gesuch.setGesuchsteller1(gesuchsteller1);
		// Erwerbspensum
		ErwerbspensumContainer erwerbspensum = createErwerbspensum(60, 20);
		gesuchsteller1.addErwerbspensumContainer(erwerbspensum);
		// Kinder
		KindContainer kind = createKind(Geschlecht.MAENNLICH, "Wälti", "Simon", LocalDate.of(2014, Month.APRIL, 13), true);
		kind.setGesuch(gesuch);
		gesuch.getKindContainers().add(kind);
		// Betreuungen
		Betreuung betreuung = createBetreuung(BetreuungsangebotTyp.KITA);
		betreuung.setKind(kind);
		kind.getBetreuungen().add(betreuung);
		// Betreuungspensen
		BetreuungspensumContainer betreuungspensum = createBetreuungspensum(80);
		betreuungspensum.setBetreuung(betreuung);
		betreuung.getBetreuungspensumContainers().add(betreuungspensum);
		// Finanzielle Situation
		FinanzielleSituationContainer finanzielleSituationContainer = createFinanzielleSituationContainer();
		finanzielleSituationContainer.getFinanzielleSituationSV().setNettolohn(MathUtil.DEFAULT.from(53265));
		finanzielleSituationContainer.getFinanzielleSituationSV().setBruttovermoegen(MathUtil.DEFAULT.from(12147));
		finanzielleSituationContainer.setGesuchsteller(gesuchsteller1);
		gesuchsteller1.setFinanzielleSituationContainer(finanzielleSituationContainer);

		return gesuch;
	}
}
