package ch.dvbern.ebegu.services;

import static ch.dvbern.ebegu.entities.AbstractAntragEntity_.status;

import java.util.*;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.AntragSortDTO;
import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.PredicateObjectDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service fuer Gesuch
 */
@Stateless
@Local(GesuchService.class)
public class GesuchServiceBean extends AbstractBaseService implements GesuchService {

	private final Logger LOG = LoggerFactory.getLogger(GesuchServiceBean.class.getSimpleName());

	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private BenutzerService benutzerService;


	@Nonnull
	@Override
	public Gesuch createGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		return persistence.persist(gesuch);
	}

	@Nonnull
	@Override
	public Gesuch updateGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		return persistence.merge(gesuch);
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

		Predicate predicateGesuch = cb.notEqual(root.get(status), AntragStatus.VERFUEGT);
		query.where(predicateGesuch);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	@Override
	public void removeGesuch(@Nonnull Gesuch gesuch) {
		Validate.notNull(gesuch);
		Optional<Gesuch> gesuchToRemove = findGesuch(gesuch.getId());
		gesuchToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuch));
		persistence.remove(gesuchToRemove.get());
	}

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
	public Pair<Long, List<Gesuch>> searchAntraege(AntragTableFilterDTO antragSearch) {

		// Rolle lesen: benutzerService.getCurrentBenutzer().get().getRole()
		benutzerService.getCurrentBenutzer().get().getRole();
		// mit Rolle gelesene Gesuche auf gewisse Status einschranken Gesuch.status
		Long count = runCountQuery(antragSearch);
		//Todo team? Suchquery implementieren, allenfalls mit einem wrapper objekt damit die performance besser ist
		// dann brauchts wohl 2 queries um jeweils noch die listen zu lesen
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);

		Root<Gesuch> root = query.from(Gesuch.class);

		if (antragSearch.getSort() != null && antragSearch.getSort().getPredicate() != null) {
			query.orderBy(createOrderClause(cb, root, antragSearch.getSort()));
		}

		TypedQuery<Gesuch> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setFirstResult(antragSearch.getPagination().getStart());
		typedQuery.setMaxResults(antragSearch.getPagination().getNumber());


		List<Gesuch> gesuche = typedQuery.getResultList();
		//Es waere hier evtl besser das query direkt so zu schreiben dass das DTO erstellt wird statt zuerst noch das gesuch
		//Darum habe ich das DTO hier auch verfuegbar gemacht
		return new ImmutablePair<Long, List<Gesuch>>(count, gesuche);
	}

	private Long runCountQuery(AntragTableFilterDTO antragSearch) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Gesuch> root = query.from(Gesuch.class);
		//order ist bei count egal
		query.select(cb.count(root));
		antragSearch.getSearch(); //	aus diesen angaben query.where() gleiche restriktionen wie oben, createWehereClause() oder so implementieren
		return persistence.getCriteriaSingleResult(query);

	}

	private List<Order> createOrderClause(CriteriaBuilder cb, Root<Gesuch> root, AntragSortDTO sort) {
		Expression orderField= root.get(Gesuch_.fall).get(Fall_.fallNummer);
		switch (sort.getPredicate()) {
			case "fallNummer":
				orderField = root.get(Gesuch_.fall).get(Fall_.fallNummer);
				break;
			case "familienName":
				orderField = root.get(Gesuch_.gesuchsteller1).get(Gesuchsteller_.nachname);
				break;
			case "antragTyp":
				LOG.warn("Sorting by antragTyp is not yet implemented");
				break;
			case "gesuchsperiode":
				orderField = root.get(Gesuch_.gesuchsperiode).get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb);
				break;
			case "aenderungsdatum":
				//todo team, hier das datum des letzten statusuebergangs verwenden?
					orderField = root.get(Gesuch_.timestampMutiert);
					break;
			case "eingangsdatum":
				orderField = root.get(Gesuch_.eingangsdatum);
				break;
			case "status":
				orderField = root.get(Gesuch_.status);
				break;
			case "angebote":
				LOG.warn("Sortig by angebote is not implemented");
				break;
			case "institutionen":
				LOG.warn("Sortig by institutionen is not implemented");
				break;
			case "verantwortlicher":
				orderField = root.get(Gesuch_.fall).get(Fall_.verantwortlicher);
				break;
			default:
				LOG.warn("Using default sort because there is no specific clause for predicate " + sort.getPredicate());
		}
		List<Order> orders = new ArrayList<>();
		if (sort.getReverse()) {
			orders.add(cb.desc(orderField));
		} else {
			orders.add(cb.asc(orderField));

		}
		return orders;
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
			root.get(Gesuch_.typ)).distinct(true);

		ParameterExpression<String> dateParam = cb.parameter(String.class, "fallId");
		Predicate predicate = cb.equal(root.get(Gesuch_.fall).get(AbstractEntity_.id), dateParam);

		query.where(predicate);
		TypedQuery<JaxAntragDTO> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateParam, fallId);
		query.orderBy(cb.asc(root.get(Gesuch_.gesuchsperiode).get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb)));
		return q.getResultList();
	}

    @Override
    public Pair<Long, List<Gesuch>> searchGesuche(AntragTableFilterDTO antragTableFilterDto) {
        PredicateObjectDTO predicateObjectDto = antragTableFilterDto.getSearch().getPredicateObject();

        CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);
		Root<Gesuch> root = query.from(Gesuch.class);
        query.select(root).distinct(true);
        Join<Gesuch, Fall> fall = root.join(Gesuch_.fall, JoinType.INNER);
        Join<Fall, Benutzer> benutzer = fall.join(Fall_.verantwortlicher, JoinType.LEFT);
        Join<Gesuch, Gesuchsperiode> gesuchsperiode = root.join(Gesuch_.gesuchsperiode, JoinType.INNER);
        Join<Gesuch, Gesuchsteller> gesuchsteller1 = root.join(Gesuch_.gesuchsteller1, JoinType.LEFT);
        Join<Gesuch, Gesuchsteller> gesuchsteller2 = root.join(Gesuch_.gesuchsteller2, JoinType.LEFT);
        SetJoin<Gesuch, KindContainer> kindContainers = root.join(Gesuch_.kindContainers, JoinType.INNER);
        SetJoin<KindContainer, Betreuung> betreuungen = kindContainers.join(KindContainer_.betreuungen, JoinType.INNER);
        Join<Betreuung, InstitutionStammdaten> institutionstammdaten = betreuungen.join(Betreuung_.institutionStammdaten, JoinType.INNER);
        Join<InstitutionStammdaten, Institution> institution = institutionstammdaten.join(InstitutionStammdaten_.institution);

        Collection<Predicate> predicates = new ArrayList<>();
        if (predicateObjectDto != null) {
            if (predicateObjectDto.getFallNummer() != null) {
                predicates.add(cb.equal(fall.get(Fall_.fallNummer), Integer.valueOf(predicateObjectDto.getFallNummer())));
            }
            if (predicateObjectDto.getFamilienName() != null) {
                predicates.add(
                        cb.or(
                                cb.equal(gesuchsteller1.get(Gesuchsteller_.nachname), predicateObjectDto.getFamilienName()),
                                cb.equal(gesuchsteller2.get(Gesuchsteller_.nachname), predicateObjectDto.getFamilienName())
                        ));
            }
            if (predicateObjectDto.getAntragTyp() != null) {
                predicates.add(cb.equal(root.get(Gesuch_.typ), predicateObjectDto.getAntragTyp()));
            }
            if (predicateObjectDto.getGesuchsperiodeString() != null) {
                predicates.add(cb.equal(gesuchsperiode.get(Gesuchsperiode_.gueltigkeit).get(DateRange_.gueltigAb), predicateObjectDto.getGesuchsperiodeString()));
            }
            if (predicateObjectDto.getEingangsdatum() != null) {
                predicates.add(cb.equal(root.get(Gesuch_.eingangsdatum), predicateObjectDto.getEingangsdatum()));
            }
            if (predicateObjectDto.getStatus() != null) {
                predicates.add(cb.equal(root.get(Gesuch_.status), predicateObjectDto.getStatus()));
            }
            if (predicateObjectDto.getAngebote() != null) {
                predicates.add(cb.equal(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), predicateObjectDto.getAngebote()));
            }
            if (predicateObjectDto.getInstitutionen() != null) {
                predicates.add(cb.equal(institution.get(Institution_.name), predicateObjectDto.getInstitutionen()));
            }
            if (predicateObjectDto.getVerantwortlicher() != null) {
                predicates.add(cb.equal(benutzer.get(Benutzer_.username), predicateObjectDto.getVerantwortlicher()));
            }
            predicates.stream().forEach((predicate) -> {
                query.where(predicate);
            });
        }
        // order-by clause
		if (antragTableFilterDto.getSort() != null && antragTableFilterDto.getSort().getPredicate() != null) {
            Expression expression;
            switch (antragTableFilterDto.getSort().getPredicate()) {
                case "fallNummer":
                    expression = fall.get(Fall_.fallNummer);
                    break;
                case "familienName":
                    expression = gesuchsteller1.get(Gesuchsteller_.nachname);
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
                case "status":
                    expression = root.get(Gesuch_.status);
                    break;
                case "angebote":
                    expression = institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp);
                    break;
                case "institutionen":
                    expression = institution.get(Institution_.name);
                    break;
                case "verantwortlicher":
                    expression = fall.get(Fall_.verantwortlicher);
                    break;
                default:
    				LOG.warn("Using default sort because there is no specific clause for predicate " + antragTableFilterDto.getSort().getPredicate());
                    expression = fall.get(Fall_.fallNummer);
                    break;
            }
            query.orderBy(antragTableFilterDto.getSort().getReverse() ? cb.asc(expression) : cb.desc(expression));
        }

		TypedQuery<Gesuch> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setFirstResult(antragTableFilterDto.getPagination().getStart());
		typedQuery.setMaxResults(antragTableFilterDto.getPagination().getNumber());

		List<Gesuch> gesuche = typedQuery.getResultList();
		return new ImmutablePair<>(runCountQuery(antragTableFilterDto), gesuche);
    }
    
}
