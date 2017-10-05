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

package ch.dvbern.ebegu.ws.ewk;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import ch.bern.e_gov.e_begu.egov_002.PersonenSucheResp;
import ch.dvbern.ebegu.cdi.Dummy;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy Implementation des EWK-Services
 */
@Dummy
@Dependent
public class EWKWebServiceDummy implements IEWKWebService {

	private static final String ID_MICHAEL_SCHUHMACHER = "1000028027";
	private static final String ID_SIMONE_MEIER = "1000348433";
	private static final String ID_FANNY_HUBER = "1000233097";
	private static final String ID_SANDRA_ANDEREGG = "1000197262";
	private static final String ID_HERBERT_GERBER = "1000637396";

	private static final String FILE_MICHAEL_SCHUHMACHER = "michael.schuhmacher.xml";
	private static final String FILE_SIMONE_MEIER = "simone.meier.xml";
	private static final String FILE_FANNY_HUBER = "fanny.huber.xml";
	private static final String FILE_SANDRA_ANDEREGG = "sandra.anderegg.xml";
	private static final String FILE_HERBERT_GERBER = "herbert.gerber.xml";
	private static final String FILE_NO_RESULT = "noresult.xml";

	private static final Logger LOGGER = LoggerFactory.getLogger(EWKWebServiceDummy.class.getSimpleName());

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheResp response = null;
		switch (id) {
		case ID_MICHAEL_SCHUHMACHER:
			response = parse(FILE_MICHAEL_SCHUHMACHER);
			break;
		case ID_SIMONE_MEIER:
			response = parse(FILE_SIMONE_MEIER);
			break;
		case ID_FANNY_HUBER:
			response = parse(FILE_FANNY_HUBER);
			break;
		case ID_SANDRA_ANDEREGG:
			response = parse(FILE_SANDRA_ANDEREGG);
			break;
		case ID_HERBERT_GERBER:
			response = parse(FILE_HERBERT_GERBER);
			break;
		default:
			response = parse(FILE_NO_RESULT);
			break;
		}
		return EWKConverter.convertFromEWK(response, EWKWebService.MAX_RESULTS_ID);
	}

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull String vorname, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheResp response;
		if ("Schuhmacher".equalsIgnoreCase(name)) {
			response = parse(FILE_MICHAEL_SCHUHMACHER);
		} else if ("Meier".equalsIgnoreCase(name)) {
			response = parse(FILE_SIMONE_MEIER);
		} else if ("Huber".equalsIgnoreCase(name)) {
			response = parse(FILE_FANNY_HUBER);
		} else if ("Anderegg".equalsIgnoreCase(name)) {
			response = parse(FILE_SANDRA_ANDEREGG);
		} else if ("PersonenSucheServiceException".equalsIgnoreCase(name)) {
			PersonenSucheServiceException e = new PersonenSucheServiceException("suchePerson", "PersonenSucheServiceException aufgetreten");
			LOGGER.error("Absichtlich provozierter Fehler bei Personensuche im Dummy-Service", e);
			throw e;
		} else if ("PersonenSucheServiceBusinessException".equalsIgnoreCase(name)) {
			PersonenSucheServiceBusinessException e = new PersonenSucheServiceBusinessException("suchePerson", "01", "Ein Fehler");
			LOGGER.error("Absichtlich provozierter Fehler bei Personensuche im Dummy-Service", e);
			throw e;
		} else {
			response = parse(FILE_HERBERT_GERBER);
		}
		return EWKConverter.convertFromEWK(response, EWKWebService.MAX_RESULTS_NAME);
	}

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return suchePerson(name, "irgendwer", geburtsdatum, geschlecht);
	}

	private PersonenSucheResp parse(String filename) throws PersonenSucheServiceException {
		try {
			byte[] bytes = ByteStreams.toByteArray(EWKWebServiceDummy.class.getResourceAsStream('/' + filename));
			String contents = new String(bytes, UTF8);
			JAXBContext jaxbContext = JAXBContext.newInstance(PersonenSucheResp.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			// Root-Element: PersonenSucheResp Hat kein @XmlRootElement-Annotation, darum muss hier angegeben werden, was wir zurueck erwarten
			final JAXBElement<PersonenSucheResp> o = unmarshaller.unmarshal(new StreamSource(new StringReader(contents)), PersonenSucheResp.class);
			return o.getValue();
		} catch (IOException | JAXBException e) {
			throw new PersonenSucheServiceException("parse", "Could not read file " + filename, e);
		}
	}
}
