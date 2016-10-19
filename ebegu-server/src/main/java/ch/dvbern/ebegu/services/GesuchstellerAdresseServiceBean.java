package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse_;
import ch.dvbern.ebegu.entities.Gesuchsteller_;
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
@Local(GesuchstellerAdresseService.class)
public class GesuchstellerAdresseServiceBean extends AbstractBaseService implements GesuchstellerAdresseService {

	@Inject
	private Persistence<GesuchstellerAdresse> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public GesuchstellerAdresse createAdresse(@Nonnull GesuchstellerAdresse gesuchstellerAdresse) {
		Objects.requireNonNull(gesuchstellerAdresse);
		return persistence.persist(gesuchstellerAdresse);
	}

	@Nonnull
	@Override
	public GesuchstellerAdresse updateAdresse(@Nonnull GesuchstellerAdresse gesuchstellerAdresse) {
		Objects.requireNonNull(gesuchstellerAdresse);
		return persistence.merge(gesuchstellerAdresse);//foundAdresse.get());
	}

	@Nonnull
	@Override
	public Optional<GesuchstellerAdresse> findAdresse(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		GesuchstellerAdresse a = persistence.find(GesuchstellerAdresse.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<GesuchstellerAdresse> getAllAdressen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(GesuchstellerAdresse.class));
	}

	@Override
	public void removeAdresse(@Nonnull GesuchstellerAdresse gesuchstellerAdresse) {
		Validate.notNull(gesuchstellerAdresse);
		Optional<GesuchstellerAdresse> propertyToRemove = findAdresse(gesuchstellerAdresse.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeAdresse", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchstellerAdresse));
		persistence.remove(propertyToRemove.get());
	}

	@Nonnull
	@Override
	public Optional<GesuchstellerAdresse> getNewestWohnadresse(String gesuchstellerID) {
		TypedQuery<GesuchstellerAdresse> query = getAdresseQuery(gesuchstellerID, AdresseTyp.WOHNADRESSE, null, Constants.END_OF_TIME);
		List<GesuchstellerAdresse> results = query.getResultList();
		//wir erwarten entweder keine oder genau eine Wohnadr, fuer eine Gesuchsteller mit gueltigBis EndOfTime
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
	public GesuchstellerAdresse getCurrentWohnadresse(String gesuchstellerID) {
		LocalDate today = LocalDate.now();
		TypedQuery<GesuchstellerAdresse> query = getAdresseQuery(gesuchstellerID, AdresseTyp.WOHNADRESSE, today, today);
		List<GesuchstellerAdresse> results = query.getResultList();
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
	private TypedQuery<GesuchstellerAdresse> getAdresseQuery(@Nonnull String gesuchstellerID, @Nonnull AdresseTyp typ, @Nullable LocalDate maximalDatumVon, @Nullable LocalDate minimalDatumBis) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> gesuchstellerIdParam = cb.parameter(String.class, "gesuchstellerID");
		ParameterExpression<AdresseTyp> typParam = cb.parameter(AdresseTyp.class, "adresseTyp");
		ParameterExpression<LocalDate> gueltigVonParam = cb.parameter(LocalDate.class, "gueltigVon");
		ParameterExpression<LocalDate> gueltigBisParam = cb.parameter(LocalDate.class, "gueltigBis");

		CriteriaQuery<GesuchstellerAdresse> query = cb.createQuery(GesuchstellerAdresse.class);
		Root<GesuchstellerAdresse> root = query.from(GesuchstellerAdresse.class);
		Predicate gesuchstellerPred = cb.equal(root.get(GesuchstellerAdresse_.gesuchsteller).get(Gesuchsteller_.id), gesuchstellerIdParam);
		Predicate typePredicate = cb.equal(root.get(GesuchstellerAdresse_.adresseTyp), typParam);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		predicatesToUse.add(gesuchstellerPred);
		predicatesToUse.add(typePredicate);
		//noinspection VariableNotUsedInsideIf
		if (maximalDatumVon != null) {
			Predicate datumVonLessThanPred = cb.lessThanOrEqualTo(root.get(GesuchstellerAdresse_.gueltigkeit).get(DateRange_.gueltigAb), gueltigVonParam);
			predicatesToUse.add(datumVonLessThanPred);

		}
		//noinspection VariableNotUsedInsideIf
		if (minimalDatumBis != null) {
			Predicate datumBisGreaterThanPRed = cb.greaterThanOrEqualTo(root.get(GesuchstellerAdresse_.gueltigkeit).get(DateRange_.gueltigBis), gueltigBisParam);
			predicatesToUse.add(datumBisGreaterThanPRed);

		}


		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicatesToUse));

		TypedQuery<GesuchstellerAdresse> typedQuery = persistence.getEntityManager().createQuery(query);

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
	public Optional<GesuchstellerAdresse> getKorrespondenzAdr(String gesuchstellerID) {

		List<GesuchstellerAdresse> results = getAdresseQuery(gesuchstellerID, AdresseTyp.KORRESPONDENZADRESSE, null, null).getResultList();
		if (results.isEmpty()) {
			return Optional.empty();
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getKorrespondenzAdr", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, gesuchstellerID);
		}
		return Optional.of(results.get(0));
	}
}
