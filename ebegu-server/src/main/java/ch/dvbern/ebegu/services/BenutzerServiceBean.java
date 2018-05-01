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

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.BenutzerPredicateObjectDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.BenutzerTableFilterDTO;
import ch.dvbern.ebegu.entities.AbstractDateRangedEntity_;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer_;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.entities.Berechtigung_;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.entities.Traegerschaft_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.SearchMode;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.util.SearchUtil;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service fuer Benutzer
 */
@PermitAll
@Stateless
@Local(BenutzerService.class)
public class BenutzerServiceBean extends AbstractBaseService implements BenutzerService {

	private static final Logger LOG = LoggerFactory.getLogger(BenutzerServiceBean.class.getSimpleName());

	public static final String ID_SUPER_ADMIN = "22222222-2222-2222-2222-222222222222";

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private AuthService authService;

	@Nonnull
	@Override
	@PermitAll
	public Benutzer saveBenutzer(@Nonnull Benutzer benutzer) {
		Objects.requireNonNull(benutzer);
		clearBenutzerObject(benutzer);
		benutzer.getCurrentBerechtigung().setBenutzer(benutzer);
		Berechtigung berechtigungMerged = saveBerechtigung(benutzer, benutzer.getCurrentBerechtigung());
		benutzer.setCurrentBerechtigung(berechtigungMerged);
		return persistence.merge(benutzer);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<Benutzer> findBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username, "username muss gesetzt sein");
		return criteriaQueryHelper.getEntityByUniqueAttribute(Benutzer.class, username, Benutzer_.username);
	}

	@Nonnull
	@Override
	@PermitAll
	public Collection<Benutzer> getAllBenutzer() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Benutzer.class));
	}

	@Nonnull
	@Override
	@PermitAll
	public Collection<Benutzer> getBenutzerJAorAdmin() {
		return getBenutzersOfRoles(UserRole.getJugendamtRoles());
	}

	@Nonnull
	@Override
	@PermitAll
	public Collection<Benutzer> getBenutzerSCHorAdminSCH() {
		return getBenutzersOfRoles(UserRole.getSchulamtRoles());
	}

	private Collection<Benutzer> getBenutzersOfRoles(List<UserRole> roles) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);
		Root<Benutzer> root = query.from(Benutzer.class);
		query.select(root);
		final Predicate role = root.get(Benutzer_.currentBerechtigung).get(Berechtigung_.role).in(roles);
		query.where(role);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public Collection<Benutzer> getGesuchsteller() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);
		Root<Benutzer> root = query.from(Benutzer.class);
		query.select(root);
		Predicate isGesuchsteller = cb.equal(root.get(Benutzer_.currentBerechtigung).get(Berechtigung_.role), UserRole.GESUCHSTELLER);
		query.where(isGesuchsteller);
		query.orderBy(cb.asc(root.get(Benutzer_.username)));

		return persistence.getCriteriaResults(query);
	}

	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public void removeBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username);
		Benutzer benutzer = findBenutzer(username).orElseThrow(() -> new EbeguEntityNotFoundException("removeBenutzer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));
		// Den Benutzer ausloggen und seine AuthBenutzer loeschen
		logoutAndDeleteAuthorisierteBenutzerForUser(username);
		// Die Berechtigungen des Benutzers loeschen
		List<Berechtigung> berechtigungenForBenutzer = getBerechtigungenForBenutzer(benutzer.getUsername());
		for (Berechtigung berechtigung : berechtigungenForBenutzer) {
			//TODO (hefr) History
			persistence.remove(berechtigung);
		}
		persistence.remove(benutzer);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<Benutzer> getCurrentBenutzer() {
		String username = null;
		if (principalBean != null) {
			final Principal principal = principalBean.getPrincipal();
			username = principal.getName();
		}
		if (StringUtils.isNotEmpty(username)) {
			if ("anonymous".equals(username) && principalBean.isCallerInRole(UserRole.SUPER_ADMIN.name())) {
				return loadSuperAdmin();
			}
			return findBenutzer(username);
		}
		return Optional.empty();
	}

	@Override
	@PermitAll
	public Benutzer updateOrStoreUserFromIAM(@Nonnull Benutzer benutzer) {
		Optional<Benutzer> foundUserOptional = this.findBenutzer(benutzer.getUsername());
		if (foundUserOptional.isPresent()) {
			// Wir kennen den Benutzer schon: Es werden nur die readonly-Attribute neu von IAM uebernommen
			Benutzer foundUser = foundUserOptional.get();
			foundUser.setUsername(benutzer.getUsername());
			foundUser.setNachname(benutzer.getNachname());
			foundUser.setVorname(benutzer.getVorname());
			foundUser.setEmail(benutzer.getEmail());
			return saveBenutzer(foundUser);
		} else {
			// Wir kennen den Benutzer noch nicht: Wir uebernehmen alles, setzen aber grundsätzlich die Rolle auf GESUCHSTELLER
			Berechtigung berechtigung = new Berechtigung();
			berechtigung.setRole(UserRole.GESUCHSTELLER);
			berechtigung.setInstitution(null);
			berechtigung.setTraegerschaft(null);
			berechtigung.setBenutzer(benutzer);
			benutzer.setCurrentBerechtigung(berechtigung);
			return saveBenutzer(benutzer);
		}
	}

	@Nonnull
	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public Benutzer sperren(@Nonnull String username) {
		Benutzer benutzerFromDB = findBenutzer(username).orElseThrow(()
			-> new EbeguEntityNotFoundException("sperren", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + username));

		benutzerFromDB.setGesperrt(Boolean.TRUE);
		int deletedAuthBenutzer = logoutAndDeleteAuthorisierteBenutzerForUser(username);
		//TODO (hefr) History
		logSperreBenutzer(benutzerFromDB, deletedAuthBenutzer);
		return persistence.merge(benutzerFromDB);
	}

	private void logSperreBenutzer(@Nonnull Benutzer benutzer, int deletedAuthBenutzer) {
		StringBuilder sb = new StringBuilder();
		sb.append("Setze Benutzer auf GESPERRT: ").append(benutzer.getUsername());
		sb.append(" / ");
		sb.append("Eingeloggt: ").append(principalBean.getBenutzer().getUsername());
		sb.append(" / ");
		sb.append("Lösche ").append(deletedAuthBenutzer).append(" Eintraege aus der AuthorisierteBenutzer Tabelle");
		LOG.info(sb.toString());
	}

	private int logoutAndDeleteAuthorisierteBenutzerForUser(@Nonnull String username) {
		Collection<AuthorisierterBenutzer> authUsers = criteriaQueryHelper.getEntitiesByAttribute(AuthorisierterBenutzer.class, username, AuthorisierterBenutzer_.username);
		for (AuthorisierterBenutzer authUser : authUsers) {
			// Den Benutzer ausloggen und den AuthentifiziertenBenutzer löschen
			authService.logout(authUser.getAuthToken());
		}
		return authUsers.size();
	}

	@Nonnull
	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public Benutzer reaktivieren(@Nonnull String username) {
		Benutzer benutzerFromDB = findBenutzer(username).orElseThrow(()
			-> new EbeguEntityNotFoundException("reaktivieren", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + username));
		benutzerFromDB.setGesperrt(Boolean.FALSE);
		logReaktivierenBenutzer(benutzerFromDB);
		//TODO (hefr) History
		return persistence.merge(benutzerFromDB);
	}

	private void logReaktivierenBenutzer(Benutzer benutzerFromDB) {
		StringBuilder sb = new StringBuilder();
		sb.append("Reaktiviere Benutzer: ").append(benutzerFromDB.getUsername());
		sb.append(" / ");
		sb.append("Eingeloggt: ").append(principalBean.getBenutzer().getUsername());
		LOG.info(sb.toString());
	}

	@Nonnull
	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public List<Berechtigung> getBerechtigungenForBenutzer(@Nonnull String username) {
		Benutzer benutzer = findBenutzer(username).orElseThrow(() -> new EbeguEntityNotFoundException("getBerechtigungenForBenutzer",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Berechtigung> query = cb.createQuery(Berechtigung.class);
		Root<Berechtigung> root = query.from(Berechtigung.class);
		query.select(root);

		Predicate predicateBenutzer = cb.equal(root.get(Berechtigung_.benutzer), benutzer);
		Predicate predicateAktivOderZukuenftig = cb.greaterThanOrEqualTo(root.get(Berechtigung_.gueltigkeit).get(DateRange_.gueltigBis), LocalDate.now());
		query.where(predicateBenutzer, predicateAktivOderZukuenftig);
		query.orderBy(cb.asc(root.get(Berechtigung_.gueltigkeit).get(DateRange_.gueltigAb)));
		return persistence.getCriteriaResults(query);
	}


	private void clearBenutzerObject(@Nonnull Benutzer benutzer) {
		// Es darf nur eine Institution gesetzt sein, wenn die Rolle INSTITUTION ist
		if (benutzer.getRole() != UserRole.SACHBEARBEITER_INSTITUTION) {
			benutzer.setInstitution(null);
		}
		// Es darf nur eine Trägerschaft gesetzt sein, wenn die Rolle TRAEGERSCHAFT ist
		if (benutzer.getRole() != UserRole.SACHBEARBEITER_TRAEGERSCHAFT) {
			benutzer.setTraegerschaft(null);
		}
		// Das Datum gueltigBis sollte bei Rolle GESUCHSTELLER nicht gesetzt werden
		if (benutzer.getRole() == UserRole.GESUCHSTELLER) {
			benutzer.getCurrentBerechtigung().getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		}
	}

	private Optional<Benutzer> loadSuperAdmin() {
		Benutzer benutzer = persistence.find(Benutzer.class, ID_SUPER_ADMIN);
		return Optional.ofNullable(benutzer);
	}

	@Nonnull
	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public Pair<Long, List<Benutzer>> searchBenutzer(@Nonnull BenutzerTableFilterDTO benutzerTableFilterDto) {
		Pair<Long, List<Benutzer>> result;
		Long countResult = searchBenutzer(benutzerTableFilterDto, SearchMode.COUNT).getLeft();
		if (countResult.equals(0L)) {    // no result found
			result = new ImmutablePair<>(0L, Collections.emptyList());
		} else {
			Pair<Long, List<Benutzer>> searchResult = searchBenutzer(benutzerTableFilterDto, SearchMode.SEARCH);
			result = new ImmutablePair<>(countResult, searchResult.getRight());
		}
		return result;
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	private Pair<Long, List<Benutzer>> searchBenutzer(@Nonnull BenutzerTableFilterDTO benutzerTableFilterDTO, @Nonnull SearchMode mode) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		@SuppressWarnings("rawtypes") // Je nach Abfrage ist es String oder Long
		CriteriaQuery query = SearchUtil.getQueryForSearchMode(cb, mode, "searchBenutzer");

		// Construct from-clause
		@SuppressWarnings("unchecked") // Je nach Abfrage ist das Query String oder Long
		Root<Benutzer> root = query.from(Benutzer.class);
		Join<Benutzer, Berechtigung> currentBerechtigung = root.join(Benutzer_.currentBerechtigung);
		Join<Berechtigung, Institution> institution = currentBerechtigung.join(Berechtigung_.institution, JoinType.LEFT);
		Join<Berechtigung, Traegerschaft> traegerschaft = currentBerechtigung.join(Berechtigung_.traegerschaft, JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();

		// General role based predicates
		Benutzer user = getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("searchBenutzer", "No User is logged in"));

		if (!principalBean.isCallerInRole(UserRole.SUPER_ADMIN)) {
			// Admins duerfen alle Benutzer ihres Mandanten sehen
			predicates.add(cb.equal(root.get(Benutzer_.mandant), user.getMandant()));
			// Und sie duerfen keine Superadmins sehen
			predicates.add(cb.notEqual(currentBerechtigung.get(Berechtigung_.role), UserRole.SUPER_ADMIN));
		}

		//prepare predicates
		BenutzerPredicateObjectDTO predicateObjectDto = benutzerTableFilterDTO.getSearch().getPredicateObject();
		if (predicateObjectDto != null) {
			// username
			if (predicateObjectDto.getUsername() != null) {
				Expression<String> expression = root.get(Benutzer_.username).as(String.class);
				String value = SearchUtil.withWildcards(predicateObjectDto.getUsername());
				predicates.add(cb.like(expression, value));
			}
			// vorname;
			if (predicateObjectDto.getVorname() != null) {
				Expression<String> expression = root.get(Benutzer_.vorname).as(String.class);
				String value = SearchUtil.withWildcards(predicateObjectDto.getVorname());
				predicates.add(cb.like(expression, value));
			}
			// nachname;
			if (predicateObjectDto.getNachname() != null) {
				Expression<String> expression = root.get(Benutzer_.nachname).as(String.class);
				String value = SearchUtil.withWildcards(predicateObjectDto.getNachname());
				predicates.add(cb.like(expression, value));
			}
			// email;
			if (predicateObjectDto.getEmail() != null) {
				Expression<String> expression = root.get(Benutzer_.email).as(String.class);
				String value = SearchUtil.withWildcards(predicateObjectDto.getEmail());
				predicates.add(cb.like(expression, value));
			}
			// role;
			if (predicateObjectDto.getRole() != null) {
				predicates.add(cb.equal(currentBerechtigung.get(Berechtigung_.role), UserRole.valueOf(predicateObjectDto.getRole())));
			}
			// roleGueltigBis;
			if (predicateObjectDto.getRoleGueltigBis() != null) {
				try {
					LocalDate searchDate = LocalDate.parse(predicateObjectDto.getRoleGueltigBis(), Constants.DATE_FORMATTER);
					predicates.add(cb.equal(currentBerechtigung.get(Berechtigung_.gueltigkeit).get(DateRange_.gueltigBis), searchDate));
				} catch (DateTimeParseException e) {
					// Kein gueltiges Datum. Es kann kein Gesuch geben, welches passt. Wir geben leer zurueck
					return new ImmutablePair<>(0L, Collections.emptyList());
				}
			}
			// institution;
			if (predicateObjectDto.getInstitution() != null) {
				predicates.add(cb.equal(institution.get(Institution_.name), predicateObjectDto.getInstitution()));
			}
			// traegerschaft;
			if (predicateObjectDto.getTraegerschaft() != null) {
				predicates.add(cb.equal(traegerschaft.get(Traegerschaft_.name), predicateObjectDto.getTraegerschaft()));
			}
			// gesperrt;
			if (predicateObjectDto.getGesperrt() != null) {
				predicates.add(cb.equal(root.get(Benutzer_.gesperrt), predicateObjectDto.getGesperrt()));
			}
		}
		// Construct the select- and where-clause
		switch (mode) {
		case SEARCH:
			//noinspection unchecked // Je nach Abfrage ist das Query String oder Long
			query.select(root.get(Benutzer_.id));
			if (!predicates.isEmpty()) {
				query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
			}
			constructOrderByClause(benutzerTableFilterDTO, cb, query, root, currentBerechtigung, institution, traegerschaft);
			break;
		case COUNT:
			//noinspection unchecked // Je nach Abfrage ist das Query String oder Long
			query.select(cb.countDistinct(root.get(Benutzer_.id)));
			if (!predicates.isEmpty()) {
				query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
			}
			break;
		}

		// Prepare and execute the query and build the result
		Pair<Long, List<Benutzer>> result = null;
		switch (mode) {
		case SEARCH:
			List<String> benutzerIds = persistence.getCriteriaResults(query); //select all ids in order, may contain duplicates
			List<Benutzer> pagedResult;
			if (benutzerTableFilterDTO.getPagination() != null) {
				int firstIndex = benutzerTableFilterDTO.getPagination().getStart();
				Integer maxresults = benutzerTableFilterDTO.getPagination().getNumber();
				List<String> orderedIdsToLoad = SearchUtil.determineDistinctIdsToLoad(benutzerIds, firstIndex, maxresults);
				pagedResult = findBenutzer(orderedIdsToLoad);
			} else {
				pagedResult = findBenutzer(benutzerIds);
			}
			result = new ImmutablePair<>(null, pagedResult);
			break;
		case COUNT:
			Long count = (Long) persistence.getCriteriaSingleResult(query);
			result = new ImmutablePair<>(count, null);
			break;
		}
		return result;
	}

	private void constructOrderByClause(@Nonnull BenutzerTableFilterDTO benutzerTableFilterDto, CriteriaBuilder cb, CriteriaQuery query,
			Root<Benutzer> root,
			Join<Benutzer, Berechtigung> currentBerechtigung,
			Join<Berechtigung, Institution> institution,
			Join<Berechtigung, Traegerschaft> traegerschaft) {
		Expression<?> expression;
		if (benutzerTableFilterDto.getSort() != null && benutzerTableFilterDto.getSort().getPredicate() != null) {
			switch (benutzerTableFilterDto.getSort().getPredicate()) {
			case "username":
				expression = root.get(Benutzer_.username);
				break;
			case "vorname":
				expression = root.get(Benutzer_.vorname);
				break;
			case "nachname":
				expression = root.get(Benutzer_.nachname);
				break;
			case "email":
				expression = root.get(Benutzer_.email);
				break;
			case "role":
				expression = currentBerechtigung.get(Berechtigung_.role);
				break;
			case "roleGueltigBis":
				expression = currentBerechtigung.get(Berechtigung_.gueltigkeit).get(DateRange_.gueltigBis);
				break;
			case "institution":
				expression = institution.get(Institution_.name);
				break;
			case "traegerschaft":
				expression = traegerschaft.get(Traegerschaft_.name);
				break;
			case "gesperrt":
				expression = root.get(Benutzer_.gesperrt);
				break;
			default:
				LOG.warn("Using default sort by Timestamp mutiert because there is no specific clause for predicate {}",
					benutzerTableFilterDto.getSort().getPredicate());
				expression = root.get(Benutzer_.timestampMutiert);
				break;
			}
			query.orderBy(benutzerTableFilterDto.getSort().getReverse() ? cb.asc(expression) : cb.desc(expression));
		} else {
			// Default sort when nothing is choosen
			expression = root.get(Benutzer_.timestampMutiert);
			query.orderBy(cb.desc(expression));
		}
	}

	private List<Benutzer> findBenutzer(@Nonnull List<String> benutzerIds) {
		if (!benutzerIds.isEmpty()) {
			final CriteriaBuilder cb = persistence.getCriteriaBuilder();
			final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);
			Root<Benutzer> root = query.from(Benutzer.class);
			Predicate predicate = root.get(Benutzer_.id).in(benutzerIds);
			query.where(predicate);
			//reduce to unique benutzer
			return persistence.getCriteriaResults(query);
		}
		return Collections.emptyList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RolesAllowed(UserRoleName.SUPER_ADMIN)
	public int handleAbgelaufeneRollen(@Nonnull LocalDate stichtag) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);

		Root<Benutzer> root = query.from(Benutzer.class);
		Join<Benutzer, Berechtigung> currentBerechtigung = root.join(Benutzer_.currentBerechtigung);
		Predicate predicateAbgelaufen = cb.lessThan(currentBerechtigung.get(Berechtigung_.gueltigkeit).get(DateRange_.gueltigBis), stichtag);
		query.where(predicateAbgelaufen);
		List<Benutzer> userMitAbgelaufenerRolle = persistence.getCriteriaResults(query);

		for (Benutzer benutzer : userMitAbgelaufenerRolle) {
			LOG.info("Benutzerrolle ist abgelaufen: {}, war: {}, abgelaufen: {}", benutzer.getUsername(),
				benutzer.getRole(), benutzer.getCurrentBerechtigung().getGueltigkeit().getGueltigBis());
			// Die abgelaufene Rolle löschen
			persistence.remove(benutzer.getCurrentBerechtigung());
			// Die aktuell gueltige Rolle suchen und neu umhängen
			Berechtigung aktuelleBerechtigung = getAktuellGueltigeBerechtigungFuerBenutzer(benutzer);
			benutzer.setCurrentBerechtigung(aktuelleBerechtigung);
		}
		return userMitAbgelaufenerRolle.size();
	}

	@Nonnull
	private Berechtigung getAktuellGueltigeBerechtigungFuerBenutzer(@Nonnull Benutzer benutzer) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Berechtigung> query = cb.createQuery(Berechtigung.class);
		Root<Berechtigung> root = query.from(Berechtigung.class);

		ParameterExpression<Benutzer> benutzerParam = cb.parameter(Benutzer.class, "benutzer");
		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");

		Predicate predicateBenutzer = cb.equal(root.get(Berechtigung_.id), benutzerParam);
		Predicate predicateZeitraum = cb.between(dateParam,
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis));

		query.where(predicateBenutzer, predicateZeitraum);

		TypedQuery<Berechtigung> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateParam, LocalDate.now());
		q.setParameter(benutzerParam, benutzer);
		List<Berechtigung> resultList = q.getResultList();

		if (resultList.isEmpty()) {
			throw new NoResultException("No Berechtigung found for Benutzer" + benutzer.getUsername());
		}
		if (resultList.size() > 1) {
			throw new NonUniqueResultException("More than one Berechtigung found for Benutzer " + benutzer.getUsername());
		}
		return resultList.get(0);
	}

	@Nonnull
	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public Optional<Berechtigung> findBerechtigung(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		return Optional.ofNullable(persistence.find(Berechtigung.class, id));
	}

	@Nonnull
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	private Berechtigung saveBerechtigung(@Nonnull Benutzer benutzer, @Nonnull Berechtigung berechtigung) {
		Objects.requireNonNull(benutzer);
		Objects.requireNonNull(berechtigung);

		boolean roleChanged = false;
		boolean institutionChanged = false;
		boolean traegerschaftChanged = false;
		LocalDate gueltigAb = berechtigung.getGueltigkeit().getGueltigAb();
		boolean isFuture = gueltigAb.isAfter(LocalDate.now());

		if (!berechtigung.isNew()) {
			// Ueberpruefen, was geaendert hat
			Berechtigung berechtigungFromDB = findBerechtigung(berechtigung.getId()).orElseThrow(()
				-> new EbeguEntityNotFoundException("saveBerechtigung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "BerechtigungID invalid: " + berechtigung.getId()));
			// Feststellen, was alles geändert hat
			roleChanged = berechtigungFromDB.getRole() != berechtigung.getRole();
			institutionChanged = roleChanged && (berechtigungFromDB.getInstitution() != null
				&& !berechtigungFromDB.getInstitution().equals(berechtigung.getInstitution()));
			traegerschaftChanged = roleChanged && (berechtigungFromDB.getTraegerschaft() != null
				&& !berechtigungFromDB.getTraegerschaft().equals(berechtigung.getTraegerschaft()));
		} else {
			roleChanged = true;
			institutionChanged = true;
			traegerschaftChanged = true;
		}

		// Ausloggen nur, wenn die Änderungen nicht in der Zukunft liegen! Falls dies der Fall ist, wird der Timer das ausloggen übernehmen
		if (!isFuture && (institutionChanged || traegerschaftChanged || roleChanged)) {
			// Die AuthorisiertenBenutzer müssen gelöscht werden
			logoutAndDeleteAuthorisierteBenutzerForUser(benutzer.getUsername());
		}

		// History-Eintrag nur, wenn etwas geaendert hat
		if (institutionChanged || traegerschaftChanged || roleChanged) {
			// TODO (hefr) History
		}
		berechtigung.setBenutzer(benutzer);
		return persistence.merge(berechtigung);
	}

	private void removeBerechtigung(@Nonnull Berechtigung berechtigung) {
		logoutAndDeleteAuthorisierteBenutzerForUser(berechtigung.getBenutzer().getUsername());
		// TODO (hefr) History
		persistence.remove(berechtigung);
	}

	@Override
	@RolesAllowed({ UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN })
	public void saveBerechtigungen(@Nonnull Benutzer benutzer, @Nonnull List<Berechtigung> berechtigungen) {
		Objects.requireNonNull(benutzer);

		// Die Gueltigkeiten richtig setzen: Jeweils den Vorgaenger am Vortag der nächsten Berechtigung
		// beenden.
		Berechtigung vorgaenger = null;
		// Die Berechtigungen sind aufsteigend sortiert
		for (Berechtigung berechtigung : berechtigungen) {
			if (vorgaenger == null) {
				vorgaenger = berechtigung;
				// Solange es kein "nächstes" gibt, ist die Berechtigung unendlich gueltig
				vorgaenger.getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
			} else {
				vorgaenger.getGueltigkeit().setGueltigBis(berechtigung.getGueltigkeit().getGueltigAb().minusDays(1));
			}
		}
		// Mit DB abgleichen
		List<Berechtigung> berechtigungenFromDB = getBerechtigungenForBenutzer(benutzer.getUsername());
		Set<Berechtigung> berechtigungenToMerge = new HashSet<>(berechtigungenFromDB);
		Set<Berechtigung> berechtigungenToRemove = new HashSet<>(berechtigungenFromDB);
		Set<Berechtigung> berechtigungenToPersist = new HashSet<>(berechtigungen);
		// Die aktuelle vorsichtshalber auch noch hinzufügen (es ist ja ein Set)
		berechtigungenToPersist.add(benutzer.getCurrentBerechtigung());
		berechtigungenToMerge.retainAll(berechtigungenToPersist);
		berechtigungenToRemove.removeAll(berechtigungenToMerge);
		berechtigungenToPersist.removeAll(berechtigungenToMerge);

		for (Berechtigung berechtigung: berechtigungenToRemove) {
			removeBerechtigung(berechtigung);
		}
		for (Berechtigung berechtigung: berechtigungenToPersist) {
			saveBerechtigung(benutzer, berechtigung);
		}
		for (Berechtigung berechtigung: berechtigungenToMerge) {
			saveBerechtigung(benutzer, berechtigung);
		}
	}
}
