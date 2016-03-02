/*
 * Copyright (c) 2014 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.entities.ApplicationProperty_;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Service fuer ApplicationProperty
 */
@Stateless
@Local(ApplicationPropertyService.class)
//@RolesAllowed({RoleNames.ADMIN_ROLENAME, RoleNames.SYSTEM_ROLENAME, RoleNames.SUBADMIN_ROLENAME, RoleNames.USER_ROLENAME})
public class ApplicationPropertyServiceBean extends AbstractBaseService implements ApplicationPropertyService {


	@Inject
	private Persistence<ApplicationProperty> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public ApplicationProperty saveOrUpdateApplicationProperty(@Nonnull final String key, @Nonnull final String value) throws EbeguException {
		ApplicationProperty property = readApplicationProperty(key);
		if (property == null) {
			return persistence.persist(new ApplicationProperty(key, value));

		} else {
			property.setValue(value);
			return persistence.merge(property);
		}

	}

	@Nullable
	@Override
	public ApplicationProperty readApplicationProperty(@Nonnull final String key) throws EbeguException {
		return criteriaQueryHelper.getEntityByUniqueAttribute(ApplicationProperty.class, key, ApplicationProperty_.name);
	}

	@Override
	public List<ApplicationProperty> listApplicationProperties() {
		return new ArrayList<>(criteriaQueryHelper.getAll(ApplicationProperty.class));
	}
}
