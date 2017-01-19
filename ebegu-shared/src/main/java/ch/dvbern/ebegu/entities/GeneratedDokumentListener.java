package ch.dvbern.ebegu.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

public class GeneratedDokumentListener {

	private static final Logger LOG = LoggerFactory.getLogger(GeneratedDokumentListener.class);

	@PostLoad
	public void postLoad(GeneratedDokument generatedDokument) {
		generatedDokument.setOrginalWriteProtected(generatedDokument.isWriteProtected());
	}

	@PreUpdate
	public void postUpdate(GeneratedDokument generatedDokument) {

		// Write Protection darf nicht entfernt werden
		if (generatedDokument.isOrginalWriteProtected() && !generatedDokument.isWriteProtected()) {
			LOG.warn("Write protection auf GeneratedDokument darf nicht mehr entfernt werden!");
			generatedDokument.setWriteProtected(true);
		}

		// Wenn es writeProtection nicht neu ist, darf es nicht gespeichert werden! (Wenn WriteProtection neu gesetzt
		// wird, darf es noch genau einmal upgedated werden)
		if (generatedDokument.isWriteProtected() && generatedDokument.isOrginalWriteProtected()) {
			throw new IllegalStateException("GeneratedDokument darf nicht mehr verändert werden wenn writeProtected!");
		}
	}

}
