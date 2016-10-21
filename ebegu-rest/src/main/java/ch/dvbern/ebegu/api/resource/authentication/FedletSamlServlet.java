/*
 * Copyright © 2010 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere fuer Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthAccessElement;
import ch.dvbern.ebegu.api.util.EBEGUSamlConstants;
import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.util.Constants;
import com.google.gson.Gson;
import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.profile.SPACSUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static ch.dvbern.ebegu.api.resource.authentication.AuthResource.COOKIE_PATH;
import static ch.dvbern.ebegu.enums.UserRole.GESUCHSTELLER;


/**
 * Dieses Servlet verarbeitet die SAML2 Antworten des IAM Systems des Kanton Bern
 */
public class FedletSamlServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(FedletSamlServlet.class);

	private static final long serialVersionUID = -1712808705436451639L;
	@Inject
	private BenutzerService benutzerService;

	@Inject
	private AuthService authService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private EbeguConfiguration configuration;

	@Inject
	private TraegerschaftService traegerschaftService;

	public static final String IAM_SEPARATOR_ROLE = ";";

	@Inject
	private MandantService mandantService;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		PrintWriter printer = response.getWriter();
		Map<String, Object> map;
		try {
			// invoke the Fedlet processing logic. this will do all the
			// necessary processing conforming to SAMLv2 specifications,
			// such as XML signature validation, Audience and Recipient
			// validation etc.

			map = SPACSUtils.processResponseForFedlet(request, response, printer);
		} catch (SAML2Exception | IOException | SessionException sme) {
			SAMLUtils.sendError(request, response,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "failedToProcessSSOResponse",
				sme.getMessage());
			return;
		} catch (ServletException se) {
			SAMLUtils.sendError(request, response,
				HttpServletResponse.SC_BAD_REQUEST, "failedToProcessSSOResponse",
				se.getMessage());
			return;
		}
		// END : code is a must for Fedlet (SP) side application


		Map<String, Set<String>> userattrs = (Map<String, Set<String>>) map.get(SAML2Constants.ATTRIBUTE_MAP);
		AuthAccessElement userAuth = null;
		if (userattrs != null) {

			Benutzer benutzer = convertSAMLAttributesToBenutzer(userattrs);
			Benutzer storedBenutzer = benutzerService.updateOrStoreUserFromIAM(benutzer);

			AuthorisierterBenutzer authorisedBenutzer = new AuthorisierterBenutzer();
			authorisedBenutzer.setBenutzer(storedBenutzer);
			authorisedBenutzer.setAuthToken(RandomStringUtils.randomAlphanumeric(Constants.UUID_LENGTH));  //auth token generieren
			authorisedBenutzer.setLastLogin(LocalDateTime.now());
			authorisedBenutzer.setRole(benutzer.getRole());
			authorisedBenutzer.setUsername(benutzer.getUsername());
			userAuth = this.authService.createLoginFromIAM(authorisedBenutzer);

			//to inform the client about the logged in user we set some cookies to pass along the generated login token
			setCookiesForClient(response, userAuth);

		}

		String relayUrl = (String) map.get(SAML2Constants.RELAY_STATE);
		if ((relayUrl != null) && (!relayUrl.isEmpty())) {
			// something special for validation to send redirect, this parameter could be removed if it is also removed on the client side
			int stringPos = relayUrl.indexOf("sendRedirectForValidationNow=true");
			if (stringPos != -1) {
				response.sendRedirect(relayUrl);
			}
		} else {
			LOG.warn("Received SAML2 response without RelayState. Staying on page");
			displayMinimalInfo(printer, userAuth);
		}
	}

	/**
	 * Reads the Attributes from the map containing the data passed by saml and creates a Benutzer object that can be
	 * used by ebegu
	 */
	@Nonnull
	private Benutzer convertSAMLAttributesToBenutzer(@Nonnull Map<String, Set<String>> userattrs) {
		String role = extractAttribute(userattrs, EBEGUSamlConstants.BGOV_EBEGU_ROLE);
		String loginName = extractAttribute(userattrs, EBEGUSamlConstants.LOGIN_NAME);
		String cn = extractAttribute(userattrs, EBEGUSamlConstants.COMMON_NAME);
		String givenName = extractAttribute(userattrs, EBEGUSamlConstants.GIVEN_NAME);
		String surname = extractAttribute(userattrs, EBEGUSamlConstants.SN);
		String mail = extractAttribute(userattrs, EBEGUSamlConstants.MAIL);
		String telephoneNumber = extractAttribute(userattrs, EBEGUSamlConstants.TELEPHONE_NUMBER);
		String mobile = extractAttribute(userattrs, EBEGUSamlConstants.MOBILE);
		String preferredLang = extractAttribute(userattrs, EBEGUSamlConstants.PREFERRED_LANGUANGE);
		String postalAddress = extractAttribute(userattrs, EBEGUSamlConstants.POSTAL_ADDRESS);
		String street = extractAttribute(userattrs, EBEGUSamlConstants.STREET);
		String postalCode = extractAttribute(userattrs, EBEGUSamlConstants.POSTAL_CODE);
		String state = extractAttribute(userattrs, EBEGUSamlConstants.STATE);
		String countryCode = extractAttribute(userattrs, EBEGUSamlConstants.COUNTRY_CODE);
		String country = extractAttribute(userattrs, EBEGUSamlConstants.COUNTRY);

		String unusedAttr = StringUtils.join(cn, telephoneNumber, mobile, preferredLang, postalAddress, state, street, postalCode, countryCode, country,',');

		Benutzer benutzer = new Benutzer();
		benutzer.setVorname(givenName);
		benutzer.setNachname(surname);
		benutzer.setEmail(mail);

		//todo team convert adress und speichers auf Benutzer
		LOG.warn("The following attributes are received from IAM but not yet stored " + unusedAttr);
		convertAndSetRoleAndInstitution(role, benutzer);

		benutzer.setMandant(mandantService.getFirst());
		benutzer.setUsername(loginName);
		return benutzer;
	}

	/**
	 * This method sets the required cookies in the response from this servlet. the authToken is the most important one
	 * as it contains the login token. the xsrf token is used to prevent hackers from stealing the login token
	 * The principal cookie just stores information about the user but has nothing to do with actual authentication
	 */
	private void setCookiesForClient(HttpServletResponse response, AuthAccessElement userAuth) {
		// Cookie to store auth_token, HTTP-Only Cookie --> Protection from XSS
		Cookie authCookie = new Cookie(AuthDataUtil.COOKIE_AUTH_TOKEN, userAuth.getAuthToken());
		authCookie.setComment("authentication");
//			authCookie.setDomain(".");
		authCookie.setPath(COOKIE_PATH);
		authCookie.setMaxAge(Constants.COOKIE_TIMEOUT_SECONDS);
		boolean useSecureCookies = this.configuration.isClientUsingHTTPS();
		authCookie.setSecure(useSecureCookies);    //todo fuer dev env loesung finden oder configurierbar machen
		authCookie.setHttpOnly(true);
		response.addCookie(authCookie);

		// Readable Cookie for XSRF Protection (the Cookie can only be read from our Domain)
		Cookie xsrfCookie = new Cookie(AuthDataUtil.COOKIE_XSRF_TOKEN, userAuth.getXsrfToken());
		xsrfCookie.setComment("xsfr prevention");
//			xsrfCookie.setDomain(".");
		xsrfCookie.setPath(COOKIE_PATH);
		xsrfCookie.setMaxAge(Constants.COOKIE_TIMEOUT_SECONDS);
			xsrfCookie.setSecure(useSecureCookies);
		xsrfCookie.setHttpOnly(false);
		response.addCookie(xsrfCookie);


		// Readable Cookie storing user data
		JaxAuthAccessElement element = converter.authAccessElementToJax(userAuth);
		Cookie principalCookie = new Cookie(AuthDataUtil.COOKIE_PRINCIPAL, encodeAuthAccessElement(element));
		principalCookie.setComment("principal");
//			principalCookie.setDomain(".");
		principalCookie.setPath(COOKIE_PATH);
		principalCookie.setMaxAge(Constants.COOKIE_TIMEOUT_SECONDS);
		principalCookie.setSecure(useSecureCookies);
		principalCookie.setHttpOnly(false);
		response.addCookie(principalCookie);
	}

	/**
	 * This method shows some infos of the logged in user in case there was no redirect specified
	 */
	private void displayMinimalInfo(PrintWriter printer, AuthAccessElement userAuth) {
		if (userAuth != null) {
			printer.println("Logged in User: " + userAuth.getAuthId() + "(" + userAuth.getVorname() + " " + userAuth.getNachname() + ")");
			printer.println("Role of logged in User " + userAuth.getRole());
		}
	}

	private String encodeAuthAccessElement(JaxAuthAccessElement element) {
		Gson gson = new Gson();
		return Base64.getEncoder().encodeToString(gson.toJson(element).getBytes(Charset.forName("UTF-8")));
	}

	private void convertAndSetRoleAndInstitution(String role, Benutzer localUser) {

		if (StringUtils.isEmpty(role)) {
			localUser.setRole(GESUCHSTELLER);
			return;
		}

		String[] strings = role.split(IAM_SEPARATOR_ROLE);
		if (strings.length == 0) {
			throw new IllegalStateException("No Role recevied from IAM");
		}

		String roleName = strings[0];
		roleName = roleName.replace("EBEGU_", "");
		UserRole userRole = UserRole.valueOf(roleName);
		localUser.setRole(userRole);

		if (UserRole.SACHBEARBEITER_INSTITUTION == userRole && strings.length == 2) {
			//read and store institution to user
			String institutionID = strings[1];
			Institution institution = institutionService.findInstitution(institutionID).orElseThrow(() -> new EbeguEntityNotFoundException("convertAndSetRoleAndInstitution", "Institution not found", institutionID));
			localUser.setInstitution(institution);

		}
		if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT == userRole && strings.length == 2) {
			//read and store traegerschaft to user
			String traegerschaftID = strings[1];
			Traegerschaft foundTraegerschaft = traegerschaftService
				.findTraegerschaft(traegerschaftID)
				.orElseThrow((() -> new EbeguEntityNotFoundException("convertAndSetRoleAndInstitution", "Traegerschaft not found: {}", traegerschaftID)));
			localUser.setTraegerschaft(foundTraegerschaft);
		}
	}

	/**
	 * extracts and flattens all strings stored under a given attribute key
	 */
	private String extractAttribute(Map<String, Set<String>> userattrs, String attrName) {

		String result = null;
		Set<String> attrVals = userattrs.get(attrName);
		if ((attrVals != null) && !attrVals.isEmpty()) {
			Iterator<String> it = attrVals.iterator();
			while (it.hasNext()) {
				result = (result != null) ? result + it.next() : it.next();
			}
		}

		return result;
	}
}
