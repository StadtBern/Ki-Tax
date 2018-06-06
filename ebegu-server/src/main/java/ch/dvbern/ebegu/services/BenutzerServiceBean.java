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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.entities.BerechtigungHistory;
import ch.dvbern.ebegu.entities.BerechtigungHistory_;
import ch.dvbern.ebegu.entities.Berechtigung_;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.entities.Traegerschaft_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.SearchMode;
import ch.dvbern.ebegu.enums.UserRole;
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

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

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
		if (benutzer.isNew()) {
			return persistence.persist(benutzer);
		} else {
			prepareBenutzerForSave(benutzer);
			return persistence.merge(benutzer);
		}
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
		Join<Benutzer, Berechtigung> joinBerechtigungen = root.join(Benutzer_.berechtigungen);
		query.select(root);

		Predicate predicateActive = cb.between(cb.literal(LocalDate.now()),
			joinBerechtigungen.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb),
			joinBerechtigungen.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis));
		Predicate predicateRole = joinBerechtigungen.get(Berechtigung_.role).in(roles);
		query.where(predicateActive, predicateRole);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN })
	public Collection<Benutzer> getGesuchsteller() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);
		Root<Benutzer> root = query.from(Benutzer.class);
		Join<Benutzer, Berechtigung> joinBerechtigungen = root.join(Benutzer_.berechtigungen);
		query.select(root);

		Predicate predicateActive = cb.between(cb.literal(LocalDate.now()),
			joinBerechtigungen.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb),
			joinBerechtigungen.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis));
		Predicate predicateRole = joinBerechtigungen.get(Berechtigung_.role).in(UserRole.GESUCHSTELLER);
		query.where(predicateActive, predicateRole);
		query.orderBy(cb.asc(root.get(Benutzer_.username)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public void removeBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username);
		Benutzer benutzer = findBenutzer(username).orElseThrow(() -> new EbeguEntityNotFoundException("removeBenutzer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));
		// Den Benutzer ausloggen und seine AuthBenutzer loeschen
		authService.logoutAndDeleteAuthorisierteBenutzerForUser(username);
		removeBerechtigungHistoryForBenutzer(benutzer);
		persistence.remove(benutzer);
	}

	private void removeBerechtigungHistoryForBenutzer(@Nonnull Benutzer benutzer) {
		Collection<BerechtigungHistory> histories = getBerechtigungHistoriesForBenutzer(benutzer);
		for (BerechtigungHistory history : histories) {
			persistence.remove(history);
		}
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
			// den username ueberschreiben wir nicht!
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
			benutzer.getBerechtigungen().clear();
			benutzer.getBerechtigungen().add(berechtigung);
			return saveBenutzer(benutzer);
		}
	}

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public Benutzer sperren(@Nonnull String username) {
		Benutzer benutzerFromDB = findBenutzer(username).orElseThrow(()
			-> new EbeguEntityNotFoundException("sperren", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + username));

		benutzerFromDB.setGesperrt(Boolean.TRUE);
		int deletedAuthBenutzer = authService.logoutAndDeleteAuthorisierteBenutzerForUser(username);
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

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public Benutzer reaktivieren(@Nonnull String username) {
		Benutzer benutzerFromDB = findBenutzer(username).orElseThrow(()
			-> new EbeguEntityNotFoundException("reaktivieren", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + username));
		benutzerFromDB.setGesperrt(Boolean.FALSE);
		logReaktivierenBenutzer(benutzerFromDB);
		return persistence.merge(benutzerFromDB);
	}

	private void logReaktivierenBenutzer(Benutzer benutzerFromDB) {
		StringBuilder sb = new StringBuilder();
		sb.append("Reaktiviere Benutzer: ").append(benutzerFromDB.getUsername());
		sb.append(" / ");
		sb.append("Eingeloggt: ").append(principalBean.getBenutzer().getUsername());
		LOG.info(sb.toString());
	}

	private void prepareBenutzerForSave(@Nonnull Benutzer benutzer) {
		List<Berechtigung> sorted = new LinkedList<>();
		sorted.addAll(benutzer.getBerechtigungen());
		sorted.sort(Comparator.comparing(o -> o.getGueltigkeit().getGueltigAb()));

		Berechtigung currentBerechtigung = sorted.get(0);
		Berechtigung futureBerechtigung = null;
		if (sorted.size() > 1) {
			futureBerechtigung = sorted.get(1);
		}
		if (futureBerechtigung != null) {
			// Die aktuelle Berechtigung per Startdatum der zukünftigen beenden
			currentBerechtigung.getGueltigkeit().setGueltigBis(futureBerechtigung.getGueltigkeit().getGueltigAb().minusDays(1));
		} else {
			// Wenn keine zukünftige Berechtigung: Sicherstellen, dass Gueltigkeit unendlich
			currentBerechtigung.getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		}
		for (Berechtigung berechtigung : sorted) {
			prepareBerechtigungForSave(berechtigung);
		}
		authService.logoutAndDeleteAuthorisierteBenutzerForUser(benutzer.getUsername());
	}

	private void prepareBerechtigungForSave(@Nonnull Berechtigung berechtigung) {
		// Es darf nur eine Institution gesetzt sein, wenn die Rolle INSTITUTION ist
		if (berechtigung.getRole() != UserRole.SACHBEARBEITER_INSTITUTION) {
			berechtigung.setInstitution(null);
		}
		// Es darf nur eine Trägerschaft gesetzt sein, wenn die Rolle TRAEGERSCHAFT ist
		if (berechtigung.getRole() != UserRole.SACHBEARBEITER_TRAEGERSCHAFT) {
			berechtigung.setTraegerschaft(null);
		}
	}

	private Optional<Benutzer> loadSuperAdmin() {
		Benutzer benutzer = persistence.find(Benutzer.class, ID_SUPER_ADMIN);
		return Optional.ofNullable(benutzer);
	}

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
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
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	private Pair<Long, List<Benutzer>> searchBenutzer(@Nonnull BenutzerTableFilterDTO benutzerTableFilterDTO, @Nonnull SearchMode mode) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		@SuppressWarnings("rawtypes") // Je nach Abfrage ist es String oder Long
		CriteriaQuery query = SearchUtil.getQueryForSearchMode(cb, mode, "searchBenutzer");

		// Construct from-clause
		@SuppressWarnings("unchecked") // Je nach Abfrage ist das Query String oder Long
		Root<Benutzer> root = query.from(Benutzer.class);
		Join<Benutzer, Berechtigung> currentBerechtigung = root.join(Benutzer_.berechtigungen);
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
			//reduce to unique Benutzer
			List<Benutzer> listWithDuplicates = persistence.getCriteriaResults(query);
			LinkedHashSet<Benutzer> setOfBenutzer = new LinkedHashSet<>();
			//richtige reihenfolge beibehalten
			for (String userId : benutzerIds) {
				listWithDuplicates.stream()
					.filter(benutzer -> benutzer.getId().equals(userId))
					.findFirst()
					.ifPresent(setOfBenutzer::add);
			}
			return new ArrayList<>(setOfBenutzer);
		}
		return Collections.emptyList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RolesAllowed(SUPER_ADMIN)
	public int handleAbgelaufeneRollen(@Nonnull LocalDate stichtag) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);
		Root<Benutzer> root = query.from(Benutzer.class);
		Join<Benutzer, Berechtigung> currentBerechtigung = root.join(Benutzer_.berechtigungen);
		Predicate predicateAbgelaufen = cb.lessThan(currentBerechtigung.get(Berechtigung_.gueltigkeit).get(DateRange_.gueltigBis), stichtag);
		query.where(predicateAbgelaufen);
		List<Benutzer> userMitAbgelaufenerRolle = persistence.getCriteriaResults(query);

		for (Benutzer benutzer : userMitAbgelaufenerRolle) {
			List<Berechtigung> abgelaufeneBerechtigungen = new ArrayList<>();
			for (Berechtigung berechtigung : benutzer.getBerechtigungen()) {
				if (berechtigung.isAbgelaufen()) {
					abgelaufeneBerechtigungen.add(berechtigung);
				}
			}
			try {
				Berechtigung aktuelleBerechtigung = getAktuellGueltigeBerechtigungFuerBenutzer(benutzer);
				persistence.merge(aktuelleBerechtigung);
			} catch(NoResultException nre) {
				// Sonderfall: Die letzte Berechtigung ist abgelaufen. Wir erstellen sofort eine neue anschliessende Berechtigung als Gesuchsteller
				Berechtigung futureGesuchstellerBerechtigung = createFutureBerechtigungAsGesuchsteller(LocalDate.now(), benutzer);
				persistence.persist(futureGesuchstellerBerechtigung);
			}
			// Die abgelaufene Rolle löschen
			for (Berechtigung abgelaufeneBerechtigung : abgelaufeneBerechtigungen) {
				LOG.info("Benutzerrolle ist abgelaufen: {}, war: {}, abgelaufen: {}", benutzer.getUsername(),
					benutzer.getRole(), abgelaufeneBerechtigung.getGueltigkeit().getGueltigBis());
				benutzer.getBerechtigungen().remove(abgelaufeneBerechtigung);
				persistence.merge(benutzer);
				removeBerechtigung(abgelaufeneBerechtigung);
			}

		}
		return userMitAbgelaufenerRolle.size();
	}

	private Berechtigung createFutureBerechtigungAsGesuchsteller(LocalDate startDatum, Benutzer benutzer) {
		Berechtigung futureGesuchstellerBerechtigung = new Berechtigung();
		futureGesuchstellerBerechtigung.getGueltigkeit().setGueltigAb(startDatum);
		futureGesuchstellerBerechtigung.getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);
		futureGesuchstellerBerechtigung.setRole(UserRole.GESUCHSTELLER);
		futureGesuchstellerBerechtigung.setBenutzer(benutzer);
		return futureGesuchstellerBerechtigung;
	}

	@Nonnull
	private Berechtigung getAktuellGueltigeBerechtigungFuerBenutzer(@Nonnull Benutzer benutzer) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Berechtigung> query = cb.createQuery(Berechtigung.class);
		Root<Berechtigung> root = query.from(Berechtigung.class);

		ParameterExpression<Benutzer> benutzerParam = cb.parameter(Benutzer.class, "benutzer");
		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");

		Predicate predicateBenutzer = cb.equal(root.get(Berechtigung_.benutzer), benutzerParam);
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
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public Optional<Berechtigung> findBerechtigung(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		return Optional.ofNullable(persistence.find(Berechtigung.class, id));
	}

	private void removeBerechtigung(@Nonnull Berechtigung berechtigung) {
		authService.logoutAndDeleteAuthorisierteBenutzerForUser(berechtigung.getBenutzer().getUsername());
		persistence.remove(berechtigung);
	}

	@Override
	@PermitAll
	public void saveBerechtigungHistory(@Nonnull Berechtigung berechtigung, boolean deleted) {
		BerechtigungHistory newBerechtigungsHistory = new BerechtigungHistory(berechtigung, deleted);
		newBerechtigungsHistory.setTimestampErstellt(LocalDateTime.now());
		String userMutiert = berechtigung.getUserMutiert() != null ? berechtigung.getUserMutiert() : Constants.SYSTEM_USER_USERNAME;
		newBerechtigungsHistory.setUserErstellt(userMutiert);
		persistence.persist(newBerechtigungsHistory);
	}

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public Collection<BerechtigungHistory> getBerechtigungHistoriesForBenutzer(@Nonnull Benutzer benutzer) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<BerechtigungHistory> query = cb.createQuery(BerechtigungHistory.class);
		Root<BerechtigungHistory> root = query.from(BerechtigungHistory.class);

		ParameterExpression<String> benutzerParam = cb.parameter(String.class, "username");
		Predicate predicateBenutzer = cb.equal(root.get(BerechtigungHistory_.username), benutzerParam);
		query.orderBy(cb.desc(root.get(BerechtigungHistory_.timestampErstellt)));
		query.where(predicateBenutzer);

		TypedQuery<BerechtigungHistory> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(benutzerParam, benutzer.getUsername());
		return q.getResultList();
	}
}
