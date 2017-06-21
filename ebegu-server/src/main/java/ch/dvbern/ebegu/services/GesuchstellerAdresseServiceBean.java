package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer_;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse_;
import ch.dvbern.ebegu.entities.Gesuchsteller_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Adresse
 */
@Stateless
@Local(GesuchstellerAdresseService.class)
public class GesuchstellerAdresseServiceBean extends AbstractBaseService implements GesuchstellerAdresseService {

	@Inject
	private Persistence<GesuchstellerAdresseContainer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER })
	public GesuchstellerAdresseContainer createAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse) {
		Objects.requireNonNull(gesuchstellerAdresse);
		return persistence.persist(gesuchstellerAdresse);
	}

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER })
	public GesuchstellerAdresseContainer updateAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse) {
		Objects.requireNonNull(gesuchstellerAdresse);
		return persistence.merge(gesuchstellerAdresse);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<GesuchstellerAdresseContainer> findAdresse(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		GesuchstellerAdresseContainer a = persistence.find(GesuchstellerAdresseContainer.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<GesuchstellerAdresseContainer> getAllAdressen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(GesuchstellerAdresseContainer.class));
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER })
	public void removeAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse) {
		Validate.notNull(gesuchstellerAdresse);
		Optional<GesuchstellerAdresseContainer> adresseToRemove = findAdresse(gesuchstellerAdresse.getId());
		adresseToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeAdresse", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchstellerAdresse));
		adresseToRemove.ifPresent(gesuchstellerAdresseContainer -> persistence.remove(gesuchstellerAdresseContainer));
	}

	/**
	 * Erstellt ein query gegen die Adresse mit den gegebenen parametern
	 * @param gesuchstellerID gesuchsteller fuer die Adressen gesucht werden
	 * @param typ typ der Adresse der gesucht wird
	 * @param maximalDatumVon datum ab dem gesucht wird (incl)
	 * @param minimalDatumBis datum bis zu dem gesucht wird (incl)
	 */
	private TypedQuery<GesuchstellerAdresseContainer> getAdresseQuery(@Nonnull String gesuchstellerID, @Nonnull AdresseTyp typ, @Nullable LocalDate maximalDatumVon, @Nullable LocalDate minimalDatumBis) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> gesuchstellerIdParam = cb.parameter(String.class, "gesuchstellerID");
		ParameterExpression<AdresseTyp> typParam = cb.parameter(AdresseTyp.class, "adresseTyp");
		ParameterExpression<LocalDate> gueltigVonParam = cb.parameter(LocalDate.class, "gueltigVon");
		ParameterExpression<LocalDate> gueltigBisParam = cb.parameter(LocalDate.class, "gueltigBis");

		CriteriaQuery<GesuchstellerAdresseContainer> query = cb.createQuery(GesuchstellerAdresseContainer.class);
		Root<GesuchstellerAdresseContainer> root = query.from(GesuchstellerAdresseContainer.class);
		Predicate gesuchstellerPred = cb.equal(root.get(GesuchstellerAdresseContainer_.gesuchstellerContainer).get(Gesuchsteller_.id), gesuchstellerIdParam);

		Predicate typePredicate;
		if (AdresseTyp.KORRESPONDENZADRESSE == typ) {
			final Join<GesuchstellerAdresseContainer, GesuchstellerAdresse> joinGS = root.join(GesuchstellerAdresseContainer_.gesuchstellerAdresseGS, JoinType.LEFT);
			final Join<GesuchstellerAdresseContainer, GesuchstellerAdresse> joinJA = root.join(GesuchstellerAdresseContainer_.gesuchstellerAdresseJA, JoinType.LEFT);
			typePredicate = cb.or(cb.equal(joinGS.get(GesuchstellerAdresse_.adresseTyp), typParam),
				cb.equal(joinJA.get(GesuchstellerAdresse_.adresseTyp), typParam));
		} else {
			typePredicate = cb.equal(root.get(GesuchstellerAdresseContainer_.gesuchstellerAdresseJA).get(GesuchstellerAdresse_.adresseTyp), typParam);
		}
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		predicatesToUse.add(gesuchstellerPred);
		predicatesToUse.add(typePredicate);
		//noinspection VariableNotUsedInsideIf
		if (maximalDatumVon != null) {
			Predicate datumVonLessThanPred = cb.lessThanOrEqualTo(root.get(GesuchstellerAdresseContainer_.gesuchstellerAdresseJA)
				.get(GesuchstellerAdresse_.gueltigkeit).get(DateRange_.gueltigAb), gueltigVonParam);
			predicatesToUse.add(datumVonLessThanPred);

		}
		//noinspection VariableNotUsedInsideIf
		if (minimalDatumBis != null) {
			Predicate datumBisGreaterThanPRed = cb.greaterThanOrEqualTo(root.get(GesuchstellerAdresseContainer_.gesuchstellerAdresseJA)
				.get(GesuchstellerAdresse_.gueltigkeit).get(DateRange_.gueltigBis), gueltigBisParam);
			predicatesToUse.add(datumBisGreaterThanPRed);

		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicatesToUse));

		TypedQuery<GesuchstellerAdresseContainer> typedQuery = persistence.getEntityManager().createQuery(query);

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
	@PermitAll
	public Optional<GesuchstellerAdresseContainer> getKorrespondenzAdr(String gesuchstellerID) {
		List<GesuchstellerAdresseContainer> results = getAdresseQuery(gesuchstellerID, AdresseTyp.KORRESPONDENZADRESSE, null, null).getResultList();
		if (results.isEmpty()) {
			return Optional.empty();
		}
		if (results.size() > 1) {
			throw new EbeguRuntimeException("getKorrespondenzAdr", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS, gesuchstellerID);
		}
		return Optional.of(results.get(0));
	}
}
