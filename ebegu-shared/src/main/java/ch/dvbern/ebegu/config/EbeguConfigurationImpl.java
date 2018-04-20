/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.config;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.services.ApplicationPropertyService;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Konfiguration von Kurstool. Liest system Properties aus
 */
@Dependent
public class EbeguConfigurationImpl extends SystemConfiguration implements EbeguConfiguration, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(EbeguConfigurationImpl.class.getSimpleName());

	private static final long serialVersionUID = 463057263479503486L;
	public static final String EBEGU_DEVELOPMENT_MODE = "ebegu.development.mode";
	private static final String EBEGU_DOCUMENT_FILE_PATH = "ebegu.document.file.path";
	private static final String EBEGU_CLIENT_USING_HTTPS = "ebegu.client.using.https";
	private static final String EBEGU_MAIL_DISABLED = "ebegu.mail.disabled";
	private static final String EBEGU_MAIL_SMTP_FROM = "ebegu.mail.smtp.from";
	private static final String EBEGU_MAIL_SMTP_HOST = "ebegu.mail.smtp.host";
	private static final String EBEGU_MAIL_SMTP_PORT = "ebegu.mail.smtp.port";
	private static final String EBEGU_HOSTNAME = "ebegu.hostname";
	private static final String EBEGU_DUMMY_LOGIN_ENABLED = "ebegu.dummy.login.enabled";
	public static final String EBEGU_DUMP_DBUNIT_XML = "ebegu.dump.dbunit.xml";
	private static final String EBEGU_ZAHLUNGEN_TEST_MODE = "ebegu.zahlungen.test.mode";
	private static final String EBEGU_PERSONENSUCHE_DISABLED = "ebegu.personensuche.disabled";
	private static final String EBEGU_PERSONENSUCHE_ENDPOINT = "ebegu.personensuche.endpoint";
	private static final String EBEGU_PERSONENSUCHE_WSDL = "ebegu.personensuche.wsdl";
	private static final String EBEGU_PERSONENSUCHE_USERNAME = "ebegu.personensuche.username";
	private static final String EBEGU_PERSONENSUCHE_PASSWORD = "ebegu.personensuche.password";
	public static final String EBEGU_LOGIN_PROVIDER_API_URL = "ebegu.login.provider.api.url";
	private static final String EBEGU_LOGIN_API_ALLOW_REMOTE = "ebegu.login.api.allow.remote";
	private static final String EBEGU_LOGIN_API_INTERNAL_USER = "ebegu.login.api.internal.user";
	private static final String EBEGU_LOGIN_API_INTERNAL_PASSWORD = "ebegu.login.api.internal.password";
	private static final String EBEGU_FORCE_COOKIE_SECURE_FLAG = "ebegu.force.cookie.secure.flag";
	private static final String EBEGU_LOGIN_API_SCHULAMT_USER = "ebegu.login.api.schulamt.user";
	private static final String EBEGU_LOGIN_API_SCHULAMT_PASSWORD = "ebegu.login.api.schulamt.password";
	private static final String EBEGU_SEND_REPORTS_AS_ATTACHEMENT = "ebegu.send.reports.as.attachement";
	private static final String EBEGU_TESTFAELLE_ENABLED = "ebegu.testfaelle.enabled";
	private static final String EBEGU_ADMINISTRATOR_MAIL = "ebegu.admin.mail";


	@Inject
	private ApplicationPropertyService applicationPropertyService;


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

	@Override
	public boolean isClientUsingHTTPS() {
		return getBoolean(EBEGU_CLIENT_USING_HTTPS, false);
	}

	@Override
	public boolean isSendingOfMailsDisabled() {
		return getBoolean(EBEGU_MAIL_DISABLED, getIsDevmode());
	}

	@Override
	public String getSMTPHost() {
		return getString(EBEGU_MAIL_SMTP_HOST, null);
	}

	@Override
	public int getSMTPPort() {
		return getInt(EBEGU_MAIL_SMTP_PORT, 25);
	}

	@Override
	public String getSenderAddress() {
		return getString(EBEGU_MAIL_SMTP_FROM, null);
	}

	@Override
	public String getHostname() {
		return getString(EBEGU_HOSTNAME, null);
	}

	@Override
	public boolean isDummyLoginEnabled() {
		// Um das Dummy Login einzuschalten, muss sowohl das DB Property wie auch das System Property gesetzt sein. Damit
		// ist eine zusätzliche Sicherheit eingebaut, dass nicht aus Versehen z.B. mit einem Produktionsdump das Dummy Login
		// automatisch ausgeschaltet ist.
		Boolean flagFromDB = applicationPropertyService.findApplicationPropertyAsBoolean(ApplicationPropertyKey.DUMMY_LOGIN_ENABLED, false);
		Boolean flagFromServerConfig = getBoolean(EBEGU_DUMMY_LOGIN_ENABLED, false);
		return flagFromDB && flagFromServerConfig;
	}

	@Override
	public boolean getIsZahlungenTestMode() {
		return getBoolean(EBEGU_ZAHLUNGEN_TEST_MODE, false) && getIsDevmode();
	}

	@Override
	public boolean isPersonenSucheDisabled() {
		return getBoolean(EBEGU_PERSONENSUCHE_DISABLED, true);
	}

	@Override
	public String getPersonenSucheEndpoint() {
		return getString(EBEGU_PERSONENSUCHE_ENDPOINT);
	}

	@Override
	public String getPersonenSucheWsdl() {
		return getString(EBEGU_PERSONENSUCHE_WSDL);
	}

	@Override
	public String getPersonenSucheUsername() {
		return getString(EBEGU_PERSONENSUCHE_USERNAME);
	}

	@Override
	public String getPersonenSuchePassword() {
		return getString(EBEGU_PERSONENSUCHE_PASSWORD);
	}

	@Override
	public String getLoginProviderAPIUrl() {
		return getString(EBEGU_LOGIN_PROVIDER_API_URL);
	}

	@Override
	public boolean isRemoteLoginConnectorAllowed() {
		return getBoolean(EBEGU_LOGIN_API_ALLOW_REMOTE, false);
	}

	@Override
	public String getInternalAPIUser() {
		String user = getString(EBEGU_LOGIN_API_INTERNAL_USER);
		if (StringUtils.isEmpty(user)) {
			LOG.warn("Internal API User  must be set in the properties (key: {}) to use the LoginConnector API ",
				EBEGU_LOGIN_API_INTERNAL_USER);

		}
		return user;
	}

	@Override
	public String getInternalAPIPassword() {
		String internalUserPW = getString(EBEGU_LOGIN_API_INTERNAL_PASSWORD);
		if (StringUtils.isEmpty(internalUserPW)) {
			LOG.warn("Internal API password must be set in the properties (key: {}) to use the LoginConnector API ",
				EBEGU_LOGIN_API_INTERNAL_PASSWORD);
		}
		return internalUserPW;
	}

	@Override
	public String getSchulamtAPIUser() {
		return getString(EBEGU_LOGIN_API_SCHULAMT_USER);
	}

	@Override
	public String getSchulamtAPIPassword() {
		return getString(EBEGU_LOGIN_API_SCHULAMT_PASSWORD);
	}

	@Override
	public boolean forceCookieSecureFlag() {
		return getBoolean(EBEGU_FORCE_COOKIE_SECURE_FLAG, false);
	}

	@Override
	public boolean isSendReportAsAttachement() {
		return getBoolean(EBEGU_SEND_REPORTS_AS_ATTACHEMENT, false);
	}

	@Override
	public boolean isTestfaelleEnabled() {
		return getBoolean(EBEGU_TESTFAELLE_ENABLED, false);
	}

	@Override
	public String getAdministratorMail() {
		return getString(EBEGU_ADMINISTRATOR_MAIL);
	}
}
