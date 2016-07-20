package ch.dvbern.ebegu.testfaelle;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Superklasse für Testfaelle des JA
 */
public class AbstractTestfall {

	protected Gesuchsperiode gesuchsperiode;
	protected Collection<InstitutionStammdaten> institutionStammdatenList;

	public AbstractTestfall(Gesuchsperiode gesuchsperiode, Collection<InstitutionStammdaten> institutionStammdatenList) {
		this.gesuchsperiode = gesuchsperiode;
		this.institutionStammdatenList = institutionStammdatenList;
	}

	protected Gesuch createAlleinerziehend(int fallNummer, LocalDate eingangsdatum) {
		// Fall
		Fall fall = new Fall();
		fall.setFallNummer(fallNummer);
		// Gesuch
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(gesuchsperiode);
		gesuch.setFall(fall);
		gesuch.setEingangsdatum(eingangsdatum);
		// Familiensituation
		Familiensituation familiensituation = new Familiensituation();
		familiensituation.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		familiensituation.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.setFamiliensituation(familiensituation);
		return gesuch;
	}

	protected Gesuchsteller createGesuchsteller(String name, String vorname) {
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setGeschlecht(Geschlecht.WEIBLICH);
		gesuchsteller.setNachname(name);
		gesuchsteller.setVorname(vorname);
		gesuchsteller.setGeburtsdatum(LocalDate.of(1980, Month.MARCH, 25));
		gesuchsteller.setAdressen(new ArrayList<>());
		gesuchsteller.getAdressen().add(createWohnadresse(gesuchsteller));
		gesuchsteller.setDiplomatenstatus(false);
		gesuchsteller.setMail("test@email.com");
		gesuchsteller.setMobile("079 000 00 00");
		return gesuchsteller;
	}

	protected GesuchstellerAdresse createWohnadresse(Gesuchsteller gesuchsteller) {
		GesuchstellerAdresse wohnadresse = new GesuchstellerAdresse();
		wohnadresse.setStrasse("Testweg");
		wohnadresse.setHausnummer("10");
		wohnadresse.setPlz("3000");
		wohnadresse.setOrt("Bern");
		wohnadresse.setLand(Land.CH);
		wohnadresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);
		wohnadresse.setGesuchsteller(gesuchsteller);
		return wohnadresse;
	}

	protected ErwerbspensumContainer createErwerbspensum(int prozent, int zuschlagsprozent) {
		ErwerbspensumContainer erwerbspensumContainer = new ErwerbspensumContainer();
		Erwerbspensum erwerbspensum = new Erwerbspensum();
		erwerbspensum.setGueltigkeit(gesuchsperiode.getGueltigkeit());
		erwerbspensum.setTaetigkeit(Taetigkeit.ANGESTELLT);
		erwerbspensum.setPensum(prozent);
		if (zuschlagsprozent > 0) {
			erwerbspensum.setZuschlagZuErwerbspensum(true);
			erwerbspensum.setZuschlagsprozent(zuschlagsprozent);
			erwerbspensum.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		}
		erwerbspensumContainer.setErwerbspensumJA(erwerbspensum);
		return erwerbspensumContainer;
	}

	protected KindContainer createKind(Geschlecht geschlecht, String name, String vorname, LocalDate geburtsdatum, boolean betreuung) {
		Kind kind = new Kind();
		kind.setGeschlecht(geschlecht);
		kind.setNachname(name);
		kind.setVorname(vorname);
		kind.setGeburtsdatum(geburtsdatum);
		kind.setKinderabzug(Kinderabzug.GANZER_ABZUG);
		kind.setFamilienErgaenzendeBetreuung(betreuung);
		if (betreuung) {
			kind.setMutterspracheDeutsch(Boolean.TRUE);
		}
		KindContainer kindContainer = new KindContainer();
		kindContainer.setKindJA(kind);
		return kindContainer;
	}

	protected Betreuung createBetreuung(BetreuungsangebotTyp betreuungsangebotTyp) {
		Betreuung betreuung = new Betreuung();
		betreuung.setInstitutionStammdaten(createInstitutionStammdaten(betreuungsangebotTyp));
		betreuung.setBetreuungsstatus(Betreuungsstatus.AUSSTEHEND);
		return betreuung;
	}

	protected InstitutionStammdaten createInstitutionStammdaten(BetreuungsangebotTyp betreuungsangebotTyp) {
		for (InstitutionStammdaten institutionStammdaten : institutionStammdatenList) {
			if (institutionStammdaten.getBetreuungsangebotTyp().equals(betreuungsangebotTyp)) {
				return institutionStammdaten;
			}
		}
		return null;
	}

	protected BetreuungspensumContainer createBetreuungspensum(int pensum) {
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		Betreuungspensum betreuungspensum = new Betreuungspensum();
		betreuungspensumContainer.setBetreuungspensumJA(betreuungspensum);
		betreuungspensum.setGueltigkeit(gesuchsperiode.getGueltigkeit());
		betreuungspensum.setPensum(pensum);
		return betreuungspensumContainer;
	}

	protected FinanzielleSituationContainer createFinanzielleSituationContainer() {
		FinanzielleSituationContainer finanzielleSituationContainer = new FinanzielleSituationContainer();
		FinanzielleSituation finanzielleSituation = new FinanzielleSituation();
		finanzielleSituation.setSteuerveranlagungErhalten(true);
		finanzielleSituation.setSteuererklaerungAusgefuellt(true);
		finanzielleSituationContainer.setJahr(gesuchsperiode.getGueltigkeit().getGueltigAb().getYear()-1);
		finanzielleSituationContainer.setFinanzielleSituationSV(finanzielleSituation);
		return finanzielleSituationContainer;
	}
}
