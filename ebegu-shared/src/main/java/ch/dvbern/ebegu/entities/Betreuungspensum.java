package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.validators.CheckBetreuungspensum;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Entity fuer Betreuungspensen.
 */
@Audited
@Entity
@CheckBetreuungspensum
public class Betreuungspensum extends AbstractPensumEntity {

	private static final long serialVersionUID = -9032857320571372370L;

}
