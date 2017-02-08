package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utils fuer das Kopieren der Daten von den JA-Containern in die GS-Container bei der Freigabe des Gesuchs.
 */
@SuppressWarnings("PMD.CollapsibleIfStatements")
public class FreigabeCopyUtil {

	/**
	 * kopiert das Gesuch fuer die Freigabe
	 */
	public static void copyForFreigabe(@Nonnull Gesuch gesuch) {
		// Familiensituation
		copyFamiliensituationContainer(gesuch.getFamiliensituationContainer());
		// Kinder
		if (gesuch.getKindContainers() != null) {
			for (KindContainer kindContainer : gesuch.getKindContainers()) {
				// Kind
				copyKindContainer(kindContainer);
			}
		}
		// EinkommensverschlechterungsInfo
		copyEinkommensverschlechterungInfoContainer(gesuch.getEinkommensverschlechterungInfoContainer());
		// Gesuchsteller 1
		copyGesuchstellerContainer(gesuch.getGesuchsteller1());
		// Gesuchsteller 2
		copyGesuchstellerContainer(gesuch.getGesuchsteller2());
	}

	private static void copyFamiliensituationContainer(@Nullable FamiliensituationContainer container) {
		if (container != null) {
			if (container.getFamiliensituationJA() != null) {
				if (container.getFamiliensituationGS() == null) {
					container.setFamiliensituationGS(new Familiensituation());  //init
				}
				//noinspection ConstantConditions
				copyFamiliensituation(container.getFamiliensituationGS(), container.getFamiliensituationJA());
			} else {
				container.setFamiliensituationGS(null);
			}
		}
	}

	private static void copyFamiliensituation(@Nonnull Familiensituation familiensituationGS, @Nonnull Familiensituation familiensituationJA) {
		familiensituationGS.setFamilienstatus(familiensituationJA.getFamilienstatus());
		familiensituationGS.setGemeinsameSteuererklaerung(familiensituationJA.getGemeinsameSteuererklaerung());
		familiensituationGS.setGesuchstellerKardinalitaet(familiensituationJA.getGesuchstellerKardinalitaet());
		familiensituationGS.setAenderungPer(familiensituationJA.getAenderungPer());
	}

	private static void copyKindContainer(@Nullable KindContainer container) {
		if (container != null) {
			if (container.getKindJA() != null) {
				if (container.getKindGS() == null) {
					container.setKindGS(new Kind());
				}
				copyKind(container.getKindGS(), container.getKindJA());
			} else {
				container.setKindGS(null);
			}
			// Betreuungen pro Kind
			if (container.getBetreuungen() != null) {
				for (Betreuung betreuung : container.getBetreuungen()) {
					// Betreuung
					if (betreuung.getBetreuungspensumContainers() != null) {
						for (BetreuungspensumContainer betreuungspensumContainer : betreuung.getBetreuungspensumContainers()) {
							copyBetreuungspensumContainer(betreuungspensumContainer);
						}
					}
					// Abwesenheiten pro Betreuung
					if (betreuung.getAbwesenheitContainers() != null) {
						for (AbwesenheitContainer abwesenheitContainer : betreuung.getAbwesenheitContainers()) {
							copyAbwesenheitContainer(abwesenheitContainer);
						}
					}
				}
			}
		}
	}

	private static void copyKind(@Nonnull Kind kindGS, @Nonnull Kind kindJA) {
		kindGS.setVorname(kindJA.getVorname());
		kindGS.setMutterspracheDeutsch(kindJA.getMutterspracheDeutsch());
		if (kindJA.getPensumFachstelle() != null) {
			kindGS.setPensumFachstelle(new PensumFachstelle());
			kindGS.getPensumFachstelle().setFachstelle(kindJA.getPensumFachstelle().getFachstelle());
			kindGS.getPensumFachstelle().setPensum(kindJA.getPensumFachstelle().getPensum());
			kindGS.getPensumFachstelle().setGueltigkeit(kindJA.getPensumFachstelle().getGueltigkeit());
		}
		kindGS.setEinschulung(kindJA.getEinschulung());
		kindGS.setFamilienErgaenzendeBetreuung(kindJA.getFamilienErgaenzendeBetreuung());
		kindGS.setKinderabzug(kindJA.getKinderabzug());
		kindGS.setWohnhaftImGleichenHaushalt(kindJA.getWohnhaftImGleichenHaushalt());
		kindGS.setGeburtsdatum(kindJA.getGeburtsdatum());
		kindGS.setNachname(kindJA.getNachname());
		kindGS.setGeschlecht(kindJA.getGeschlecht());
	}

	private static void copyBetreuungspensumContainer(@Nullable BetreuungspensumContainer container) {
		if (container != null) {
			if (container.getBetreuungspensumJA() != null) {
				if (container.getBetreuungspensumGS() == null) {
					container.setBetreuungspensumGS(new Betreuungspensum());
				}
				copyBetreuungspensum(container.getBetreuungspensumGS(), container.getBetreuungspensumJA());
			} else {
				container.setBetreuungspensumGS(null);
			}
		}
	}

	private static void copyBetreuungspensum(@Nonnull Betreuungspensum betreuungspensumGS, @Nonnull Betreuungspensum betreuungspensumJA) {
		betreuungspensumGS.setGueltigkeit(betreuungspensumJA.getGueltigkeit());
		betreuungspensumGS.setPensum(betreuungspensumJA.getPensum());
		betreuungspensumGS.setNichtEingetreten(betreuungspensumJA.getNichtEingetreten());
	}

	private static void copyAbwesenheitContainer(@Nullable AbwesenheitContainer container) {
		if (container != null) {
			if (container.getAbwesenheitJA() != null) {
				if (container.getAbwesenheitGS() == null) {
					container.setAbwesenheitGS(new Abwesenheit());
				}
				copyAbwesenheit(container.getAbwesenheitGS(), container.getAbwesenheitJA());
			} else {
				container.setAbwesenheitGS(null);
			}
		}
	}

	private static void copyAbwesenheit(@Nonnull Abwesenheit abwesenheitGS, @Nonnull Abwesenheit abwesenheitJA) {
		abwesenheitGS.setGueltigkeit(new DateRange(abwesenheitJA.getGueltigkeit()));
	}

	private static void copyGesuchstellerContainer(@Nullable GesuchstellerContainer container) {
		if (container != null) {
			// Stammdaten
			if (container.getGesuchstellerJA() != null) {
				if (container.getGesuchstellerGS() == null) {
					container.setGesuchstellerGS(new Gesuchsteller());
				}
				copyGesuchsteller(container.getGesuchstellerGS(), container.getGesuchstellerJA());
			} else {
				container.setGesuchstellerGS(null);
			}
			// Adressen
			for (GesuchstellerAdresseContainer betreuung : container.getAdressen()) {
				copyGesuchstellerAdresseContainer(betreuung);
			}
			// Finanzielle Situation
			copyFinanzielleSituationContainer(container.getFinanzielleSituationContainer());
			// Einkommensverschlechterung
			copyEinkommensverschlechterungContainer(container.getEinkommensverschlechterungContainer());
			// Erwerbspensum
			for (ErwerbspensumContainer erwerbspensumContainer : container.getErwerbspensenContainers()) {
				copyErwerbspensumContainer(erwerbspensumContainer);
			}
		}
	}

	private static void copyGesuchsteller(@Nonnull Gesuchsteller gesuchstellerGS, @Nonnull Gesuchsteller gesuchstellerJA) {
		gesuchstellerGS.setGeschlecht(gesuchstellerJA.getGeschlecht());
		gesuchstellerGS.setVorname(gesuchstellerJA.getVorname());
		gesuchstellerGS.setNachname(gesuchstellerJA.getNachname());
		gesuchstellerGS.setGeburtsdatum(gesuchstellerJA.getGeburtsdatum());
		gesuchstellerGS.setMail(gesuchstellerJA.getMail());
		gesuchstellerGS.setMobile(gesuchstellerJA.getMobile());
		gesuchstellerGS.setTelefon(gesuchstellerJA.getTelefon());
		gesuchstellerGS.setTelefonAusland(gesuchstellerJA.getTelefonAusland());
		gesuchstellerGS.setZpvNumber(gesuchstellerJA.getZpvNumber());
		gesuchstellerGS.setDiplomatenstatus(gesuchstellerJA.isDiplomatenstatus());
	}

	private static void copyGesuchstellerAdresseContainer(@Nullable GesuchstellerAdresseContainer container) {
		if (container != null) {
			if (container.getGesuchstellerAdresseJA() != null) {
				if (container.getGesuchstellerAdresseGS() == null) {
					container.setGesuchstellerAdresseGS(new GesuchstellerAdresse());
				}
				copyGesuchstellerAdresse(container.getGesuchstellerAdresseGS(), container.getGesuchstellerAdresseJA());
			} else {
				container.setGesuchstellerAdresseGS(null);
			}
		}
	}

	private static void copyGesuchstellerAdresse(@Nonnull GesuchstellerAdresse gs, @Nonnull GesuchstellerAdresse ja) {
		gs.setGueltigkeit(new DateRange(ja.getGueltigkeit()));
		gs.setStrasse(ja.getStrasse());
		gs.setHausnummer(ja.getHausnummer());
		gs.setZusatzzeile(ja.getZusatzzeile());
		gs.setPlz(ja.getPlz());
		gs.setOrt(ja.getOrt());
		gs.setLand(ja.getLand());
		gs.setGemeinde(ja.getGemeinde());
		gs.setOrganisation(ja.getOrganisation());
		gs.setAdresseTyp(ja.getAdresseTyp());
		gs.setNichtInGemeinde(ja.isNichtInGemeinde());
	}

	private static void copyAbstractFinanzielleSituation(@Nonnull AbstractFinanzielleSituation gs, @Nonnull AbstractFinanzielleSituation ja) {
		gs.setSteuerveranlagungErhalten(ja.getSteuerveranlagungErhalten());
		gs.setSteuererklaerungAusgefuellt(ja.getSteuererklaerungAusgefuellt());
		gs.setFamilienzulage(ja.getFamilienzulage());
		gs.setErsatzeinkommen(ja.getErsatzeinkommen());
		gs.setErhalteneAlimente(ja.getErhalteneAlimente());
		gs.setBruttovermoegen(ja.getBruttovermoegen());
		gs.setSchulden(ja.getSchulden());
		gs.setGeschaeftsgewinnBasisjahr(ja.getGeschaeftsgewinnBasisjahr());
		gs.setGeleisteteAlimente(ja.getGeleisteteAlimente());
	}

	private static void copyEinkommensverschlechterungInfoContainer(@Nullable EinkommensverschlechterungInfoContainer container) {
		if (container != null) {
			if (container.getEinkommensverschlechterungInfoGS() == null) {
				container.setEinkommensverschlechterungInfoGS(new EinkommensverschlechterungInfo());
			}
			copyEinkommensverschlechterungInfo(container.getEinkommensverschlechterungInfoGS(), container.getEinkommensverschlechterungInfoJA());
		}
	}

	private static void copyEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo gs, @Nonnull EinkommensverschlechterungInfo ja) {
		gs.setEinkommensverschlechterung(ja.getEinkommensverschlechterung());
		gs.setEkvFuerBasisJahrPlus1(ja.getEkvFuerBasisJahrPlus1());
		gs.setEkvFuerBasisJahrPlus2(ja.getEkvFuerBasisJahrPlus2());
		gs.setGemeinsameSteuererklaerung_BjP1(ja.getGemeinsameSteuererklaerung_BjP1());
		gs.setGemeinsameSteuererklaerung_BjP2(ja.getGemeinsameSteuererklaerung_BjP2());
		gs.setGrundFuerBasisJahrPlus1(ja.getGrundFuerBasisJahrPlus1());
		gs.setGrundFuerBasisJahrPlus2(ja.getGrundFuerBasisJahrPlus2());
		gs.setStichtagFuerBasisJahrPlus1(ja.getStichtagFuerBasisJahrPlus1());
		gs.setStichtagFuerBasisJahrPlus2(ja.getStichtagFuerBasisJahrPlus2());
	}

	private static void copyEinkommensverschlechterungContainer(@Nullable EinkommensverschlechterungContainer container) {
		if (container != null) {
			if (container.getEkvJABasisJahrPlus1() != null) {
				if (container.getEkvGSBasisJahrPlus1() == null) {
					container.setEkvGSBasisJahrPlus1(new Einkommensverschlechterung());
				}
				copyEinkommensverschlechterung(container.getEkvGSBasisJahrPlus1(), container.getEkvJABasisJahrPlus1());
			} else {
				container.setEkvGSBasisJahrPlus1(null);
			}
			if (container.getEkvJABasisJahrPlus2() != null) {
				if (container.getEkvGSBasisJahrPlus2() == null) {
					container.setEkvGSBasisJahrPlus2(new Einkommensverschlechterung());
				}
				copyEinkommensverschlechterung(container.getEkvGSBasisJahrPlus2(), container.getEkvJABasisJahrPlus2());
			} else {
				container.setEkvGSBasisJahrPlus2(null);
			}
		}
	}

	private static void copyEinkommensverschlechterung(@Nonnull Einkommensverschlechterung gs, @Nonnull Einkommensverschlechterung ja) {
		copyAbstractFinanzielleSituation(gs, ja);
		gs.setNettolohnJan(ja.getNettolohnJan());
		gs.setNettolohnFeb(ja.getNettolohnFeb());
		gs.setNettolohnMrz(ja.getNettolohnMrz());
		gs.setNettolohnApr(ja.getNettolohnApr());
		gs.setNettolohnMai(ja.getNettolohnMai());
		gs.setNettolohnJun(ja.getNettolohnJun());
		gs.setNettolohnJul(ja.getNettolohnJul());
		gs.setNettolohnAug(ja.getNettolohnAug());
		gs.setNettolohnSep(ja.getNettolohnSep());
		gs.setNettolohnOkt(ja.getNettolohnOkt());
		gs.setNettolohnNov(ja.getNettolohnNov());
		gs.setNettolohnDez(ja.getNettolohnDez());
		gs.setNettolohnZus(ja.getNettolohnZus());
	}

	private static void copyFinanzielleSituationContainer(@Nullable FinanzielleSituationContainer container) {
		if (container != null) {
			if (container.getFinanzielleSituationJA() != null) {
				if (container.getFinanzielleSituationGS() == null) {
					container.setFinanzielleSituationGS(new FinanzielleSituation());
				}
				copyFinanzielleSituation(container.getFinanzielleSituationGS(), container.getFinanzielleSituationJA());
			} else {
				container.setFinanzielleSituationGS(null);
			}
		}
	}

	private static void copyFinanzielleSituation(@Nonnull FinanzielleSituation gs, @Nonnull FinanzielleSituation ja) {
		copyAbstractFinanzielleSituation(gs, ja);
		gs.setNettolohn(ja.getNettolohn());
		gs.setGeschaeftsgewinnBasisjahrMinus1(ja.getGeschaeftsgewinnBasisjahrMinus1());
		gs.setGeschaeftsgewinnBasisjahrMinus2(ja.getGeschaeftsgewinnBasisjahrMinus2());
	}

	private static void copyErwerbspensumContainer(@Nullable ErwerbspensumContainer container) {
		if (container != null) {
			if (container.getErwerbspensumJA() != null) {
				if (container.getErwerbspensumGS() == null) {
					container.setErwerbspensumGS(new Erwerbspensum());
				}
				copyErwerbspensum(container.getErwerbspensumGS(), container.getErwerbspensumJA());
			} else {
				container.setErwerbspensumGS(null);
			}
		}
	}

	private static void copyErwerbspensum(@Nonnull Erwerbspensum erwerbspensumGS, @Nonnull Erwerbspensum erwerbspensumJA) {
		erwerbspensumGS.setGueltigkeit(new DateRange(erwerbspensumJA.getGueltigkeit()));
		erwerbspensumGS.setPensum(erwerbspensumJA.getPensum());
		erwerbspensumGS.setTaetigkeit(erwerbspensumJA.getTaetigkeit());
		erwerbspensumGS.setZuschlagZuErwerbspensum(erwerbspensumJA.getZuschlagZuErwerbspensum());
		erwerbspensumGS.setZuschlagsgrund(erwerbspensumJA.getZuschlagsgrund());
		erwerbspensumGS.setZuschlagsprozent(erwerbspensumJA.getZuschlagsprozent());
		erwerbspensumGS.setBezeichnung(erwerbspensumJA.getBezeichnung());
	}
}
