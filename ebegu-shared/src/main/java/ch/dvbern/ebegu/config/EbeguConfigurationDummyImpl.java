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

	@Override
	public String getDocumentFilePath() {
		return "jboss.server.data.dir";
	}

	@Override
	public String getFedletConfigPath() {
		return "fedletConfig/http_app_ebegu_ch";
	}

	@Override
	public boolean isClientUsingHTTPS() {
		return false;
	}

	@Override
	public String getOpenIdmURL() {
		return "https://eaccount-test.bern.ch";
	}

	@Override
	public String getOpenIdmUser() {
		return "SRVC_eBEGU";
	}

	@Override
	public String getOpenIdmPassword() {
		return "EBEGUADMINTZZ0";
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
}
