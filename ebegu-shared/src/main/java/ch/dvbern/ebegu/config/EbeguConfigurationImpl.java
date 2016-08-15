/*
 * Copyright (c) 2013 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.config;

import ch.dvbern.ebegu.services.FileSaverService;
import org.apache.commons.configuration.SystemConfiguration;

import javax.ejb.Local;
import javax.enterprise.context.Dependent;
import java.io.Serializable;

/**
 * Konfiguration von Kurstool. Liest system Properties aus
 */
@Dependent
public class EbeguConfigurationImpl extends SystemConfiguration implements EbeguConfiguration, Serializable {


	private static final long serialVersionUID = 463057263479503486L;
	private static final String EBEGU_DEVELOPMENT_MODE = "ebegu.development.mode";
	private static final String EBEGU_DOCUMENT_FILE_PATH = "ebegu.document.file.path";

	public EbeguConfigurationImpl() {


	}

	@Override
	public boolean getIsDevmode() {
		return getBoolean(EBEGU_DEVELOPMENT_MODE, true);
	}

	@Override
	public String getDocumentFilePath() {
		return getString(EBEGU_DOCUMENT_FILE_PATH, getString("jboss.server.data.dir"));
	}


}
