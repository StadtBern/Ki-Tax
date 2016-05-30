package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Betreuung
 */
@Stateless
@Local(BetreuungService.class)
public class BetreuungServiceBean extends AbstractBaseService implements BetreuungService {

	@Inject
	private Persistence<Betreuung> persistence;


	@Override
	@Nonnull
	public Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung);
		return persistence.merge(betreuung);
	}

	@Override
	@Nonnull
	public Optional<Betreuung> findBetreuung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Betreuung a =  persistence.find(Betreuung.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	public void removeBetreuung(@Nonnull String betreuungId) {
		Objects.requireNonNull(betreuungId);
		Optional<Betreuung> betreuungToRemove = findBetreuung(betreuungId);
		betreuungToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungId));
		persistence.remove(betreuungToRemove.get());
	}
}
