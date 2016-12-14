package ch.dvbern.ebegu.rules.anlageverzeichnis;

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
		add(getDokument(DokumentTyp.NACHWEIS_TRENNUNG, gesuch.extractFamiliensituationErstgesuch(), gesuch.extractFamiliensituation(), null, null, DokumentGrundTyp.FAMILIENSITUATION), anlageVerzeichnis);
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Familiensituation dataForDocument) {
		return false;
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Familiensituation familiensituationErstgesuch, Familiensituation familiensituationMutation) {
		if (familiensituationErstgesuch == null || familiensituationMutation == null) {
			return false;
		}
		switch (dokumentTyp) {
			case NACHWEIS_TRENNUNG:
				//überprüfen, ob ein Wechsel von zwei Gesuchsteller auf einen stattgefunden hat.
				return familiensituationErstgesuch.hasSecondGesuchsteller() && !familiensituationMutation.hasSecondGesuchsteller();
			default:
				return false;
		}
	}
}
