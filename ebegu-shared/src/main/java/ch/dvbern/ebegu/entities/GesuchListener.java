package ch.dvbern.ebegu.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 09/01/2017.
 */
public class GesuchListener {

	private static final Logger LOG = LoggerFactory.getLogger(GesuchListener.class);

	/*@Inject
	private GeneratedDokumentService generatedDokumentService;	*/

	@PostLoad
	public void postLoad(Gesuch gesuch){
		gesuch.setOrginalStatus(gesuch.getStatus());
	}

	@PostUpdate
	public void postUpdate(Gesuch gesuch){
		LOG.debug("Geusch " + gesuch.getId() + " Status is being updated.");
		if (gesuch.getOrginalStatus() != null && gesuch.getStatus() != null && gesuch.getOrginalStatus() != gesuch.getStatus()) {
			LOG.debug("Orignal Status" + gesuch.getOrginalStatus().name());
			LOG.debug("New Status" + gesuch.getStatus().name());
			if (gesuch.getStatus().isAnyStatusOfVerfuegt() && !gesuch.getOrginalStatus().isAnyStatusOfVerfuegt()) {
				LOG.debug(String.format("Gesuch %s is changing from status %s to %s. Regenerating Deckblatt.",
					gesuch.getId(),
					gesuch.getOrginalStatus().name(),
					gesuch.getStatus().name()));
				// TODO : would like to access GeneratedDokumentService here but not possible due to bug:
				// https://issues.jboss.org/browse/WFLY-2387
				/*try {
					generatedDokumentService.getDokumentAccessTokenGeneratedDokument(gesuch, GeneratedDokumentTyp.BEGLEITSCHREIBEN, false);
				} catch (MimeTypeParseException | MergeDocException e) {
					LOG.error("Error updating Deckblatt Dokument", e);
				}*/

			}
		}
	}

}
