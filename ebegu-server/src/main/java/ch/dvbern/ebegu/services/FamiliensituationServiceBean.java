package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer familiensituation
 */
@Stateless
@Local(FamiliensituationService.class)
public class FamiliensituationServiceBean extends AbstractBaseService implements FamiliensituationService {

	@Inject
	private Persistence<Familiensituation> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public Familiensituation createFamiliensituation(@Nonnull Familiensituation familiensituation) {
		Objects.requireNonNull(familiensituation);
		return persistence.persist(familiensituation);
	}

	@Nonnull
	@Override
	public Familiensituation updateFamiliensituation(@Nonnull Familiensituation familiensituation) {
		Objects.requireNonNull(familiensituation);
		return persistence.merge(familiensituation);
	}

	@Nonnull
	@Override
	public Optional<Familiensituation> findFamiliensituation(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Familiensituation a =  persistence.find(Familiensituation.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Familiensituation> getAllFamiliensituatione() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Familiensituation.class));
	}

	@Nonnull
	@Override
	public void removeFamiliensituation(@Nonnull Familiensituation familiensituation) {
		Validate.notNull(familiensituation);
		Optional<Familiensituation> familiensituationToRemove = findFamiliensituation(familiensituation.getId());
		familiensituationToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, familiensituation));
		persistence.remove(familiensituationToRemove.get());
	}

}
