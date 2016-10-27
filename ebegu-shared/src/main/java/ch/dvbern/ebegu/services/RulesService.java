package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.rules.Rule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface fuer RulesService
 */
public interface RulesService {

	List<Rule> getRulesForGesuchsperiode(@Nullable Mandant mandant, @Nonnull Gesuchsperiode gesuchsperiode);

}
