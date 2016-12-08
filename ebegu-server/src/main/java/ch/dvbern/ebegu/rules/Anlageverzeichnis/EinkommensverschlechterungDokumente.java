package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Dokumente für Einkommensverschlechterung:
 * <p>
 * Unterlagen zur aktuellen Einkommenssituation (pro Monat)
 * Notwendig, wenn Monatseinkünfte > 0
 * <p>
 * Nachweis über Familienzulagen (soweit nicht im Nettolohn enthalten)
 * Notwendig, wenn Familienzulage > 0
 * <p>
 * Nachweis über Ersatzeinkommen
 * Notwendig, wenn Ersatzeinkommen > 0
 * <p>
 * Nachweis über erhaltene Alimente (Unterhaltsbeiträge)
 * Notwendig, wenn erhaltene Alimente > 0
 * <p>
 * Nachweis über geleistete Alimente
 * Notwendig, wenn geleistete Alimente > 0
 * <p>
 * Nachweis über das Vermögen, Stand 31.12. (z.B.: Kto.-Auszug, Immobilien usw.)
 * Notwendig, wenn Vermögen > 0
 * <p>
 * Nachweis über die Schulden, Stand: 31.12. (z.B.: Kto.-Auszug, Darlehensvertrag usw.)
 * Notwendig, wenn Schulden > 0
 * <p>
 * Erfolgsrechnungen der letzten drei Jahre (Jahr der Einkommensverschlechterung: x, x-1, x-2)
 * Notwendig, wenn Summe der Erfolgsrechnungen > 0
 * <p>
 * Unterstützungsnachweis / Bestätigung Sozialdienst (Ersatzeinkommen)
 * Notwendig, wenn Nettolohn > 0 => nicht mehr nötig!
 **/
public class EinkommensverschlechterungDokumente extends AbstractFinanzielleSituationDokumente {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {

		final boolean gemeinsam = gesuch.getFamiliensituation() != null &&
			gesuch.getFamiliensituation().getGemeinsameSteuererklaerung() != null &&
			gesuch.getFamiliensituation().getGemeinsameSteuererklaerung();

		final EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.extractEinkommensverschlechterungInfo();

		final String basisJahrPlus1 = String.valueOf(gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear().getYear() + 1);
		final String basisJahrPlus2 = String.valueOf(gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear().getYear() + 2);

		final Gesuchsteller gesuchsteller1 = gesuch.getGesuchsteller1();
		final Gesuchsteller gesuchsteller2 = gesuch.getGesuchsteller2();

		if (einkommensverschlechterungInfo != null) {
			if (einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus1() || einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2()) {
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1, gemeinsam, 1, 1, basisJahrPlus1);
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2, gemeinsam, 2, 1, basisJahrPlus1);
			}
			if (einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2()) {
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1, gemeinsam, 1, 2, basisJahrPlus2);
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2, gemeinsam, 2, 2, basisJahrPlus2);
			}
		}

	}

	private void getAllDokumenteGesuchsteller(Set<DokumentGrund> anlageVerzeichnis, Gesuchsteller gesuchsteller,
											  boolean gemeinsam, int gesuchstellerNumber, int basisJahrPlusNumber, String basisJahr) {

		if (gesuchsteller == null || gesuchsteller.getEinkommensverschlechterungContainer() == null) {
			return;
		}

		final EinkommensverschlechterungContainer einkommensverschlechterungContainer = gesuchsteller.getEinkommensverschlechterungContainer();
		Einkommensverschlechterung einkommensverschlechterung;
		if (basisJahrPlusNumber == 2) {
			einkommensverschlechterung = einkommensverschlechterungContainer.getEkvJABasisJahrPlus2();
		} else {
			einkommensverschlechterung = einkommensverschlechterungContainer.getEkvJABasisJahrPlus1();
		}

		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller.getFullName(), basisJahr, gemeinsam,
			gesuchstellerNumber, einkommensverschlechterung, DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);

		add(getDokument(DokumentTyp.NACHWEIS_EINKOMMENSSITUATION_MONAT, einkommensverschlechterung, gesuchsteller.getFullName(),
			basisJahr, DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG), anlageVerzeichnis);

	}

	@Override
	protected boolean isMonatsLohnausweisNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation) {
		if (abstractFinanzielleSituation instanceof Einkommensverschlechterung) {

			Einkommensverschlechterung einkommensverschlechterung = (Einkommensverschlechterung) abstractFinanzielleSituation;

			return !einkommensverschlechterung.getSteuerveranlagungErhalten() &&
				(
					einkommensverschlechterung.getNettolohnJan() != null && einkommensverschlechterung.getNettolohnJan().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnFeb() != null && einkommensverschlechterung.getNettolohnFeb().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnMrz() != null && einkommensverschlechterung.getNettolohnMrz().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnApr() != null && einkommensverschlechterung.getNettolohnApr().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnMai() != null && einkommensverschlechterung.getNettolohnMai().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnJun() != null && einkommensverschlechterung.getNettolohnJun().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnJul() != null && einkommensverschlechterung.getNettolohnJul().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnAug() != null && einkommensverschlechterung.getNettolohnAug().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnSep() != null && einkommensverschlechterung.getNettolohnSep().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnOkt() != null && einkommensverschlechterung.getNettolohnOkt().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnNov() != null && einkommensverschlechterung.getNettolohnNov().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnDez() != null && einkommensverschlechterung.getNettolohnDez().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnZus() != null && einkommensverschlechterung.getNettolohnZus().compareTo(BigDecimal.ZERO) > 0
				);
		}
		return false;
	}

}
