/*
 * Copyright (c) 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.ewk;

import ch.bern.e_gov.e_begu.egov_002.PersonenSucheResp;
import ch.dvbern.ebegu.cdi.Dummy;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;
import ch.dvbern.ebegu.ws.personensuche.service.IEWKWebService;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Dummy Implementation des EWK-Services
 */
@Dummy
@Dependent
public class EWKWebServiceDummy implements IEWKWebService {

	private static final String ID_MARC_SCHMID = "1000028027";
	private static final String ID_SIMONE_MEIER = "1000348433";
	private static final String ID_FRANZISKA_HERGER = "1000233097";
	private static final String ID_SANDRA_ANDEREGG = "1000197262";
	private static final String ID_HERBERT_GERBER = "1000637396";

	private static final String FILE_MARC_SCHMID = "marc.schmid.xml";
	private static final String FILE_SIMONE_MEIER = "simone.meier.xml";
	private static final String FILE_FRANZISKA_HERGER = "franziska.herger.xml";
	private static final String FILE_SANDRA_ANDEREGG = "sandra.anderegg.xml";
	private static final String FILE_HERBERT_GERBER = "herbert.gerber.xml";
	private static final String FILE_NO_RESULT = "noresult.xml";

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheResp response = null;
		if (ID_MARC_SCHMID.equals(id)) {
			response = parse(FILE_MARC_SCHMID);
		} else if (ID_SIMONE_MEIER.equals(id)) {
			response = parse(FILE_SIMONE_MEIER);
		} else if (ID_FRANZISKA_HERGER.equals(id)) {
			response = parse(FILE_FRANZISKA_HERGER);
		} else if (ID_SANDRA_ANDEREGG.equals(id)) {
			response = parse(FILE_SANDRA_ANDEREGG);
		} else if (ID_HERBERT_GERBER.equals(id)) {
			response = parse(FILE_HERBERT_GERBER);
		} else {
			response = parse(FILE_NO_RESULT);
		}
		return EWKConverter.convertFromEWK(response);
	}

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull String vorname, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		PersonenSucheResp response = null;
		if ("Schmid".equalsIgnoreCase(name)) {
			response = parse(FILE_MARC_SCHMID);
		} else if ("Meier".equalsIgnoreCase(name)) {
			response = parse(FILE_SIMONE_MEIER);
		} else if ("Herger".equalsIgnoreCase(name)) {
			response = parse(FILE_FRANZISKA_HERGER);
		} else if ("Anderegg".equalsIgnoreCase(name)) {
			response = parse(FILE_SANDRA_ANDEREGG);
		} else {
			// Default: Marc Schmid
			response = parse(FILE_HERBERT_GERBER);
		}
		return EWKConverter.convertFromEWK(response);
	}

	@Nonnull
	@Override
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return suchePerson(name, "irgendwer", geburtsdatum, geschlecht);
	}

	private PersonenSucheResp parse(String filename) throws PersonenSucheServiceException {
		try {
			File file = new File("/home/hefr/workspaces/ebegu/ebegu-ws/src/main/resources/" + filename); //TODO Hefr abs pfad
			String contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			JAXBContext jaxbContext = JAXBContext.newInstance(PersonenSucheResp.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			// Root-Element: PersonenSucheResp Hat kein @XmlRootElement-Annotation, darum muss hier angegeben werden, was wir zurueck erwarten
			final JAXBElement o = unmarshaller.unmarshal(new StreamSource(new StringReader(contents)), PersonenSucheResp.class);
			return (PersonenSucheResp) o.getValue();
		} catch (Exception e) {
			throw new PersonenSucheServiceException("Could not read file " + filename, e);
		}
	}
}
