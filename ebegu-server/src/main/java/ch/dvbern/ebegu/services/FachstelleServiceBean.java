package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fachstelle;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

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
@Local(FachstelleService.class)
public class FachstelleServiceBean extends AbstractBaseService implements FachstelleService {

	@Inject
	private Persistence<Fachstelle> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;




	@Nonnull
	@Override
	public Fachstelle saveFachstelle(@Nonnull Fachstelle fachstelle) {
		Objects.requireNonNull(fachstelle);
		return persistence.merge(fachstelle);
	}

	@Nonnull
	@Override
	public Optional<Fachstelle> findFachstelle(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Fachstelle a =  persistence.find(Fachstelle.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Fachstelle> getAllFachstellen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Fachstelle.class));
	}

	@Override
	public void removeFachstelle(@Nonnull String fachstelleId) {
		Objects.requireNonNull(fachstelleId);
		Optional<Fachstelle> familiensituationToRemove = findFachstelle(fachstelleId);
		familiensituationToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fachstelleId));
		persistence.remove(familiensituationToRemove.get());
	}

}
