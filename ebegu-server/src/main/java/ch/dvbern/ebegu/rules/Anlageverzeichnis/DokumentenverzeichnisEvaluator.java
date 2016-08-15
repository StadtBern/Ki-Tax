package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import javax.ejb.Stateless;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class DokumentenverzeichnisEvaluator {

	private AbstractDokumente kindAnlagen = new KindDokumente();
	private AbstractDokumente erwerbspensumDokumente = new ErwerbspensumDokumente();
	private AbstractDokumente finanzielleSituationDokumente = new FinanzielleSituationDokumente();
	private AbstractDokumente einkommensverschlechterungDokumente = new EinkommensverschlechterungDokumente();

	public Set<DokumentGrund> calculate(Gesuch gesuch) {

		Set<DokumentGrund> anlageVerzeichnis = new HashSet<>();

		if (gesuch != null) {
			kindAnlagen.getAllDokumente(gesuch, anlageVerzeichnis);
			erwerbspensumDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			finanzielleSituationDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
			einkommensverschlechterungDokumente.getAllDokumente(gesuch, anlageVerzeichnis);
		}


		return anlageVerzeichnis;
	}

	public void addSonstige(Set<DokumentGrund> dokumentGrunds, Gesuch gesuch) {
		DokumentGrund dokumentGrund = new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE , DokumentTyp.DIV);
		dokumentGrunds.add(dokumentGrund);
	}

}
