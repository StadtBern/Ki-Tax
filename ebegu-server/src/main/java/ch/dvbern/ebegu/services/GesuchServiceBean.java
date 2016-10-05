package ch.dvbern.ebegu.services;

import static ch.dvbern.ebegu.entities.AbstractAntragEntity_.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.PredicateObjectDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
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
    public Pair<Long, List<Gesuch>> searchAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto) {
        Pair<Long, List<Gesuch>> result;
        Pair<Long, List<Gesuch>> countResult = searchAntraege(antragTableFilterDto, Mode.COUNT);
        if (countResult.getLeft().equals(Long.valueOf(0))) {
            result = new ImmutablePair<>(0L, Collections.EMPTY_LIST);
        } else {
            Pair<Long, List<Gesuch>> searchResult = searchAntraege(antragTableFilterDto, Mode.SEARCH);
            result = new ImmutablePair<>(countResult.getLeft(), searchResult.getRight());
        }
        return result;
    }

    private Pair<Long, List<Gesuch>> searchAntraege(@Nonnull AntragTableFilterDTO antragTableFilterDto, @Nonnull Mode mode) {
        Benutzer user = benutzerService.getCurrentBenutzer().get();
        UserRole role = user.getRole();

        Set<AntragStatus> allowedAntragStatus = AntragStatus.allowedforRole(role);
        if (allowedAntragStatus.isEmpty()) {
            return new ImmutablePair<>(0L, Collections.EMPTY_LIST);
        }

        CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaQuery query = null;
        switch(mode) {
            case SEARCH:
        		query = cb.createQuery(Gesuch.class);
                break;
            case COUNT:
                query = cb.createQuery(Long.class);
                break;
            default:
                // TODO: Throw appropriate exception here
                break;
        }
        // Construct from-clause
		Root<Gesuch> root = query.from(Gesuch.class);
        
        // Join the relevant relations
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

        // General role based predicates
        CriteriaBuilder.In<AntragStatus> inClauseStatus = cb.in(root.get(Gesuch_.status));
        allowedAntragStatus.stream().forEach((status) -> {
            inClauseStatus.value(status);
        });
        predicates.add(inClauseStatus);

        // Special role based predicates
        switch(role) {
            case ADMIN:
            case REVISOR:
                break;
            case SACHBEARBEITER_JA:
            case JURIST:
                predicates.add(cb.notEqual(institutionstammdaten.get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.TAGESSCHULE));
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
                break;
        }

        // Predicates derived from PredicateDTO
        PredicateObjectDTO predicateObjectDto = antragTableFilterDto.getSearch().getPredicateObject();
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
                predicates.add(cb.equal(root.get(Gesuch_.typ), AntragTyp.valueOf(predicateObjectDto.getAntragTyp())));
            }
            if (predicateObjectDto.getGesuchsperiodeString() != null) {
                String[] years = predicateObjectDto.getGesuchsperiodeString().split("/");
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
        switch(mode) {
            case SEARCH:
                query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
                break;
            case COUNT:
                query.select(cb.countDistinct(root)).where(cb.and(predicates.toArray(new Predicate[0])));
                break;
        }

        // Construct the order-by clause
        switch(mode) {
            case SEARCH:
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
                break;
            case COUNT:
                break;
        }

        // Prepare and execute the query and build the result
        Pair<Long, List<Gesuch>> result = null;
        switch(mode) {
            case SEARCH:
                TypedQuery<Gesuch> typedQuery = persistence.getEntityManager().createQuery(query);
                typedQuery.setFirstResult(antragTableFilterDto.getPagination().getStart());
                typedQuery.setMaxResults(antragTableFilterDto.getPagination().getNumber());
                List<Gesuch> gesuche = typedQuery.getResultList();
                result = new ImmutablePair<>(null, gesuche);
                break;
            case COUNT:
                result = new ImmutablePair<>(persistence.getCriteriaSingleResult(query), null);
                break;
        }
        return result;
    }

    private enum Mode {
        COUNT,
        SEARCH
    }
}
