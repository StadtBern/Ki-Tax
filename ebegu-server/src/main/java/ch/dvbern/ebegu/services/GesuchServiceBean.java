package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.PredicateObjectDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Service fuer Gesuch
 */
@Stateless
@Local(GesuchService.class)
public class GesuchServiceBean extends AbstractBaseService implements GesuchService {

	private static final Logger LOG = LoggerFactory.getLogger(GesuchServiceBean.class.getSimpleName());

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private BenutzerService benutzerService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;

	@Nonnull
	@Override
	public Gesuch createGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		final Gesuch persistedGesuch = persistence.persist(gesuch);
		// Die WizsrdSteps werden direkt erstellt wenn das Gesuch erstellt wird. So vergewissern wir uns dass es kein Gesuch ohne WizardSteps gibt
		wizardStepService.createWizardStepList(persistedGesuch);
		antragStatusHistoryService.saveStatusChange(persistedGesuch);
		return persistedGesuch;
	}

	@Nonnull
	@Override
	public Gesuch updateGesuch(@Nonnull Gesuch gesuch, boolean saveInStatusHistory) {
		Objects.requireNonNull(gesuch);
		final Gesuch merged = persistence.merge(gesuch);
		if (saveInStatusHistory) {
			antragStatusHistoryService.saveStatusChange(merged);
		}
		return merged;
	}

	@Nonnull
	@Override
	public Optional<Gesuch> findGesuch(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Gesuch a = persistence.find(Gesuch.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Gesuch> getAllGesuche() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Gesuch.class));
	}

	@Nonnull
	@Override
	public Collection<Gesuch> getAllActiveGesuche() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);

		Predicate predicateGesuch = cb.notEqual(root.get(Gesuch_.status), AntragStatus.VERFUEGT);
		query.where(predicateGesuch);
		return persistence.getCriteriaResults(query);
	}

	@Override
	public void removeGesuch(@Nonnull Gesuch gesuch) {
		Validate.notNull(gesuch);
		Optional<Gesuch> gesuchToRemove = findGesuch(gesuch.getId());
		gesuchToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuch));
		persistence.remove(gesuchToRemove.get());
	}

	@Nonnull
	@Override
	public Optional<List<Gesuch>> findGesuchByGSName(String nachname, String vorname) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);

		ParameterExpression<String> nameParam = cb.parameter(String.class, "nachname");
		Predicate namePredicate = cb.equal(root.get(Gesuch_.gesuchsteller1).get(Gesuchsteller_.nachname), nameParam);

		ParameterExpression<String> vornameParam = cb.parameter(String.class, "vorname");
		Predicate vornamePredicate = cb.equal(root.get(Gesuch_.gesuchsteller1).get(Gesuchsteller_.vorname), vornameParam);

		query.where(namePredicate, vornamePredicate);
		TypedQuery<Gesuch> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(nameParam, nachname);
		q.setParameter(vornameParam, vorname);

		return Optional.ofNullable(q.getResultList());
	}

	@Override
	public Pair<Long, List<Gesuch>> searchAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto) {
		Pair<Long, List<Gesuch>> result;
		Long countResult = searchAntraege(antragTableFilterDto, Mode.COUNT).getLeft();
		if (countResult.equals(Long.valueOf(0))) {    // no result found
			result = new ImmutablePair<>(0L, Collections.emptyList());
		} else {
			Pair<Long, List<Gesuch>> searchResult = searchAntraege(antragTableFilterDto, Mode.SEARCH);
			result = new ImmutablePair<>(countResult, searchResult.getRight());
		}
		return result;
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private Pair<Long, List<Gesuch>> searchAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto, @Nonnull Mode mode) {
		Benutzer user = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("searchAntraege", "No User is logged in"));
		UserRole role = user.getRole();
		Set<AntragStatus> allowedAntragStatus = AntragStatus.allowedforRole(role);
		if (allowedAntragStatus.isEmpty()) {
			return new ImmutablePair<>(0L, Collections.emptyList());
		}

		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaQuery query = null;
		switch (mode) {
			case SEARCH:
				query = cb.createQuery(String.class);
				break;
			case COUNT:
				query = cb.createQuery(Long.class);
				break;
			default:
				throw new IllegalStateException("Undefined Mode for searchAntraege Query: " + mode);
		}
		// Construct from-clause
		Root<Gesuch> root = query.from(Gesuch.class);
		// Join all the relevant relations
		Join<Gesuch, Fall> fall = root.join(Gesuch_.fall, JoinType.INNER);
		Join<Fall, Benutzer> benutzer = fall.join(Fall_.verantwortlicher, JoinType.LEFT);
		Join<Gesuch, Gesuchsperiode> gesuchsperiode = root.join(Gesuch_.gesuchsperiode, JoinType.INNER);
		Join<Gesuch, Gesuchsteller> gesuchsteller1 = root.join(Gesuch_.gesuchsteller1, JoinType.LEFT);
		Join<Gesuch, Gesuchsteller> gesuchsteller2 = root.join(Gesuch_.gesuchsteller2, JoinType.LEFT);
		SetJoin<Gesuch, KindContainer> kindContainers = root.join(Gesuch_.kindContainers, JoinType.LEFT);
		SetJoin<KindContainer, Betreuung> betreuungen = kindContainers.join(KindContainer_.betreuungen, JoinType.LEFT);
		Join<Betreuung, InstitutionStammdaten> institutionstammdaten = betreuungen.join(Betreuung_.institutionStammdaten, JoinType.LEFT);
		Join<InstitutionStammdaten, Institution> institution = institutionstammdaten.join(InstitutionStammdaten_.institution, JoinType.LEFT);

		//prepare predicates
		List<Expression<Boolean>> predicates = new ArrayList<>();

		// General role based predicates
		Predicate inClauseStatus = root.get(Gesuch_.status).in(allowedAntragStatus);
		predicates.add(inClauseStatus);

		// Special role based predicates
		switch (role) {
			case ADMIN:
			case REVISOR:
				break;
			case SACHBEARBEITER_JA:
			case JURIST:
				// Jugendamt-Mitarbeiter duerfen auch Faelle sehen, die noch gar keine Kinder/Betreuungen haben.
				// Wenn aber solche erfasst sind, dann duerfen sie nur diejenigen sehen, die nicht nur Schulamt haben
				Predicate predicateKeineKinder = kindContainers.isNull();
				Predicate predicateKeineBetreuungen = betreuungen.isNull();
				Predicate predicateKeineInstitutionsstammdaten = institutionstammdaten.isNull();
				Predicate predicateKeineInstitution = institution.isNull();
				Predicate predicateAngebotstyp = cb.notEqual(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.TAGESSCHULE);
				Predicate predicateRichtigerAngebotstypOderNichtAusgefuellt = cb.or(predicateKeineKinder, predicateKeineBetreuungen, predicateKeineInstitutionsstammdaten, predicateKeineInstitution, predicateAngebotstyp);
				predicates.add(predicateRichtigerAngebotstypOderNichtAusgefuellt);
				break;
			case SACHBEARBEITER_TRAEGERSCHAFT:
				predicates.add(cb.equal(benutzer.get(Benutzer_.traegerschaft), institution.get(Institution_.traegerschaft)));
				break;
			case SACHBEARBEITER_INSTITUTION:
				predicates.add(cb.equal(benutzer.get(Benutzer_.institution), institution));
				break;
			case SCHULAMT:
				predicates.add(cb.equal(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.TAGESSCHULE));
				break;
			default:
				LOG.warn("antragSearch can not be performed by users in role " + role);
				predicates.add(cb.isFalse(cb.literal(Boolean.TRUE))); // impossible predicate
				break;
		}

		// Predicates derived from PredicateDTO
		PredicateObjectDTO predicateObjectDto = antragTableFilterDto.getSearch().getPredicateObject();
		if (predicateObjectDto != null) {
			if (predicateObjectDto.getFallNummer() != null) {
				predicates.add(cb.equal(fall.get(Fall_.fallNummer), Integer.valueOf(predicateObjectDto.readFallNummerAsNumber())));
			}
			if (predicateObjectDto.getFamilienName() != null) {
				predicates.add(
					cb.or(
						cb.like(gesuchsteller1.get(Gesuchsteller_.nachname), predicateObjectDto.getFamilienNameForLike()),
						cb.like(gesuchsteller2.get(Gesuchsteller_.nachname), predicateObjectDto.getFamilienNameForLike())
					));
			}
			if (predicateObjectDto.getAntragTyp() != null) {
				predicates.add(cb.equal(root.get(Gesuch_.typ), AntragTyp.valueOf(predicateObjectDto.getAntragTyp())));
			}
			if (predicateObjectDto.getGesuchsperiodeString() != null) {
				String[] years = predicateObjectDto.getGesuchsperiodeString().split("/");
				if (years.length != 2) {
					throw new EbeguRuntimeException("searchAntraege", "Der Gesuchsperioden string war nicht im erwarteten Format x/y sondern " + predicateObjectDto.getGesuchsperiodeString());
				}
				predicates.add(
					cb.and(
						cb.equal(cb.function("year", Integer.class, gesuchsperiode.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb)), years[0]),
						cb.equal(cb.function("year", Integer.class, gesuchsperiode.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigBis)), years[1]))
				);
			}
			if (predicateObjectDto.getEingangsdatum() != null) {
				predicates.add(cb.equal(root.get(Gesuch_.eingangsdatum), LocalDate.parse(predicateObjectDto.getEingangsdatum(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
			}
			if (predicateObjectDto.getStatus() != null) {
				predicates.add(cb.equal(root.get(Gesuch_.status), AntragStatus.valueOf(predicateObjectDto.getStatus())));
			}
			if (predicateObjectDto.getAngebote() != null) {
				predicates.add(cb.equal(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.valueOf(predicateObjectDto.getAngebote())));
			}
			if (predicateObjectDto.getInstitutionen() != null) {
				predicates.add(cb.equal(institution.get(Institution_.name), predicateObjectDto.getInstitutionen()));
			}
			if (predicateObjectDto.getVerantwortlicher() != null) {
				String[] strings = predicateObjectDto.getVerantwortlicher().split(" ");
				predicates.add(
					cb.and(
						cb.equal(benutzer.get(Benutzer_.vorname), strings[0]),
						cb.equal(benutzer.get(Benutzer_.nachname), strings[1])
					));
			}
		}
		// Construct the select- and where-clause
		switch (mode) {
			case SEARCH:
				query.select(root.get(Gesuch_.id))
					.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
				constructOrderByClause(antragTableFilterDto, cb, query, root, betreuungen);
				break;
			case COUNT:
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

	private void constructOrderByClause(@Nonnull AntragTableFilterDTO antragTableFilterDto, CriteriaBuilder cb, CriteriaQuery query, Root<Gesuch> root, SetJoin<KindContainer, Betreuung> betreuungen) {
		if (antragTableFilterDto.getSort() != null && antragTableFilterDto.getSort().getPredicate() != null) {
			Expression<?> expression;
			switch (antragTableFilterDto.getSort().getPredicate()) {
				case "fallNummer":
					expression = root.get(Gesuch_.fall).get(Fall_.fallNummer);
					break;
				case "familienName":
					expression = root.get(Gesuch_.gesuchsteller1).get(Gesuchsteller_.nachname);
					break;
				case "antragTyp":
					expression = root.get(Gesuch_.typ);
					break;
				case "gesuchsperiode":
					expression = root.get(Gesuch_.gesuchsperiode).get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb);
					break;
				case "aenderungsdatum":
					expression = root.get(Gesuch_.timestampMutiert);
					break;
				case "eingangsdatum":
					expression = root.get(Gesuch_.eingangsdatum);
					break;
				case "status":
					expression = root.get(Gesuch_.status);
					break;
				case "angebote":
					expression = betreuungen.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp);
					break;
				case "institutionen":
					expression = betreuungen.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).get(Institution_.name);
					break;
				case "verantwortlicher":
					expression = root.get(Gesuch_.fall).get(Fall_.verantwortlicher);
					break;
				default:
					LOG.warn("Using default sort by FallNummer because there is no specific clause for predicate " + antragTableFilterDto.getSort().getPredicate());
					expression = root.get(Gesuch_.fall).get(Fall_.fallNummer);
					break;
			}
			query.orderBy(antragTableFilterDto.getSort().getReverse() ? cb.asc(expression) : cb.desc(expression));
		}
	}

	private enum Mode {
		COUNT,
		SEARCH;
	}

	@Nonnull
	@Override
	public List<JaxAntragDTO> getAllAntragDTOForFall(String fallId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<JaxAntragDTO> query = cb.createQuery(JaxAntragDTO.class);
		Root<Gesuch> root = query.from(Gesuch.class);
		query.multiselect(
			root.get(Gesuch_.id),
			root.get(Gesuch_.gesuchsperiode).get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(Gesuch_.gesuchsperiode).get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigBis),
			root.get(Gesuch_.eingangsdatum),
			root.get(Gesuch_.typ),
			root.get(Gesuch_.status)).distinct(true);

		ParameterExpression<String> dateParam = cb.parameter(String.class, "fallId");
		Predicate predicate = cb.equal(root.get(Gesuch_.fall).get(AbstractEntity_.id), dateParam);

		query.where(predicate);
		TypedQuery<JaxAntragDTO> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateParam, fallId);
		query.orderBy(cb.asc(root.get(Gesuch_.gesuchsperiode).get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb)));
		return q.getResultList();
	}

	@Override
	@Nonnull
	public Optional<Gesuch> antragMutieren(@Nonnull String antragId, @Nonnull Mutationsdaten mutationsdaten,
										   @Nonnull LocalDate eingangsdatum) {
		// Mutiert wird immer das Gesuch mit dem letzten Verfügungsdatum

		Optional<Gesuch> gesuch = findGesuch(antragId);
		if (gesuch.isPresent()) {

			Optional<Gesuch> gesuchForMutation = getNeustesVerfuegtesGesuchFuerGesuch(gesuch.get());

			if (gesuchForMutation.isPresent()) {
				Gesuch mutation = new Gesuch(gesuchForMutation.get());
				mutation.setEingangsdatum(eingangsdatum);
				mutation.setMutationsdaten(mutationsdaten);
				mutation.setStatus(AntragStatus.IN_BEARBEITUNG_JA); // todo im gesuch online darf dies auch IN_BEARBEITUNG_GS sein
				return Optional.of(mutation);
			}
		}
		return Optional.empty();
	}

	@Override
	@Nonnull
	public Optional<Gesuch> getNeustesVerfuegtesGesuchFuerGesuch(Gesuch gesuch) {
		// TODO (team): Diese methode macht, was sie sagt, evt. waere es aber sicherer, das *vorgänger*gesuch zu suchen? Könnte über die neue vorgängerId gemacht werden
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);
		Join<Gesuch, AntragStatusHistory> join = root.join(Gesuch_.antragStatusHistories, JoinType.INNER);

		Predicate predicateStatus = cb.equal(root.get(Gesuch_.status), AntragStatus.VERFUEGT);
		Predicate predicateGesuchsperiode = cb.equal(root.get(Gesuch_.gesuchsperiode), gesuch.getGesuchsperiode());
		Predicate predicateAntragStatus = cb.equal(join.get(AntragStatusHistory_.status), AntragStatus.VERFUEGT);
		Predicate predicateFall = cb.equal(root.get(Gesuch_.fall), gesuch.getFall());

		query.where(predicateStatus, predicateGesuchsperiode, predicateAntragStatus, predicateFall);
		query.select(root);
		query.orderBy(cb.desc(join.get(AntragStatusHistory_.datum))); // Das mit dem neuesten Verfuegungsdatum
		List<Gesuch> criteriaResults = persistence.getCriteriaResults(query, 1);
		if (criteriaResults.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(criteriaResults.get(0));
	}

	private List<String> determineDistinctGesuchIdsToLoad(List<String> allGesuchIds, int startindex, int maxresults) {
		List<String> uniqueGesuchIds = new ArrayList<>(new LinkedHashSet<>(allGesuchIds)); //keep order but remove duplicate ids
		int lastindex = Math.min(startindex + maxresults, (uniqueGesuchIds.size()));
		return uniqueGesuchIds.subList(startindex, lastindex);
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

		} else {
			return Collections.emptyList();
		}
	}
}


