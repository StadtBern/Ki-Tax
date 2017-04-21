package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AbstractDateRangedEntity_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode_;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Gesuchsperiode
 */
@Stateless
@Local(GesuchsperiodeService.class)
@PermitAll
public class GesuchsperiodeServiceBean extends AbstractBaseService implements GesuchsperiodeService {

	@Inject
	private Persistence<Gesuchsperiode> persistence;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		Objects.requireNonNull(gesuchsperiode);
		return persistence.merge(gesuchsperiode);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<Gesuchsperiode> findGesuchsperiode(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Gesuchsperiode gesuchsperiode = persistence.find(Gesuchsperiode.class, key);
		return Optional.ofNullable(gesuchsperiode);
	}

	@Nonnull
	@Override
	@PermitAll
	public Collection<Gesuchsperiode> getAllGesuchsperioden() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuchsperiode> query = cb.createQuery(Gesuchsperiode.class);
		Root<Gesuchsperiode> root = query.from(Gesuchsperiode.class);
		query.select(root);
		query.orderBy(cb.desc(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb)));
		return persistence.getCriteriaResults(query);

	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public void removeGesuchsperiode(@Nonnull String gesuchsperiodeId) {
		Objects.requireNonNull(gesuchsperiodeId);
		Optional<Gesuchsperiode> gesuchsperiodeToRemove = findGesuchsperiode(gesuchsperiodeId);
		gesuchsperiodeToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeGesuchsperiode", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsperiodeId));
		persistence.remove(gesuchsperiodeToRemove.get());
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Gesuchsperiode> getAllActiveGesuchsperioden() {
		return getGesuchsperiodenImStatus(GesuchsperiodeStatus.AKTIV);
	}

	/**
	 * @return all Gesuchsperioden that have a gueltigkeitBis Date that is in the future (compared to the current date)
	 */
	@Override
	@Nonnull
	@PermitAll
	public Collection<Gesuchsperiode> getAllNichtAbgeschlosseneGesuchsperioden() {
		return getGesuchsperiodenImStatus(GesuchsperiodeStatus.AKTIV, GesuchsperiodeStatus.INAKTIV);
	}

	private Collection<Gesuchsperiode> getGesuchsperiodenImStatus(GesuchsperiodeStatus... status) {
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuchsperiode> query = builder.createQuery(Gesuchsperiode.class);
		final Root<Gesuchsperiode> root = query.from(Gesuchsperiode.class);
		query.where(root.get(Gesuchsperiode_.status).in(status));
		query.orderBy(builder.desc(root.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nonnull
	@PermitAll
	public Optional<Gesuchsperiode> getGesuchsperiodeAm(@Nonnull LocalDate stichtag) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuchsperiode> query = cb.createQuery(Gesuchsperiode.class);
		Root<Gesuchsperiode> root = query.from(Gesuchsperiode.class);

		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), stichtag);
		Predicate predicateEnd = cb.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), stichtag);

		query.where(predicateStart, predicateEnd);
		Gesuchsperiode criteriaSingleResult = persistence.getCriteriaSingleResult(query);
		return Optional.ofNullable(criteriaSingleResult);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Gesuchsperiode> getGesuchsperiodenBetween(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuchsperiode> query = cb.createQuery(Gesuchsperiode.class);
		Root<Gesuchsperiode> root = query.from(Gesuchsperiode.class);

		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), datumBis);
		Predicate predicateEnd = cb.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), datumVon);

		query.where(predicateStart, predicateEnd);
		return persistence.getCriteriaResults(query);
	}
}
