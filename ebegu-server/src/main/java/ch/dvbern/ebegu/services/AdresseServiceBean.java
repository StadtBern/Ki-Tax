package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.*;

import static ch.dvbern.ebegu.entities.Adresse_.gueltigAb;
import static ch.dvbern.ebegu.entities.Adresse_.gueltigBis;

/**
 * Service fuer Adresse
 */
@Stateless
@Local(AdresseService.class)
public class AdresseServiceBean extends AbstractBaseService implements AdresseService {

	@Inject
	private Persistence<Adresse> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public Adresse createAdresse(@Nonnull Adresse adresse) {
		Objects.requireNonNull(adresse);
		return persistence.persist(adresse);
	}

	@Nonnull
	@Override
	public Adresse updateAdresse(@Nonnull Adresse adresse) {
		Objects.requireNonNull(adresse);
		return persistence.merge(adresse);//foundAdresse.get());
	}

	@Nonnull
	@Override
	public Optional<Adresse> findAdresse(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Adresse a = persistence.find(Adresse.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<Adresse> getAllAdressen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Adresse.class));
	}

	@Override
	public void removeAdresse(@Nonnull Adresse adresse) {
		Validate.notNull(adresse);
		Optional<Adresse> propertyToRemove = findAdresse(adresse.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeAdresse", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, adresse));
		persistence.remove(propertyToRemove.get());
	}

	@Nonnull
	@Override
	public Optional<Adresse> getNewestWohnadresse(String personID) {
		TypedQuery<Adresse> query = getAdresseQuery(personID, AdresseTyp.WOHNADRESSE, null, Constants.END_OF_TIME);
		List<Adresse> results = query.getResultList();
		//wir erwarten entweder keine oder genau eine Wohnadr, fuer eine Person mit guelitBis EndOfTime
		if (results.isEmpty()) {
			return Optional.empty();
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getNewestWohnadresse", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, personID);
		}
		return Optional.of(results.get(0));

	}

	@Nonnull
	@Override
	public Adresse getCurrentWohnadresse(String personID) {
		LocalDate today = LocalDate.now();
		TypedQuery<Adresse> query = getAdresseQuery(personID, AdresseTyp.WOHNADRESSE, today, today);
		List<Adresse> results = query.getResultList();
		//wir erwarten entweder keine oder genau eine Wohnadr, fuer eine Person mit guelitBis EndOfTime
		if (results.isEmpty()) {
			throw new EbeguRuntimeException("getCurrentWohnaddresse", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, personID);
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getCurrentWohnaddresse", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, personID);
		}
		return results.get(0);

	}

	/**
	 * Erstellt ein query gegen die Adresse mit den gegebenen parametern
	 * @param personID person fuer die Adressen gesucht werden
	 * @param typ typ der Adresse der gesucht wird
	 * @param datumVon datum ab dem gesucht wird (incl)
	 * @param datumBis datum bis zu dem gesucht wird (incl)
	 * @return
	 */
	private TypedQuery<Adresse> getAdresseQuery(@Nonnull String personID, @Nonnull AdresseTyp typ, @Nullable LocalDate datumVon, @Nullable LocalDate datumBis) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> personIdParam = cb.parameter(String.class, "personID");
		ParameterExpression<AdresseTyp> typParam = cb.parameter(AdresseTyp.class, "adresseTyp");
		ParameterExpression<LocalDate> gueltigVonParam = cb.parameter(LocalDate.class, "gueltigVon");
		ParameterExpression<LocalDate> gueltigBisParam = cb.parameter(LocalDate.class, "gueltigBis");

		CriteriaQuery<Adresse> query = cb.createQuery(Adresse.class);
		Root<Adresse> root = query.from(Adresse.class);
		Predicate personPredicate = cb.equal(root.get(Adresse_.person).get(Person_.id), personIdParam);
		Predicate typePredicate = cb.equal(root.get(Adresse_.adresseTyp), typParam);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		predicatesToUse.add(personPredicate);
		predicatesToUse.add(typePredicate);
		//noinspection VariableNotUsedInsideIf
		if (datumVon != null) {
			Predicate datumVonPredicate = cb.greaterThanOrEqualTo(root.get(gueltigAb), gueltigVonParam);
			predicatesToUse.add(datumVonPredicate);

		}
		//noinspection VariableNotUsedInsideIf
		if (datumBis != null) {
			Predicate datumBisPredicate = cb.lessThanOrEqualTo(root.get(gueltigBis), gueltigBisParam);
			predicatesToUse.add(datumBisPredicate);

		}


		query.where(criteriaQueryHelper.concatenateExpressions(cb, predicatesToUse));

		TypedQuery<Adresse> typedQuery = persistence.getEntityManager().createQuery(query);

		typedQuery.setParameter("personID", personID);
		typedQuery.setParameter("adresseTyp", typ);
		if (datumVon != null) {
			typedQuery.setParameter("gueltigVon", datumVon);
		}
		if (datumBis != null) {
			typedQuery.setParameter("gueltigBis", datumBis);
		}
		return typedQuery;
	}

	@Nonnull
	@Override
	public Optional<Adresse> getKorrespondenzAdr(String personID) {

		List<Adresse> results = getAdresseQuery(personID, AdresseTyp.KORRESPONDENZADRESSE, null, null).getResultList();
		if (results.isEmpty()) {
			return Optional.empty();
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getKorrespondenzAdr", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, personID);
		}
		return Optional.of(results.get(0));
	}
}
