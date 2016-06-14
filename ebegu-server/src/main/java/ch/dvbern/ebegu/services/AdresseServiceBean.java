package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.Adresse_;
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

}
