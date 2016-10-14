/*
 * Copyright © 2010 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere fuer Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package com.sun.identity.saml2;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthAccessElement;
import ch.dvbern.ebegu.api.dtos.JaxIamUser;
import ch.dvbern.ebegu.api.resource.authentication.AuthDataUtil;
import ch.dvbern.ebegu.api.util.EBEGUSamlConstants;
import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.util.Constants;
import com.google.gson.Gson;
import com.sun.identity.saml.common.SAMLUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

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
import java.util.*;

import static ch.dvbern.ebegu.api.resource.authentication.AuthResource.COOKIE_PATH;
import static ch.dvbern.ebegu.enums.UserRole.GESUCHSTELLER;


/**
 * Servlet fuer Dateiupload.
 */
public class FedletSamlServlet extends HttpServlet {

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private AuthService authService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private TraegerschaftService traegerschaftService;

	public static final String IAM_SEPARATOR_ROLE = ";";

	@Inject
	MandantService mandantService;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		PrintWriter printer = response.getWriter();
		// BEGIN : following code is a must for Fedlet (SP) side application
		Map map;
		try {
			// invoke the Fedlet processing logic. this will do all the
			// necessary processing conforming to SAMLv2 specifications,
			// such as XML signature validation, Audience and Recipient
			// validation etc.

			map = com.sun.identity.saml2.profile.SPACSUtils.processResponseForFedlet(request, response, printer);
		} catch ( com.sun.identity.saml2.common.SAML2Exception sme) {
			SAMLUtils.sendError(request, response,
				response.SC_INTERNAL_SERVER_ERROR, "failedToProcessSSOResponse",
				sme.getMessage());
			return;
		} catch (IOException ioe) {
			SAMLUtils.sendError(request, response,
				response.SC_INTERNAL_SERVER_ERROR, "failedToProcessSSOResponse",
				ioe.getMessage());
			return;
		} catch (com.sun.identity.plugin.session.SessionException se) {
			SAMLUtils.sendError(request, response,
				response.SC_INTERNAL_SERVER_ERROR, "failedToProcessSSOResponse",
				se.getMessage());
			return;
		} catch (ServletException se) {
			SAMLUtils.sendError(request, response,
				response.SC_BAD_REQUEST, "failedToProcessSSOResponse",
				se.getMessage());
			return;
		}
		// END : code is a must for Fedlet (SP) side application

		//mycode


		Map userattrs = (Map) map.get(com.sun.identity.saml2.common.SAML2Constants.ATTRIBUTE_MAP);
		if (userattrs != null) {


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

			JaxIamUser iamUser = new JaxIamUser();
			iamUser.setLoginName(loginName);
			iamUser.setSurname(surname);
			iamUser.setEmail(mail);
			iamUser.setCommonName(cn);
			iamUser.setGivenName(givenName);


			//todo check if user is stored
			//todo create user if not

			Benutzer benutzer = new Benutzer();
			benutzer.setVorname(givenName);
			benutzer.setNachname(surname);
			benutzer.setEmail(mail);
			//todo convert adress etc.
			convertAndSetRoleAndInstitution(role, benutzer);

			benutzer.setMandant(mandantService.getFirst());
			benutzer.setUsername(loginName);
			Benutzer storedBenutzer = benutzerService.updateOrStoreUserFromIAM(benutzer);


			AuthorisierterBenutzer authorisierterBenutzer = new AuthorisierterBenutzer();
			authorisierterBenutzer.setBenutzer(storedBenutzer);
			authorisierterBenutzer.setAuthToken(RandomStringUtils.randomAlphanumeric(32));  //auth token generieren
			authorisierterBenutzer.setLastLogin(LocalDateTime.now());
			AuthAccessElement userAuth = this.authService.createLoginFromIAM(authorisierterBenutzer);

			//			Cookie userCookie = new Cookie("name", "value");
			//			userCookie.setMaxAge(60*60*24*365); //Store cookie for 1 year
			//			response.addCookie(userCookie);


			// Cookie to store auth_token, HTTP-Only Cookie --> Protection from XSS
			Cookie authCookie = new Cookie(AuthDataUtil.COOKIE_AUTH_TOKEN, userAuth.getAuthToken());
			authCookie.setComment("authentication");
//			authCookie.setDomain(COOKIE_DOMAIN);
			authCookie.setPath(COOKIE_PATH);
			authCookie.setMaxAge(Constants.COOKIE_TIMEOUT_SECONDS);
//			authCookie.setSecure(request.isSecure());    //todo fuer dev env loesung finden
			authCookie.setHttpOnly(true);
			response.addCookie(authCookie);

			// Readable Cookie for XSRF Protection (the Cookie can only be read from our Domain)
			Cookie xsrfCookie = new Cookie(AuthDataUtil.COOKIE_XSRF_TOKEN, userAuth.getXsrfToken());
			xsrfCookie.setComment("xsfr prevention");
//			xsrfCookie.setDomain(COOKIE_DOMAIN);
			xsrfCookie.setPath(COOKIE_PATH);
			xsrfCookie.setMaxAge(Constants.COOKIE_TIMEOUT_SECONDS);
//			xsrfCookie.setSecure(request.isSecure());
			xsrfCookie.setHttpOnly(false);
			response.addCookie(xsrfCookie);


			// Readable Cookie storing user data
			JaxAuthAccessElement element = converter.authAccessElementToJax(userAuth);
			Cookie principalCookie = new Cookie(AuthDataUtil.COOKIE_PRINCIPAL, encodeAuthAccessElement(element));
			principalCookie.setComment("principal");
//			principalCookie.setDomain(COOKIE_DOMAIN);
			principalCookie.setPath(COOKIE_PATH);
			principalCookie.setMaxAge(Constants.COOKIE_TIMEOUT_SECONDS);
//			principalCookie.setSecure(request.isSecure());
			principalCookie.setHttpOnly(false);
			response.addCookie(principalCookie);


			String url = request.getRequestURL().toString();
			String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			Object o = map.get(com.sun.identity.saml2.common.SAML2Constants.RELAY_STATE);



		}

		// end mycode

		String relayUrl = (String) map.get(com.sun.identity.saml2.common.SAML2Constants.RELAY_STATE);
		if ((relayUrl != null) && (relayUrl.length() != 0)) {
			// something special for validation to send redirect
			int stringPos = relayUrl.indexOf("sendRedirectForValidationNow=true");
			if (stringPos != -1) {
				response.sendRedirect(relayUrl);
			}
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

		if (UserRole.SACHBEARBEITER_INSTITUTION == userRole) {
			//read and store institution to user
			if (strings.length == 2) {
				String institutionID = strings[1];
				Institution institution = institutionService.findInstitution(institutionID).orElseThrow(() -> new EbeguEntityNotFoundException("convertAndSetRoleAndInstitution", "Institution not found", institutionID));
				localUser.setInstitution(institution);

			}

		}
		if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT == userRole) {
			//read and store traegerschaft to user
			if (strings.length == 2) {
				String traegerschaftID = strings[1];
				Traegerschaft foundTraegerschaft = traegerschaftService
					.findTraegerschaft(traegerschaftID)
					.orElseThrow((() -> new EbeguEntityNotFoundException("convertAndSetRoleAndInstitution", "Traegerschaft not found: {}", traegerschaftID)));
				localUser.setTraegerschaft(foundTraegerschaft);

			}
		}
	}

	private String extractAttribute(Map userattrs, String attrName) {

		String result = null;
		Set attrVals = (HashSet) userattrs.get(attrName);
		if ((attrVals != null) && !attrVals.isEmpty()) {
			Iterator it = attrVals.iterator();
			while (it.hasNext()) {
				result = (result != null) ? result + it.next() : (String) it.next();
			}
		}

		return result;
	}
}
