package ch.dvbern.ebegu.rules.anlageverzeichnis;

import ch.dvbern.ebegu.entities.AbstractFinanzielleSituation;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Gemeinsame Basisklasse zum berechnen der benötigten Dokumente für die Finanzielle Situation und die Einkommensverschlechterung
 */
abstract class AbstractFinanzielleSituationDokumente extends AbstractDokumente<AbstractFinanzielleSituation, Object> {


	void getAllDokumenteGesuchsteller(Set<DokumentGrund> anlageVerzeichnis, String fullname, int basisJahr,
									  boolean gemeinsam, int gesuchstellerNumber, AbstractFinanzielleSituation abstractFinanzielleSituation, DokumentGrundTyp dokumentGrundTyp) {

		final String basisJahrString = String.valueOf(basisJahr);

		if (gemeinsam) {
			if (gesuchstellerNumber == 1) {
				add(getDokument(DokumentTyp.STEUERVERANLAGUNG, abstractFinanzielleSituation, null, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
				add(getDokument(DokumentTyp.STEUERERKLAERUNG, abstractFinanzielleSituation, null, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
			}
		} else {
			add(getDokument(DokumentTyp.STEUERVERANLAGUNG, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
			add(getDokument(DokumentTyp.STEUERERKLAERUNG, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		}

		add(getDokument(DokumentTyp.NACHWEIS_FAMILIENZULAGEN, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.NACHWEIS_ERSATZEINKOMMEN, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.NACHWEIS_ERHALTENE_ALIMENTE, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.NACHWEIS_GELEISTETE_ALIMENTE, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.NACHWEIS_VERMOEGEN, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.NACHWEIS_SCHULDEN, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.ERFOLGSRECHNUNGEN_JAHR, abstractFinanzielleSituation, fullname, basisJahrString, dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.ERFOLGSRECHNUNGEN_JAHR_MINUS1, abstractFinanzielleSituation, fullname, String.valueOf(basisJahr -1), dokumentGrundTyp), anlageVerzeichnis);
		add(getDokument(DokumentTyp.ERFOLGSRECHNUNGEN_JAHR_MINUS2, abstractFinanzielleSituation, fullname, String.valueOf(basisJahr -2), dokumentGrundTyp), anlageVerzeichnis);
	}


	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, AbstractFinanzielleSituation abstractFinanzielleSituation) {
		if (abstractFinanzielleSituation != null) {
			switch (dokumentTyp) {
				case STEUERVERANLAGUNG:
					return abstractFinanzielleSituation.getSteuerveranlagungErhalten();
				case STEUERERKLAERUNG:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() && abstractFinanzielleSituation.getSteuererklaerungAusgefuellt();
				case JAHRESLOHNAUSWEISE:
					return isJahresLohnausweisNeeded(abstractFinanzielleSituation);
				case NACHWEIS_EINKOMMENSSITUATION_MONAT:
					return isMonatsLohnausweisNeeded(abstractFinanzielleSituation);
				case NACHWEIS_FAMILIENZULAGEN:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() &&
						abstractFinanzielleSituation.getFamilienzulage() != null &&
						abstractFinanzielleSituation.getFamilienzulage().compareTo(BigDecimal.ZERO) > 0;
				case NACHWEIS_ERSATZEINKOMMEN:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() &&
						abstractFinanzielleSituation.getErsatzeinkommen() != null &&
						abstractFinanzielleSituation.getErsatzeinkommen().compareTo(BigDecimal.ZERO) > 0;
				case NACHWEIS_ERHALTENE_ALIMENTE:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() &&
						abstractFinanzielleSituation.getErhalteneAlimente() != null &&
						abstractFinanzielleSituation.getErhalteneAlimente().compareTo(BigDecimal.ZERO) > 0;
				case NACHWEIS_GELEISTETE_ALIMENTE:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() &&
						abstractFinanzielleSituation.getGeleisteteAlimente() != null &&
						abstractFinanzielleSituation.getGeleisteteAlimente().compareTo(BigDecimal.ZERO) > 0;
				case NACHWEIS_VERMOEGEN:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() &&
						!abstractFinanzielleSituation.getSteuererklaerungAusgefuellt() &&
						abstractFinanzielleSituation.getBruttovermoegen() != null &&
						abstractFinanzielleSituation.getBruttovermoegen().compareTo(BigDecimal.ZERO) > 0;
				case NACHWEIS_SCHULDEN:
					return !abstractFinanzielleSituation.getSteuerveranlagungErhalten() &&
						!abstractFinanzielleSituation.getSteuererklaerungAusgefuellt() &&
						abstractFinanzielleSituation.getSchulden() != null &&
						abstractFinanzielleSituation.getSchulden().compareTo(BigDecimal.ZERO) > 0;
				case ERFOLGSRECHNUNGEN_JAHR:
					return isErfolgsrechnungNeeded(abstractFinanzielleSituation, 0);
				case ERFOLGSRECHNUNGEN_JAHR_MINUS1:
					return isErfolgsrechnungNeeded(abstractFinanzielleSituation, 1);
				case ERFOLGSRECHNUNGEN_JAHR_MINUS2:
					return isErfolgsrechnungNeeded(abstractFinanzielleSituation, 2);
				default:
					return false;
			}
		}
		return false;
	}

	protected boolean isJahresLohnausweisNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation) {
		return false;
	}

	protected abstract boolean isErfolgsrechnungNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation, int minus);

	protected boolean isMonatsLohnausweisNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation) {
		return false;
	}


}
