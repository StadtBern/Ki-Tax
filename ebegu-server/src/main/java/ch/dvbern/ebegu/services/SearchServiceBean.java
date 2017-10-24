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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import ch.dvbern.ebegu.dto.suchfilter.smarttable.AntragTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.PredicateObjectDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuung_;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Fall_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode_;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer_;
import ch.dvbern.ebegu.entities.Gesuchsteller_;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdaten_;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.KindContainer_;
import ch.dvbern.ebegu.entities.Kind_;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service zum Suchen.
 */
@Stateless
@Local(SearchService.class)
@PermitAll
public class SearchServiceBean extends AbstractBaseService implements SearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SearchServiceBean.class.getSimpleName());

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private Persistence persistence;


	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Pair<Long, List<Gesuch>> searchPendenzen(@Nonnull AntragTableFilterDTO antragTableFilterDto) {
		return countAndSearchAntraege(antragTableFilterDto, true);
	}

	@Override
	@PermitAll
	public Pair<Long, List<Gesuch>> searchAllAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto) {
		return countAndSearchAntraege(antragTableFilterDto, false);
	}

	@Nonnull
	private Pair<Long, List<Gesuch>> countAndSearchAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto, boolean searchForPendenzen) {
		Pair<Long, List<Gesuch>> result;
		Long countResult = searchAntraege(antragTableFilterDto, Mode.COUNT, searchForPendenzen).getLeft();
		if (countResult.equals(0L)) {    // no result found
			result = new ImmutablePair<>(0L, Collections.emptyList());
		} else {
			Pair<Long, List<Gesuch>> searchResult = searchAntraege(antragTableFilterDto, Mode.SEARCH, searchForPendenzen);
			result = new ImmutablePair<>(countResult, searchResult.getRight());
		}
		return result;
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private Pair<Long, List<Gesuch>> searchAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto, @Nonnull Mode mode, boolean searchForPendenzen) {
		Benutzer user = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("searchAllAntraege", "No User is logged in"));
		UserRole role = user.getRole();
		Set<AntragStatus> allowedAntragStatus;
		if (searchForPendenzen) {
			allowedAntragStatus = AntragStatus.pendenzenForRole(role);
		} else {
			allowedAntragStatus = AntragStatus.allowedforRole(role);
		}
		if (allowedAntragStatus.isEmpty()) {
			return new ImmutablePair<>(0L, Collections.emptyList());
		}

		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		@SuppressWarnings("rawtypes") // Je nach Abfrage ist es String oder Long
			CriteriaQuery query;
		switch (mode) {
		case SEARCH:
			query = cb.createQuery(String.class);
			break;
		case COUNT:
			query = cb.createQuery(Long.class);
			break;
		default:
			throw new IllegalStateException("Undefined Mode for searchAllAntraege Query: " + mode);
		}
		// Construct from-clause
		@SuppressWarnings("unchecked") // Je nach Abfrage ist das Query String oder Long
		Root<Gesuch> root = query.from(Gesuch.class);
		// Join all the relevant relations (except gesuchsteller join, which is only done when needed)
		Join<Gesuch, Fall> fall = root.join(Gesuch_.fall, JoinType.INNER);
		Join<Fall, Benutzer> verantwortlicher = fall.join(Fall_.verantwortlicher, JoinType.LEFT);
		Join<Gesuch, Gesuchsperiode> gesuchsperiode = root.join(Gesuch_.gesuchsperiode, JoinType.INNER);

		SetJoin<Gesuch, KindContainer> kindContainers = root.join(Gesuch_.kindContainers, JoinType.LEFT);
		SetJoin<KindContainer, Betreuung> betreuungen = kindContainers.join(KindContainer_.betreuungen, JoinType.LEFT);
		Join<KindContainer, Kind> kinder = kindContainers.join(KindContainer_.kindJA, JoinType.LEFT);
		Join<Betreuung, InstitutionStammdaten> institutionstammdaten = betreuungen.join(Betreuung_.institutionStammdaten, JoinType.LEFT);
		Join<InstitutionStammdaten, Institution> institution = institutionstammdaten.join(InstitutionStammdaten_.institution, JoinType.LEFT);

		//prepare predicates
		List<Predicate> predicates = new ArrayList<>();

		// General role based predicates
		Predicate inClauseStatus = root.get(Gesuch_.status).in(allowedAntragStatus);
		predicates.add(inClauseStatus);

		// Special role based predicates
		switch (role) {
		case SUPER_ADMIN:
		case ADMIN:
		case REVISOR:
		case JURIST:
			break;
		case STEUERAMT:
			break;
		case SACHBEARBEITER_JA:
			// Jugendamt-Mitarbeiter duerfen auch Faelle sehen, die noch gar keine Kinder/Betreuungen haben.
			// Wenn aber solche erfasst sind, dann duerfen sie nur diejenigen sehen, die nicht nur Schulamt haben
			// zudem muss auch der status ensprechend sein
			Predicate predicateKeineKinder = kindContainers.isNull();
			Predicate predicateKeineBetreuungen = betreuungen.isNull();
			Predicate predicateKeineInstitutionsstammdaten = institutionstammdaten.isNull();
			Predicate predicateKeineInstitution = institution.isNull();
			Predicate predicateAngebotstyp = cb.notEqual(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.TAGESSCHULE);
			Predicate predicateRichtigerAngebotstypOderNichtAusgefuellt = cb.or(predicateKeineKinder, predicateKeineBetreuungen, predicateKeineInstitutionsstammdaten, predicateKeineInstitution, predicateAngebotstyp);
			predicates.add(predicateRichtigerAngebotstypOderNichtAusgefuellt);
			break;
		case SACHBEARBEITER_TRAEGERSCHAFT:
			predicates.add(cb.equal(institution.get(Institution_.traegerschaft), user.getTraegerschaft()));
			break;
		case SACHBEARBEITER_INSTITUTION:
			// es geht hier nicht um die institution des zugewiesenen benutzers sondern um die institution des eingeloggten benutzers
			predicates.add(cb.equal(institution, user.getInstitution()));
			break;
		case SCHULAMT:
		case ADMINISTRATOR_SCHULAMT:
			predicates.add(cb.equal(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.TAGESSCHULE));
			break;
		default:
			LOG.warn("antragSearch can not be performed by users in role {}", role);
			predicates.add(cb.isFalse(cb.literal(Boolean.TRUE))); // impossible predicate
			break;
		}

		// Predicates derived from PredicateDTO
		PredicateObjectDTO predicateObjectDto = antragTableFilterDto.getSearch().getPredicateObject();
		if (predicateObjectDto != null) {
			if (predicateObjectDto.getFallNummer() != null) {
				// Die Fallnummer muss als String mit LIKE verglichen werden: Bei Eingabe von "14" soll der Fall "114" kommen
				Expression<String> fallNummerAsString = fall.get(Fall_.fallNummer).as(String.class);
				String fallNummerWithWildcards = '%' + predicateObjectDto.getFallNummer() + '%';
				predicates.add(cb.like(fallNummerAsString, fallNummerWithWildcards));
			}
			if (predicateObjectDto.getFamilienName() != null) {
				Join<Gesuch, GesuchstellerContainer> gesuchsteller1 = root.join(Gesuch_.gesuchsteller1, JoinType.LEFT);
				Join<Gesuch, GesuchstellerContainer> gesuchsteller2 = root.join(Gesuch_.gesuchsteller2, JoinType.LEFT);
				Join<GesuchstellerContainer, Gesuchsteller> gesuchsteller1JA = gesuchsteller1.join(GesuchstellerContainer_.gesuchstellerJA, JoinType.LEFT);
				Join<GesuchstellerContainer, Gesuchsteller> gesuchsteller2JA = gesuchsteller2.join(GesuchstellerContainer_.gesuchstellerJA, JoinType.LEFT);
				predicates.add(
					cb.or(
						cb.like(gesuchsteller1JA.get(Gesuchsteller_.nachname), predicateObjectDto.getFamilienNameForLike()),
						cb.like(gesuchsteller2JA.get(Gesuchsteller_.nachname), predicateObjectDto.getFamilienNameForLike())
					));
			}
			if (predicateObjectDto.getAntragTyp() != null) {
				List<AntragTyp> values = AntragTyp.getValuesForFilter(predicateObjectDto.getAntragTyp());
				predicates.add(root.get(Gesuch_.typ).in(values));
			}
			if (predicateObjectDto.getGesuchsperiodeString() != null) {
				String[] years = ensureYearFormat(predicateObjectDto.getGesuchsperiodeString());
				predicates.add(
					cb.and(
						cb.equal(cb.function("year", Integer.class, gesuchsperiode.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb)), years[0]),
						cb.equal(cb.function("year", Integer.class, gesuchsperiode.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigBis)), years[1]))
				);
			}
			if (predicateObjectDto.getEingangsdatum() != null) {
				try {
					LocalDate searchDate = LocalDate.parse(predicateObjectDto.getEingangsdatum(), Constants.DATE_FORMATTER);
					predicates.add(cb.equal(root.get(Gesuch_.eingangsdatum), searchDate));
				} catch (DateTimeParseException e) {
					// Kein gueltiges Datum. Es kann kein Gesuch geben, welches passt. Wir geben leer zurueck
					return new ImmutablePair<>(0L, Collections.emptyList());
				}
			}
			if (predicateObjectDto.getEingangsdatumSTV() != null) {
				try {
					LocalDate searchDate = LocalDate.parse(predicateObjectDto.getEingangsdatumSTV(), Constants.DATE_FORMATTER);
					predicates.add(cb.equal(root.get(Gesuch_.eingangsdatumSTV), searchDate));
				} catch (DateTimeParseException e) {
					// Kein gueltiges Datum. Es kann kein Gesuch geben, welches passt. Wir geben leer zurueck
					return new ImmutablePair<>(0L, Collections.emptyList());
				}
			}
			if (predicateObjectDto.getAenderungsdatum() != null) {
				try {
					// Wir wollen ohne Zeit vergleichen
					Expression<LocalDate> timestampAsLocalDate = root.get(Gesuch_.timestampMutiert).as(LocalDate.class);
					LocalDate searchDate = LocalDate.parse(predicateObjectDto.getAenderungsdatum(), Constants.DATE_FORMATTER);
					predicates.add(cb.equal(timestampAsLocalDate, searchDate));
				} catch (DateTimeParseException e) {
					// Kein gueltiges Datum. Es kann kein Gesuch geben, welches passt. Wir geben leer zurueck
					return new ImmutablePair<>(0L, Collections.emptyList());
				}
			}
			if (predicateObjectDto.getStatus() != null) {
				createPredicateGesuchBetreuungenStatus(cb, root, predicates, predicateObjectDto.getStatus());
				// Achtung, hier muss von Client zu Server Status konvertiert werden!
				Collection<AntragStatus> antragStatus = AntragStatusConverterUtil.convertStatusToEntityForRole(AntragStatusDTO.valueOf(predicateObjectDto.getStatus()), role);
				predicates.add(root.get(Gesuch_.status).in(antragStatus));
			}
			if (predicateObjectDto.getDokumenteHochgeladen() != null) {
				predicates.add(cb.equal(root.get(Gesuch_.dokumenteHochgeladen), predicateObjectDto.getDokumenteHochgeladen()));
			}
			if (predicateObjectDto.getAngebote() != null) {
				predicates.add(cb.equal(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.valueOf(predicateObjectDto.getAngebote())));
			}
			if (predicateObjectDto.getInstitutionen() != null) {
				predicates.add(cb.equal(institution.get(Institution_.name), predicateObjectDto.getInstitutionen()));
			}
			if (predicateObjectDto.getKinder() != null) {
				predicates.add(cb.like(kinder.get(Kind_.vorname), predicateObjectDto.getKindNameForLike()));
			}
			if (predicateObjectDto.getVerantwortlicher() != null) {
				String[] strings = predicateObjectDto.getVerantwortlicher().split(" ");
				predicates.add(
					cb.and(
						cb.equal(verantwortlicher.get(Benutzer_.vorname), strings[0]),
						cb.equal(verantwortlicher.get(Benutzer_.nachname), strings[1])
					));
			}
		}
		// Construct the select- and where-clause
		switch (mode) {
		case SEARCH:
			//noinspection unchecked // Je nach Abfrage ist das Query String oder Long
			query.select(root.get(Gesuch_.id))
				.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
			constructOrderByClause(antragTableFilterDto, cb, query, root, kinder, gesuchsperiode, institutionstammdaten, institution);
			break;
		case COUNT:
			//noinspection unchecked // Je nach Abfrage ist das Query String oder Long
			query.select(cb.countDistinct(root.get(Gesuch_.id)))
				.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
			break;
		}

		// Prepare and execute the query and build the result
		Pair<Long, List<Gesuch>> result = null;
		switch (mode) {
		case SEARCH:
			List<String> gesuchIds = persistence.getCriteriaResults(query); //select all ids in order, may contain duplicates
			List<Gesuch> pagedResult;
			if (antragTableFilterDto.getPagination() != null) {
				int firstIndex = antragTableFilterDto.getPagination().getStart();
				Integer maxresults = antragTableFilterDto.getPagination().getNumber();
				List<String> orderedIdsToLoad = this.determineDistinctGesuchIdsToLoad(gesuchIds, firstIndex, maxresults);
				pagedResult = findGesuche(orderedIdsToLoad);
			} else {
				pagedResult = findGesuche(gesuchIds);
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

	private List<String> determineDistinctGesuchIdsToLoad(List<String> allGesuchIds, int startindex, int maxresults) {
		List<String> uniqueGesuchIds = new ArrayList<>(new LinkedHashSet<>(allGesuchIds)); //keep order but remove duplicate ids
		int lastindex = Math.min(startindex + maxresults, (uniqueGesuchIds.size()));
		return uniqueGesuchIds.subList(startindex, lastindex);
	}

	/**
	 * Adds a predicate to the predicates list if it is needed to filter by gesuchBetreuungenStatus. This will be
	 * needed just when the Status is GEPRUEFT, PLATZBESTAETIGUNG_WARTEN or PLATZBESTAETIGUNG_ABGEWIESEN
	 */
	private void createPredicateGesuchBetreuungenStatus(CriteriaBuilder cb, Root<Gesuch> root, List<Predicate> predicates, String status) {
		if (AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN.toString().equalsIgnoreCase(status)) {
			predicates.add(cb.equal(root.get(Gesuch_.gesuchBetreuungenStatus), GesuchBetreuungenStatus.WARTEN));
		} else if (AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN.toString().equalsIgnoreCase(status)) {
			predicates.add(cb.equal(root.get(Gesuch_.gesuchBetreuungenStatus), GesuchBetreuungenStatus.ABGEWIESEN));
		} else if (AntragStatusDTO.GEPRUEFT.toString().equalsIgnoreCase(status)) {
			predicates.add(cb.equal(root.get(Gesuch_.gesuchBetreuungenStatus), GesuchBetreuungenStatus.ALLE_BESTAETIGT));
		}
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private void constructOrderByClause(@Nonnull AntragTableFilterDTO antragTableFilterDto, CriteriaBuilder cb, CriteriaQuery query,
		Root<Gesuch> root, Join<KindContainer, Kind> kinder,
		Join<Gesuch, Gesuchsperiode> gesuchsperiode,
		Join<Betreuung, InstitutionStammdaten> institutionstammdaten,
		Join<InstitutionStammdaten, Institution> institution) {
		Expression<?> expression;
		if (antragTableFilterDto.getSort() != null && antragTableFilterDto.getSort().getPredicate() != null) {
			switch (antragTableFilterDto.getSort().getPredicate()) {
			case "fallNummer":
				expression = root.get(Gesuch_.fall).get(Fall_.fallNummer);
				break;
			case "familienName":
				expression = root.get(Gesuch_.gesuchsteller1).get(GesuchstellerContainer_.gesuchstellerJA).get(Gesuchsteller_.nachname);
				break;
			case "antragTyp":
				expression = root.get(Gesuch_.typ);
				break;
			case "gesuchsperiode":
				expression = gesuchsperiode.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb);
				break;
			case "aenderungsdatum":
				expression = root.get(Gesuch_.timestampMutiert);
				break;
			case "eingangsdatum":
				expression = root.get(Gesuch_.eingangsdatum);
				break;
			case "eingangsdatumSTV":
				expression = root.get(Gesuch_.eingangsdatumSTV);
				break;
			case "status":
				expression = root.get(Gesuch_.status);
				break;
			case "angebote":
				// Die Angebote sind eigentlich eine Liste innerhalb der Liste (also des Tabelleneintrages).
				// Kinder ohne Angebot sollen egal wie sortiert ist am Schluss kommen!
				if (antragTableFilterDto.getSort().getReverse()) {
					expression = cb.selectCase().when(institutionstammdaten.isNull(), "ZZZZ")
						.otherwise(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp));
				} else {
					expression = cb.selectCase().when(institutionstammdaten.isNull(), "0000")
						.otherwise(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp));
				}
				break;
			case "institutionen":
				// Die Institutionen sind eigentlich eine Liste innerhalb der Liste (also des Tabelleneintrages).
				// Kinder ohne Angebot sollen egal wie sortiert ist am Schluss kommen!
				if (antragTableFilterDto.getSort().getReverse()) {
					expression = cb.selectCase().when(institution.isNull(), "ZZZZ")
						.otherwise(institution.get(Institution_.name));
				} else {
					expression = cb.selectCase().when(institution.isNull(), "0000")
						.otherwise(institution.get(Institution_.name));
				}
				break;
			case "verantwortlicher":
				expression = root.get(Gesuch_.fall).get(Fall_.verantwortlicher).get(Benutzer_.nachname);
				break;
			case "kinder":
				expression = kinder.get(Kind_.vorname);
				break;
			case "dokumenteHochgeladen":
				expression = root.get(Gesuch_.dokumenteHochgeladen);
				break;
			default:
				LOG.warn("Using default sort by FallNummer because there is no specific clause for predicate {}",
					antragTableFilterDto.getSort().getPredicate());
				expression = root.get(Gesuch_.fall).get(Fall_.fallNummer);
				break;
			}
			query.orderBy(antragTableFilterDto.getSort().getReverse() ? cb.asc(expression) : cb.desc(expression));
		} else {
			// Default sort when nothing is choosen
			expression = root.get(Gesuch_.timestampMutiert);
			query.orderBy(cb.desc(expression));
		}
	}

	private String[] ensureYearFormat(String gesuchsperiodeString) {
		String[] years = gesuchsperiodeString.split("/");
		if (years.length != 2) {
			throw new EbeguRuntimeException("searchAllAntraege", "Der Gesuchsperioden string war nicht im erwarteten Format x/y sondern " + gesuchsperiodeString);
		}
		String[] result = new String[2];
		result[0] = changeTwoDigitYearToFourDigit(years[0]);
		result[1] = changeTwoDigitYearToFourDigit(years[1]);
		return result;
	}

	private String changeTwoDigitYearToFourDigit(String year) {
		//im folgenden wandeln wir z.B 16  in 2016 um. Funktioniert bis ins jahr 2099, da die Periode 2099/2100 mit dieser Methode nicht geht
		String currentYearAsString = String.valueOf(LocalDate.now().getYear());
		if (year.length() == currentYearAsString.length()) {
			return year;
		}
		if (year.length() < currentYearAsString.length()) { // jahr ist im kurzformat
			return currentYearAsString.substring(0, currentYearAsString.length() - year.length()) + year;
		}
		throw new EbeguRuntimeException("searchAllAntraege", "Der Gesuchsperioden string war nicht im erwarteten Format yy oder yyyy sondern " + year);
	}

	private List<Gesuch> findGesuche(@Nonnull List<String> gesuchIds) {
		if (!gesuchIds.isEmpty()) {
			final CriteriaBuilder cb = persistence.getCriteriaBuilder();
			final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);
			Root<Gesuch> root = query.from(Gesuch.class);
			Predicate predicate = root.get(Gesuch_.id).in(gesuchIds);
			Fetch<Gesuch, KindContainer> kindContainers = root.fetch(Gesuch_.kindContainers, JoinType.LEFT);
			kindContainers.fetch(KindContainer_.betreuungen, JoinType.LEFT);
			query.where(predicate);
			//reduce to unique gesuche
			List<Gesuch> listWithDuplicates = persistence.getCriteriaResults(query);
			LinkedHashSet<Gesuch> set = new LinkedHashSet<>();
			//richtige reihenfolge beibehalten
			for (String gesuchId : gesuchIds) {
				listWithDuplicates.stream()
					.filter(gesuch -> gesuch.getId().equals(gesuchId))
					.findFirst()
					.ifPresent(set::add);
			}
			return new ArrayList<>(set);
		}
		return Collections.emptyList();
	}

	private enum Mode {
		COUNT,
		SEARCH
	}
}
