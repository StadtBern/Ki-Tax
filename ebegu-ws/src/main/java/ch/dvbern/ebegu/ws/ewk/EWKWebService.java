package ch.dvbern.ebegu.ws.ewk;

import ch.bern.e_gov.cra.ReturnMessage;
import ch.bern.e_gov.e_begu.egov_002.PersonenSucheOB;
import ch.bern.e_gov.e_begu.egov_002.PersonenSucheReq;
import ch.bern.e_gov.e_begu.egov_002.PersonenSucheResp;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse ruft den PersonenSuche Webservice des EWK auf
 */
@Dependent
public class EWKWebService implements IEWKWebService {

	private static final String TARGET_NAME_SPACE = "http://bern.ch/E_GOV/E_BEGU/EGOV_002";
	private static final String SERVICE_NAME = "PersonenSuche_OBService";
	public static final BigInteger MAX_RESULTS_ID = BigInteger.ONE;
	public static final BigInteger MAX_RESULTS_NAME = BigInteger.TEN;
	private static final String RETURN_CODE_OKAY = "00";

	private static final Logger logger = LoggerFactory.getLogger(EWKWebService.class.getSimpleName());
	public static final String METHOD_NAME_SUCHE_PERSON = "suchePerson";
	public static final String METHOD_NAME_INIT_PERSONEN_SUCHE = "initPersonenSucheServicePort";

	@Inject
	private EbeguConfiguration config;

	private PersonenSucheOB port;

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheReq request = new PersonenSucheReq();
		request.setPersonID(id);
		request.setMaxTreffer(MAX_RESULTS_ID);

		PersonenSucheResp response = getService().personenSucheOB(request);
		if (response == null) {
			throw new PersonenSucheServiceException(METHOD_NAME_SUCHE_PERSON, "Response war NULL, es muss aber immer eine Antwort zurueckkommen");
		}
		handleResponseStatus(response);
		if (response.getAnzahlTreffer().intValue() > 1) {
			throw new PersonenSucheServiceException(METHOD_NAME_SUCHE_PERSON, "Mehr als eine Person gefunden mit ID " + id);
		}
		return EWKConverter.convertFromEWK(response, MAX_RESULTS_ID);
	}

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull String vorname, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheReq request = new PersonenSucheReq();
		request.setNachname(name);
		request.setVorname(vorname);
		request.setGeburtsdatum(geburtsdatum);
		ch.bern.e_gov.cra.Geschlecht geschlechtEWK = geschlecht.equals(Geschlecht.MAENNLICH) ? ch.bern.e_gov.cra.Geschlecht.M : ch.bern.e_gov.cra.Geschlecht.W;
		request.setGeschlecht(geschlechtEWK);
		request.setMaxTreffer(MAX_RESULTS_NAME);

		PersonenSucheResp response = getService().personenSucheOB(request);
		if (response == null) {
			throw new PersonenSucheServiceException(METHOD_NAME_SUCHE_PERSON, "Response war NULL, es muss aber immer eine Antwort zurueckkommen");
		}
		handleResponseStatus(response);
		return EWKConverter.convertFromEWK(response, MAX_RESULTS_NAME);
	}

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheReq request = new PersonenSucheReq();
		request.setNachname(name);
		request.setGeburtsdatum(geburtsdatum);
		ch.bern.e_gov.cra.Geschlecht geschlechtEWK = geschlecht.equals(Geschlecht.MAENNLICH) ? ch.bern.e_gov.cra.Geschlecht.M : ch.bern.e_gov.cra.Geschlecht.W;
		request.setGeschlecht(geschlechtEWK);
		request.setMaxTreffer(MAX_RESULTS_NAME);

		PersonenSucheResp response = getService().personenSucheOB(request);
		if (response == null) {
			throw new PersonenSucheServiceException(METHOD_NAME_SUCHE_PERSON, "Response war NULL, es muss aber immer eine Antwort zurueckkommen");
		}
		handleResponseStatus(response);
		return EWKConverter.convertFromEWK(response, MAX_RESULTS_NAME);
	}

	/**
	 * Analysiert das Response-Objekt. Falls ein Fehlercode vorhanden ist, wird eine Exception geworfen.
	 */
	private void handleResponseStatus(@Nonnull  PersonenSucheResp response) throws PersonenSucheServiceBusinessException, PersonenSucheServiceException {
		ReturnMessage returnMessage = response.getReturnMessage();
		if (returnMessage == null) {
			logger.error("Das Statusobjekt aus der Response vom SARI Service war null, dies ist unerwartet und darf nicht vorkommen");
			throw new PersonenSucheServiceException("handleResponseStatus", "Status der Response muss gesetzt sein");
		}
		//wenn der Status nicht 0 ist ist es ein Fehler
		if (!RETURN_CODE_OKAY.equals(returnMessage.getCode())) {
			String msg = "EWK: Fehler bei Webservice Aufruf: " + returnMessage.getCode() + " / " + returnMessage.getText();
			logger.error(msg);
			throw new PersonenSucheServiceBusinessException("handleResponseStatus", returnMessage.getCode(), returnMessage.getText());
		} else {
			logger.debug("Response indicates SUCCESS");
		}
	}

	/**
	 * initialisiert den Service Port wenn noetig oder gibt ihn zurueck.
	 * @throws SARIServiceNotAvailableException
	 */
	private PersonenSucheOB getService() throws PersonenSucheServiceException {
		if (port == null) {
			initPersonenSucheServicePort();
		}
		return port;
	}

	private void initPersonenSucheServicePort() throws PersonenSucheServiceException {
		logger.info("Initialising PersonenSucheService:");
		if (port == null) {
			String endpointURL = config.getPersonenSucheEndpoint();
			String wsdlURL = config.getPersonenSucheWsdl();
			String username = config.getPersonenSucheUsername();
			String password = config.getPersonenSuchePassword();
			if (StringUtils.isEmpty(endpointURL)) {
				throw new PersonenSucheServiceException(METHOD_NAME_INIT_PERSONEN_SUCHE, "Es wurde keine Endpunkt URL definiert fuer den PersonenSuche Service");
			}
			if (StringUtils.isEmpty(username)) {
				throw new PersonenSucheServiceException(METHOD_NAME_INIT_PERSONEN_SUCHE, "Es wurde keine Username definiert fuer den PersonenSuche Service");
			}
			if (StringUtils.isEmpty(password)) {
				throw new PersonenSucheServiceException(METHOD_NAME_INIT_PERSONEN_SUCHE, "Es wurde keine Passwort definiert fuer den PersonenSuche Service");
			}
			logger.info("PersonenSucheService Endpoint: " + endpointURL);
			logger.info("PersonenSucheService Username: " + username);

			try {
				// Test der neu mitgeteilten WSDL-URL:
				URL wsdl = new URL(wsdlURL);
				logger.info("PersonenSucheService WSDL: " + wsdl);
				Object content = wsdl.getContent();
				logger.info("PersonenSucheService WSDL-Content: " + content);
			} catch (IOException e) {
				logger.error("PersonenSucheService WSDL not found: ", e);
			}

			try {
				// WSDL wird mitgeliefert. Die EndpointURL?wsdl funktioniert so nicht.
				final URL url = EWKWebService.class.getResource("/wsdl/Stadt_Bern_E-BEGU_Personensuche_v1.2.wsdl");
				Validate.notNull(url,"WSDL konnte unter der angegebenen URI nicht gefunden werden. Kann Service-Port nicht erstellen");
				logger.info("PersonenSucheService URL: " + url);
				logger.info("PersonenSucheService TargetNameSpace: " + TARGET_NAME_SPACE);
				logger.info("PersonenSucheService ServiceName: " + SERVICE_NAME);
				final QName qname = new QName(TARGET_NAME_SPACE, SERVICE_NAME);
				logger.info("PersonenSucheService QName: " + qname);
				final Service service = Service.create(url, qname);
				logger.info("PersonenSucheService Service created: " + service);
				port = service.getPort(PersonenSucheOB.class);
				logger.info("PersonenSucheService Port created: " + port);
				final BindingProvider bp = (BindingProvider) port;

				bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);

				// Authorization-Header setzen
				Map<String, List<String>> headers = new HashMap<>();
				String usernameAndPassword = username + ":" + password;
				String authorizationHeaderName = "Authorization";
				String authorizationHeaderValue = "Basic " + DatatypeConverter.printBase64Binary( usernameAndPassword.getBytes() );
				headers.put(authorizationHeaderName, Collections.singletonList(authorizationHeaderValue));
				bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, headers);
				logger.info("PersonenSucheService Authorization Header set");

				bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				logger.info("PersonenSucheService Context Properties set (Endpoint, Username, Password)");
			} catch (RuntimeException e) {
				port = null;
				logger.error("PersonenSucheOB-Service konnte nicht initialisiert werden: ", e);
				throw new PersonenSucheServiceException(METHOD_NAME_INIT_PERSONEN_SUCHE, "Could not create service port for endpoint " + endpointURL, e);
			}
		}
		logger.info("PersonenSucheService erfolgreich initialisiert");
	}
}
