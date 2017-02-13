package ch.dvbern.ebegu.services;

import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(Pain001Service.class)
public class Pain001ServiceBean extends AbstractBaseService implements Pain001Service {




}
