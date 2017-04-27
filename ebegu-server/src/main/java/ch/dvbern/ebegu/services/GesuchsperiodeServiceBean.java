package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.AbstractDateRangedEntity_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode_;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(GesuchsperiodeServiceBean.class.getSimpleName());

	@Inject
	private Persistence<Gesuchsperiode> persistence;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private GesuchService gesuchService;

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		Objects.requireNonNull(gesuchsperiode);
		return persistence.merge(gesuchsperiode);
	}


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	@SuppressWarnings("PMD.CollapsibleIfStatements")
	public Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode, @Nonnull GesuchsperiodeStatus statusBisher) {
		if (gesuchsperiode.isNew() && !GesuchsperiodeStatus.ENTWURF.equals(gesuchsperiode.getStatus())) {
			// Gesuchsperiode muss im Status ENTWURF erstellt werden
			throw new EbeguRuntimeException("saveGesuchsperiode", ErrorCodeEnum.ERROR_GESUCHSPERIODE_INVALID_STATUSUEBERGANG);
		}
		// Überprüfen, ob der Statusübergang zulässig ist
		if (!gesuchsperiode.getStatus().equals(statusBisher)) {
			// Superadmin darf alles
			if (!principalBean.isCallerInRole(UserRole.SUPER_ADMIN)) {
				if (!isStatusUebergangValid(statusBisher, gesuchsperiode.getStatus())) {
					throw new EbeguRuntimeException("saveGesuchsperiode", ErrorCodeEnum.ERROR_GESUCHSPERIODE_INVALID_STATUSUEBERGANG);
				}
						// Falls es ein Statuswechsel war, und der neue Status ist AKTIV -> Mail an alle Gesuchsteller schicken
						// Nur wenn als JA-Admin. Superadmin kann die Periode auch "wiederöffnen", dann darf aber kein Mail mehr verschickt werden!
				if (GesuchsperiodeStatus.AKTIV.equals(gesuchsperiode.getStatus())) {
					// TODO (team): Mail schicken an Gesuchsteller
					LOGGER.debug("Gesuchsperiode wurde aktiv gesetzt: " + gesuchsperiode.getGesuchsperiodeString() + ": Gesuchsteller informieren");
				}
				if (GesuchsperiodeStatus.GESCHLOSSEN.equals(gesuchsperiode.getStatus())) {
					// Prüfen, dass ALLE Gesuche dieser Periode im Status "Verfügt" oder "Schulamt" sind. Sind noch
					// Gesuce in Bearbeitung, oder in Beschwerde etc. darf nicht geschlossen werden!
					if (!gesuchService.canGesuchsperiodeBeClosed(gesuchsperiode)) {
						throw new EbeguRuntimeException("saveGesuchsperiode", ErrorCodeEnum.ERROR_GESUCHSPERIODE_CANNOT_BE_CLOSED);
					}
				}
			}
		}
		return saveGesuchsperiode(gesuchsperiode);
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

	private boolean isStatusUebergangValid(GesuchsperiodeStatus statusBefore, GesuchsperiodeStatus statusAfter) {
		if (GesuchsperiodeStatus.ENTWURF.equals(statusBefore)) {
			return GesuchsperiodeStatus.AKTIV.equals(statusAfter);
		} else if (GesuchsperiodeStatus.AKTIV.equals(statusBefore)) {
			return GesuchsperiodeStatus.INAKTIV.equals(statusAfter);
		} else if (GesuchsperiodeStatus.INAKTIV.equals(statusBefore)) {
			return GesuchsperiodeStatus.GESCHLOSSEN.equals(statusAfter);
		} else {
			return false;
		}
	}
}
