package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.AntragSortDTO;
import ch.dvbern.ebegu.dto.suchfilter.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
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
import java.util.*;



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

		Predicate predicateGesuch = cb.notEqual(root.get(Gesuch_.status), AntragStatus.VERFUEGT);
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
}
