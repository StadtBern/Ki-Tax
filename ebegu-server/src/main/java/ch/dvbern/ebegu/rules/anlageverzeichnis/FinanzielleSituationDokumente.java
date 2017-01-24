package ch.dvbern.ebegu.rules.anlageverzeichnis;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Dokumente für FinanzielleSituation:
 * <p>
 * Steuerveranlagung
 * Notwendig, wenn Steuerveranlagung vorhanden. Ist dies der Fall, müssen alle weiteren Belege unter „Finanzielle Situation“ nicht eingereicht werden
 * Bei Verheirateten wird das Dokument unter "Allgemeine Dokumente" angezeigt, bei allen anderen beim jeweiligen Gesuchsteller
 * <p>
 * Steuererklärung
 * Notwendig, wenn keine Steuerveranlagung, jedoch Steuererklärung vorhanden
 * Bei Verheirateten wird das Dokument unter "Allgemeine Dokumente" angezeigt, bei allen anderen beim jeweiligen Gesuchsteller
 * <p>
 * Jahreslohnausweise
 * Notwendig, wenn keine Veranlagung vorhanden und Nettolohn > 0
 * <p>
 * Nachweis über Familienzulagen (soweit nicht im Nettolohn enthalten)
 * Notwendig, wenn keine Veranlagung vorhanden und Familienzulage > 0
 * <p>
 * Nachweis über Ersatzeinkommen
 * Notwendig wenn keine Veranlagung vorhanden und Ersatzeinkommen > 0
 * <p>
 * Nachweis über erhaltene Alimente (Unterhaltsbeiträge)
 * Notwendig, wenn keine Veranlagung vorhanden und erhaltene Alimente > 0
 * <p>
 * Nachweis über geleistete Alimente
 * Notwendig, wenn keine Veranlagung vorhanden und geleistete Alimente > 0
 * <p>
 * Nachweis über das Vermögen, Stand 31.12., (z.B.: Kto.-Auszug, Immobilien, Zinsbestätigung usw.)
 * Notwendig, wenn weder Steuerveranlagung noch Steuerklärung vorhanden und Vermögen> 0
 * <p>
 * Nachweis über die Schulden, Stand: 31.12., (z.B.: Kto.-Auszug, Darlehensvertrag usw.)
 * Notwendig, wenn weder Steuerveranlagung noch Steuerklärung vorhanden und Schulden > 0
 * <p>
 * Erfolgsrechnungen der letzten drei Jahre (Basisjahr, Basisjahr-1, Basisjahr-2)
 * Notwendig, wenn keine Steuerveranlagung vorhanden und Summe der Erfolgsrechnungen > 0
 * <p>
 **/
public class FinanzielleSituationDokumente extends AbstractFinanzielleSituationDokumente {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {

		final boolean gemeinsam = gesuch.extractFamiliensituation() != null &&
			gesuch.extractFamiliensituation().getGemeinsameSteuererklaerung() != null &&
			gesuch.extractFamiliensituation().getGemeinsameSteuererklaerung();

		final int basisJahr = gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear().getYear();

		final GesuchstellerContainer gesuchsteller1 = gesuch.getGesuchsteller1();
		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1, gemeinsam, 1, basisJahr);

		final GesuchstellerContainer gesuchsteller2 = gesuch.getGesuchsteller2();
		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2, gemeinsam, 2, basisJahr);
	}

	private void getAllDokumenteGesuchsteller(Set<DokumentGrund> anlageVerzeichnis, GesuchstellerContainer gesuchsteller,
											  boolean gemeinsam, int gesuchstellerNumber, int basisJahr) {

		if (gesuchsteller == null || gesuchsteller.getFinanzielleSituationContainer() == null) {
			return;
		}

		final FinanzielleSituationContainer finanzielleSituationContainer = gesuchsteller.getFinanzielleSituationContainer();

		final FinanzielleSituation finanzielleSituationJA = finanzielleSituationContainer.getFinanzielleSituationJA();

		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller.extractFullName(), basisJahr, gemeinsam, gesuchstellerNumber, finanzielleSituationJA, DokumentGrundTyp.FINANZIELLESITUATION);

		add(getDokument(DokumentTyp.JAHRESLOHNAUSWEISE, finanzielleSituationJA, gesuchsteller.extractFullName(), String.valueOf(basisJahr), DokumentGrundTyp.FINANZIELLESITUATION), anlageVerzeichnis);

	}

	@Override
	protected boolean isJahresLohnausweisNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation) {
		if (abstractFinanzielleSituation instanceof FinanzielleSituation) {
			FinanzielleSituation finanzielleSituation = (FinanzielleSituation) abstractFinanzielleSituation;

			return !finanzielleSituation.getSteuerveranlagungErhalten() &&
				finanzielleSituation.getNettolohn() != null &&
				finanzielleSituation.getNettolohn().compareTo(BigDecimal.ZERO) > 0;
		}
		return false;
	}

	@Override
	protected boolean isErfolgsrechnungNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation, int minus) {
		if (abstractFinanzielleSituation instanceof FinanzielleSituation) {
			FinanzielleSituation finanzielleSituation = (FinanzielleSituation) abstractFinanzielleSituation;
			switch (minus) {
				case 0:
					return !finanzielleSituation.getSteuerveranlagungErhalten() && (finanzielleSituation.getGeschaeftsgewinnBasisjahr() != null);
				case 1:
					return !finanzielleSituation.getSteuerveranlagungErhalten() && (finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1() != null);
				case 2:
					return !finanzielleSituation.getSteuerveranlagungErhalten() && (finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2() != null);
			}
		}
		return false;
	}

}
