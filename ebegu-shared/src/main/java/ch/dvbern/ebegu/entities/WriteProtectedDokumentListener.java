package ch.dvbern.ebegu.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;

public class WriteProtectedDokumentListener {

	private static final Logger LOG = LoggerFactory.getLogger(WriteProtectedDokumentListener.class);

	@PostLoad
	public void postLoad(WriteProtectedDokument writeProtectedDokument) {
		writeProtectedDokument.setOrginalWriteProtected(writeProtectedDokument.isWriteProtected());
	}

	@PreUpdate
	public void preUpdate(WriteProtectedDokument writeProtectedDokument) {

		// Write Protection darf nicht entfernt werden
		if (writeProtectedDokument.isOrginalWriteProtected() && !writeProtectedDokument.isWriteProtected()) {
			LOG.warn("Write protection auf GeneratedDokument darf nicht mehr entfernt werden!");
			writeProtectedDokument.setWriteProtected(true);
		}

		// Wenn es writeProtection nicht neu ist, darf es nicht gespeichert werden! (Wenn WriteProtection neu gesetzt
		// wird, darf es noch genau einmal upgedated werden)
		if (writeProtectedDokument.isWriteProtected() && writeProtectedDokument.isOrginalWriteProtected()) {
			throw new IllegalStateException("GeneratedDokument darf nicht mehr verändert werden wenn writeProtected!");
		}
	}

}
