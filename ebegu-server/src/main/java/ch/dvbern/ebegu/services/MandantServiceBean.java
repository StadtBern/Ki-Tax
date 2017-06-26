package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service fuer Mandanten
 */
@Stateless
@Local(MandantService.class)
@PermitAll
public class MandantServiceBean extends AbstractBaseService implements MandantService {

	private static final Logger LOG = LoggerFactory.getLogger(MandantServiceBean.class);

	@Inject
	private Persistence<Mandant> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public Optional<Mandant> findMandant(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Mandant a = persistence.find(Mandant.class, id);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Mandant getFirst() {
		Collection<Mandant> mandants = criteriaQueryHelper.getAll(Mandant.class);
		if (mandants != null && !mandants.isEmpty()) {
			return mandants.iterator().next();
		} else {
			LOG.error("Wir erwarten, dass mindestens ein Mandant bereits in der DB existiert");
			throw new EbeguRuntimeException("getFirst", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
		}
	}
}
