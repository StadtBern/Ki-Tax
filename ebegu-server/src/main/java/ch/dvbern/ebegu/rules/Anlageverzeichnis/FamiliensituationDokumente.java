package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Set;

/**
 * Dokumente für Familiensituation:
 * <p>
 * Trennungsvereinbarung / Scheidungsurteil / Sonstiger Nachweis über Trennung / Eheschutzverfahren:
 * <p>
 * Wird nur bei Mutation der Familiensituation verlangt, nicht bei Erstgesuch.
 * Notwendig beim Wechsel von zwei Gesuchsteller auf einen.
 * Nur eines der drei Dokumente ist notwendig. Die Dokumente werden im Anlageverzeichnis als 1 Punkt geführt
 **/
public class FamiliensituationDokumente extends AbstractDokumente<Familiensituation, Familiensituation> {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {

		//TODO: Sobald die Familiensituation mutiert werden kann muss hier als zweiter Parameter die mutierte Familiensituation angegeben werden
		add(getDokument(DokumentTyp.NACHWEIS_TRENNUNG, gesuch.getFamiliensituation(), gesuch.getFamiliensituation(), null, null, DokumentGrundTyp.FAMILIENSITUATION), anlageVerzeichnis);

	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Familiensituation dataForDocument) {
		return false;
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Familiensituation familiensituation1, Familiensituation familiensituation2) {
		if (familiensituation1 == null || familiensituation2 == null) {
			return false;
		}
		switch (dokumentTyp) {
			case NACHWEIS_TRENNUNG:
				//TODO: Sobald die Familiensituation mutiert werden kann muss überprüft werden, ob ein Wechsel von zwei Gesuchsteller auf einen stattgefunden hat.
				return true;
			default:
				return false;
		}
	}

}
