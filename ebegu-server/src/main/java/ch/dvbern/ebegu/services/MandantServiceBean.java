package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Mandanten
 */
@Stateless
@Local(MandantService.class)
public class MandantServiceBean extends AbstractBaseService implements MandantService {

	@Inject
	private Persistence<Mandant> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public Optional<Mandant> findMandant(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Mandant a =  persistence.find(Mandant.class, id);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Mandant getFirst() {
		Collection<Mandant> mandants = criteriaQueryHelper.getAll(Mandant.class);
		if(mandants != null && !mandants.isEmpty()){
			return mandants.iterator().next();
		}else{
			throw new EbeguRuntimeException("getFirst", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
		}
	}
}
