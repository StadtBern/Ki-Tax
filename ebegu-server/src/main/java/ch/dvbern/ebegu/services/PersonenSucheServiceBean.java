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

package ch.dvbern.ebegu.services;

import java.time.LocalDate;

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

import ch.dvbern.ebegu.cdi.Dummy;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;
import ch.dvbern.ebegu.ws.ewk.IEWKWebService;
import ch.dvbern.lib.cdipersistence.Persistence;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer die Personensuche
 */
@Stateless
@Local(PersonenSucheService.class)
@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
public class PersonenSucheServiceBean extends AbstractBaseService implements PersonenSucheService {

	@Inject
	@Any
	//wir entscheiden programmatisch ob wir den dummy brauchen, daher hier mal alle injecten und dann im postconstruct entscheiden
	private Instance<IEWKWebService> serviceInstance;

	private IEWKWebService ewkService;

	@Inject
	private EbeguConfiguration config;

	@Inject
	private Persistence persistence;

	@SuppressWarnings({ "PMD.UnusedPrivateMethod", "IfStatementWithIdenticalBranches" })
	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
	@PostConstruct
	private void resolveService() {
		if (config.isPersonenSucheDisabled()) {
			ewkService = serviceInstance.select(new AnnotationLiteral<Dummy>() {
			}).get();
		} else {
			ewkService = serviceInstance.select(new AnnotationLiteral<Default>() {
			}).get();
		}
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(@Nonnull Gesuchsteller gesuchsteller) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		Validate.notNull(gesuchsteller, "Gesuchsteller muss gesetzt sein");
		Validate.isTrue(!gesuchsteller.isNew(), "Gesuchsteller muss zuerst gespeichert werden!");
		EWKResultat resultat;
		if (StringUtils.isNotEmpty(gesuchsteller.getEwkPersonId())) {
			resultat = suchePerson(gesuchsteller.getEwkPersonId());
		} else {
			resultat = suchePerson(gesuchsteller.getNachname(), gesuchsteller.getGeburtsdatum(), gesuchsteller.getGeschlecht());
		}
		// Wenn es genau 1 Resultat gibt, wird dieses direkt gesetzt
		if (resultat.getAnzahlResultate() == 1) {
			gesuchsteller.setEwkPersonId(resultat.getPersonen().get(0).getPersonID());
			gesuchsteller.setEwkAbfrageDatum(LocalDate.now());
			persistence.merge(gesuchsteller);
		}
		return resultat;
	}

	@Override
	@Nonnull
	public Gesuchsteller selectPerson(@Nonnull Gesuchsteller gesuchsteller, @Nonnull String ewkPersonID) {
		Validate.notNull(gesuchsteller, "Gesuchsteller muss gesetzt sein");
		Validate.notNull(ewkPersonID, "ewkPersonID muss gesetzt sein");
		Validate.isTrue(!gesuchsteller.isNew(), "Gesuchsteller muss zuerst gespeichert werden!");
		gesuchsteller.setEwkPersonId(ewkPersonID);
		gesuchsteller.setEwkAbfrageDatum(LocalDate.now());
		return persistence.merge(gesuchsteller);
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(@Nonnull String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return ewkService.suchePerson(id);
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull String vorname, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return ewkService.suchePerson(name, vorname, geburtsdatum, geschlecht);
	}

	@Override
	@Nonnull
	public EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException {
		return ewkService.suchePerson(name, geburtsdatum, geschlecht);
	}
}
