package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.cdi.Dummy;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;
import ch.dvbern.ebegu.ws.personensuche.service.IEWKWebService;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.time.LocalDate;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer die Personensuche
 */
@Stateless
@Local(PersonenSucheService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
public class PersonenSucheServiceBean extends AbstractBaseService implements PersonenSucheService {

	@Inject
	@Any
	//wir entscheiden programmatisch ob wir den dummy brauchen, daher hier mal alle injecten und dann im postconstruct entscheiden
	private Instance<IEWKWebService> serviceInstance;

	private IEWKWebService ewkService;

	@Inject
	private EbeguConfiguration config;


	@PostConstruct
	private void resolveService() {
		if (config.isPersonenSucheDisabled()) {
			ewkService = serviceInstance.select(new AnnotationLiteral<Dummy>() {}).get();
		} else {
			ewkService = serviceInstance.select(new AnnotationLiteral<Default>() {}).get();
		}
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return ewkService.suchePerson(id);
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(String name, String vorname, LocalDate geburtsdatum, Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return ewkService.suchePerson(name, vorname, geburtsdatum, geschlecht);
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return ewkService.suchePerson(name, geburtsdatum, geschlecht);
	}
}
