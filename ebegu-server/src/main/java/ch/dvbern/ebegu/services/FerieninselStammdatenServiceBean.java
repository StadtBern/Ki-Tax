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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.entities.FerieninselStammdaten;
import ch.dvbern.ebegu.entities.FerieninselStammdaten_;
import ch.dvbern.ebegu.entities.Gesuchsperiode_;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service zum Verwalten von Ferieninsel-Stammdaten
 */
@Stateless
@Local(FerieninselStammdatenService.class)
@PermitAll
public class FerieninselStammdatenServiceBean extends AbstractBaseService implements FerieninselStammdatenService {

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public FerieninselStammdaten saveFerieninselStammdaten(@Nonnull FerieninselStammdaten ferieninselStammdaten) {
		Objects.requireNonNull(ferieninselStammdaten);
		return persistence.merge(ferieninselStammdaten);
	}

	@Nonnull
	@Override
	public Optional<FerieninselStammdaten> findFerieninselStammdaten(@Nonnull String ferieninselStammdatenId) {
		Objects.requireNonNull(ferieninselStammdatenId, "ferieninselStammdatenId muss gesetzt sein");
		FerieninselStammdaten ferieninselStammdaten = persistence.find(FerieninselStammdaten.class, ferieninselStammdatenId);
		return Optional.ofNullable(ferieninselStammdaten);
	}

	@Nonnull
	@Override
	public Collection<FerieninselStammdaten> getAllFerieninselStammdaten() {
		return criteriaQueryHelper.getAll(FerieninselStammdaten.class);
	}

	@Nonnull
	@Override
	public Collection<FerieninselStammdaten> findFerieninselStammdatenForGesuchsperiode(@Nonnull String gesuchsperiodeId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<FerieninselStammdaten> query = cb.createQuery(FerieninselStammdaten.class);
		Root<FerieninselStammdaten> root = query.from(FerieninselStammdaten.class);
		query.select(root);
		Predicate predicateGesuchsperiode = cb.equal(root.get(FerieninselStammdaten_.gesuchsperiode).get(Gesuchsperiode_.id), gesuchsperiodeId);
		query.where(predicateGesuchsperiode);
		query.orderBy(cb.asc(root.get(FerieninselStammdaten_.ferienname)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	public void removeFerieninselStammdaten(@Nonnull String ferieninselStammdatenId) {
		Objects.requireNonNull(ferieninselStammdatenId, "ferieninselStammdatenId muss gesetzt sein");
		persistence.remove(FerieninselStammdaten.class, ferieninselStammdatenId);
	}
}
