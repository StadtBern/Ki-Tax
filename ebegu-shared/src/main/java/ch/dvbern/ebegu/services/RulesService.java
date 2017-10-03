package ch.dvbern.ebegu.services;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.rules.Rule;

/**
 * Interface fuer RulesService
 */
public interface RulesService {

	List<Rule> getRulesForGesuchsperiode(@Nullable Mandant mandant, @Nonnull Gesuchsperiode gesuchsperiode);

}
