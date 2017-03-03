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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

/**
 * Konfiguration fuer Testing
 */
@Alternative
@Dependent
public class EbeguConfigurationDummyImpl extends EbeguConfigurationImpl {

	private static final long serialVersionUID = 7880484074016308515L;

	@Override
	public boolean getIsDevmode() {
		return true;
	}

	@Override
	public boolean getOpenIdmEnabled() {
		return false;
	}

	@Override
	public boolean isSendingOfMailsDisabled() {
		return true;
	}

	@Override
	public String getSenderAddress() {
		return "hallo@dvbern.ch";
	}

	@Override
	public String getOpenamURL() {
		return null;
	}

	@Override
	public boolean getLoginWithToken() {
		return false;
	}

	@Override
	public String getSMTPHost() {
		return null;
	}

	@Override
	public int getSMTPPort() {
		return 0;
	}

	@Override
	public String getHostname() {
		return "localhost";
	}

	@Override
	public boolean isDummyLoginEnabled() {
		return false;
	}

	@Override
	public String getEmailOfSuperUser() {
		return "hallo@dvbern.ch";
	}

	@Override
	public String getBackgroundColor() {
		return null;
	}
}
