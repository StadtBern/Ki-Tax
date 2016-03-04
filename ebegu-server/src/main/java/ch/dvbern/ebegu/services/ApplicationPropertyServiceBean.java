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
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service fuer ApplicationProperty
 */
@Stateless
@Local(ApplicationPropertyService.class)
public class ApplicationPropertyServiceBean extends AbstractBaseService implements ApplicationPropertyService {


	@Inject
	private Persistence<ApplicationProperty> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public ApplicationProperty saveOrUpdateApplicationProperty(@Nonnull final String key, @Nonnull final String value) {
		Validate.notNull(key);
		Validate.notNull(value);

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
	public ApplicationProperty readApplicationProperty(@Nonnull final String key) {
		return criteriaQueryHelper.getEntityByUniqueAttribute(ApplicationProperty.class, key, ApplicationProperty_.name);
	}

	@Override
	public List<ApplicationProperty> listApplicationProperties() {
		return new ArrayList<>(criteriaQueryHelper.getAll(ApplicationProperty.class));
	}


	@Override
	public void removeApplicationProperty(@Nonnull String testKey) {
		Validate.notNull(testKey);
		ApplicationProperty propertyToRemove = readApplicationProperty(testKey);
		Objects.requireNonNull(propertyToRemove, "Property to remove could not be found");
		persistence.remove(propertyToRemove);

	}
}
