package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
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

/**
 * Service fuer Adresse
 */
@Stateless
@Local(AdresseService.class)
public class AdresseServiceBean extends AbstractBaseService implements AdresseService {

	@Inject
	private Persistence<PersonenAdresse> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public PersonenAdresse createAdresse(@Nonnull PersonenAdresse personenAdresse) {
		Objects.requireNonNull(personenAdresse);
		return persistence.persist(personenAdresse);
	}

	@Nonnull
	@Override
	public PersonenAdresse updateAdresse(@Nonnull PersonenAdresse personenAdresse) {
		Objects.requireNonNull(personenAdresse);
		return persistence.merge(personenAdresse);//foundAdresse.get());
	}

	@Nonnull
	@Override
	public Optional<PersonenAdresse> findAdresse(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		PersonenAdresse a = persistence.find(PersonenAdresse.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<PersonenAdresse> getAllAdressen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(PersonenAdresse.class));
	}

	@Override
	public void removeAdresse(@Nonnull PersonenAdresse personenAdresse) {
		Validate.notNull(personenAdresse);
		Optional<PersonenAdresse> propertyToRemove = findAdresse(personenAdresse.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeAdresse", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, personenAdresse));
		persistence.remove(propertyToRemove.get());
	}

	@Nonnull
	@Override
	public Optional<PersonenAdresse> getNewestWohnadresse(String gesuchstellerID) {
		TypedQuery<PersonenAdresse> query = getAdresseQuery(gesuchstellerID, AdresseTyp.WOHNADRESSE, null, Constants.END_OF_TIME);
		List<PersonenAdresse> results = query.getResultList();
		//wir erwarten entweder keine oder genau eine Wohnadr, fuer eine Gesuchsteller mit guelitBis EndOfTime
		if (results.isEmpty()) {
			return Optional.empty();
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getNewestWohnadresse", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, gesuchstellerID);
		}
		return Optional.of(results.get(0));

	}

	@Nonnull
	@Override
	public PersonenAdresse getCurrentWohnadresse(String gesuchstellerID) {
		LocalDate today = LocalDate.now();
		TypedQuery<PersonenAdresse> query = getAdresseQuery(gesuchstellerID, AdresseTyp.WOHNADRESSE, today, today);
		List<PersonenAdresse> results = query.getResultList();
		//wir erwarten entweder keine oder genau eine Wohnadr, fuer einen Gesuchsteller mit guelitBis EndOfTime
		if (results.isEmpty()) {
			throw new EbeguEntityNotFoundException("getCurrentWohnaddresse", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchstellerID);
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getCurrentWohnaddresse", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, gesuchstellerID);
		}
		return results.get(0);

	}

	/**
	 * Erstellt ein query gegen die Adresse mit den gegebenen parametern
	 * @param gesuchstellerID gesuchsteller fuer die Adressen gesucht werden
	 * @param typ typ der Adresse der gesucht wird
	 * @param maximalDatumVon datum ab dem gesucht wird (incl)
	 * @param minimalDatumBis datum bis zu dem gesucht wird (incl)
	 * @return
	 */
	private TypedQuery<PersonenAdresse> getAdresseQuery(@Nonnull String gesuchstellerID, @Nonnull AdresseTyp typ, @Nullable LocalDate maximalDatumVon, @Nullable LocalDate minimalDatumBis) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> gesuchstellerIdParam = cb.parameter(String.class, "gesuchstellerID");
		ParameterExpression<AdresseTyp> typParam = cb.parameter(AdresseTyp.class, "adresseTyp");
		ParameterExpression<LocalDate> gueltigVonParam = cb.parameter(LocalDate.class, "gueltigVon");
		ParameterExpression<LocalDate> gueltigBisParam = cb.parameter(LocalDate.class, "gueltigBis");

		CriteriaQuery<PersonenAdresse> query = cb.createQuery(PersonenAdresse.class);
		Root<PersonenAdresse> root = query.from(PersonenAdresse.class);
		Predicate gesuchstellerPred = cb.equal(root.get(PersonenAdresse_.gesuchsteller).get(Gesuchsteller_.id), gesuchstellerIdParam);
		Predicate typePredicate = cb.equal(root.get(PersonenAdresse_.adresseTyp), typParam);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		predicatesToUse.add(gesuchstellerPred);
		predicatesToUse.add(typePredicate);
		//noinspection VariableNotUsedInsideIf
		if (maximalDatumVon != null) {
			Predicate datumVonLessThanPred = cb.lessThanOrEqualTo(root.get(PersonenAdresse_.gueltigkeit).get(DateRange_.gueltigAb), gueltigVonParam);
			predicatesToUse.add(datumVonLessThanPred);

		}
		//noinspection VariableNotUsedInsideIf
		if (minimalDatumBis != null) {
			Predicate datumBisGreaterThanPRed = cb.greaterThanOrEqualTo(root.get(PersonenAdresse_.gueltigkeit).get(DateRange_.gueltigBis), gueltigBisParam);
			predicatesToUse.add(datumBisGreaterThanPRed);

		}


		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicatesToUse));

		TypedQuery<PersonenAdresse> typedQuery = persistence.getEntityManager().createQuery(query);

		typedQuery.setParameter("gesuchstellerID", gesuchstellerID);
		typedQuery.setParameter("adresseTyp", typ);
		if (maximalDatumVon != null) {
			typedQuery.setParameter("gueltigVon", maximalDatumVon);
		}
		if (minimalDatumBis != null) {
			typedQuery.setParameter("gueltigBis", minimalDatumBis);
		}
		return typedQuery;
	}

	@Nonnull
	@Override
	public Optional<PersonenAdresse> getKorrespondenzAdr(String gesuchstellerID) {

		List<PersonenAdresse> results = getAdresseQuery(gesuchstellerID, AdresseTyp.KORRESPONDENZADRESSE, null, null).getResultList();
		if (results.isEmpty()) {
			return Optional.empty();
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getKorrespondenzAdr", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, gesuchstellerID);
		}
		return Optional.of(results.get(0));
	}
}
