/*
 * Copyright (c) 2013 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.config.config;

import javax.enterprise.inject.Alternative;

/**
 * Konfiguration fuer Testing
 */
@Alternative
public class EbeguConfigurationDummyImpl extends EbeguConfigurationImpl {

	private static final long serialVersionUID = 7880484074016308515L;

	@Override
	public boolean getIsDevmode() {
		return true;
	}


}
