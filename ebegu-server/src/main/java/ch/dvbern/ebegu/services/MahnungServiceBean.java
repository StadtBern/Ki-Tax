package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Mahnung_;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;

/**
 * Service fuer Mahnungen
 */
@Stateless
@Local(MahnungService.class)
public class MahnungServiceBean extends AbstractBaseService implements MahnungService {

	@Inject
	private Persistence<Mahnung> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Override
	@Nonnull
	public Mahnung createMahnung(@Nonnull Mahnung mahnung) {
		Objects.requireNonNull(mahnung);
		return persistence.persist(mahnung);
	}

	@Override
	@Nonnull
	public Collection<Mahnung> findMahnungenForGesuch(@Nonnull Gesuch gesuch) {
		return criteriaQueryHelper.getEntitiesByAttribute(Mahnung.class, gesuch, Mahnung_.gesuch);
	}

	@Override
	public void dokumenteKomplettErhalten(@Nonnull Gesuch gesuch) {
		// Alle Mahnungen auf erledigt stellen
		Collection<Mahnung> mahnungenForGesuch = findMahnungenForGesuch(gesuch);
		for (Mahnung mahnung : mahnungenForGesuch) {
			mahnung.setActive(false);
			persistence.persist(mahnung);
		}
	}
}
