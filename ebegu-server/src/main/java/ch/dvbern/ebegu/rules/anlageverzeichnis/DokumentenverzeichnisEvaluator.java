package ch.dvbern.ebegu.rules.anlageverzeichnis;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

@Stateless
public class DokumentenverzeichnisEvaluator {

	private final AbstractDokumente familiensituationDokumente = new FamiliensituationDokumente();
	private final AbstractDokumente kindAnlagen = new KindDokumente();
	private final AbstractDokumente erwerbspensumDokumente = new ErwerbspensumDokumente();
	private final AbstractDokumente finanzielleSituationDokumente = new FinanzielleSituationDokumente();
	private final AbstractDokumente einkommensverschlechterungDokumente = new EinkommensverschlechterungDokumente();

	public Set<DokumentGrund> calculate(Gesuch gesuch) {

		Set<DokumentGrund> anlageVerzeichnis = new HashSet<>();

		if (gesuch != null) {
			familiensituationDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			kindAnlagen.getAllDokumente(gesuch, anlageVerzeichnis);
			erwerbspensumDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			finanzielleSituationDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			einkommensverschlechterungDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
		}

		return anlageVerzeichnis;
	}

	public void addSonstige(Set<DokumentGrund> dokumentGrunds) {
		DokumentGrund dokumentGrund = new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.DIV);
		dokumentGrund.setNeeded(false);
		dokumentGrunds.add(dokumentGrund);
	}

	public void addPapiergesuch(Set<DokumentGrund> dokumentGrunds, Gesuch gesuch) {
		DokumentGrund dokumentGrund = new DokumentGrund(DokumentGrundTyp.PAPIERGESUCH, DokumentTyp.ORIGINAL_PAPIERGESUCH);
		dokumentGrund.setNeeded(false);
		dokumentGrunds.add(dokumentGrund);
	}
}
