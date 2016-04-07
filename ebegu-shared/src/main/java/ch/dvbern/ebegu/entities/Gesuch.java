package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Entitaet zum Speichern von Gesuch in der Datenbank.
 */
@Audited
@Entity
public class Gesuch extends AbstractEntity {

	@ManyToOne(optional = false)
	private Fall fall;

	@Nullable
	@ManyToOne(optional = true)
	private Person gesuchssteller1;

	@Nullable
	@ManyToOne(optional = true)
	private Person gesuchssteller2;


	public Fall getFall() {
		return fall;
	}

	public void setFall(Fall fall) {
		this.fall = fall;
	}

	@Nullable
	public Person getGesuchssteller1() {
		return gesuchssteller1;
	}

	public void setGesuchssteller1(@Nullable Person gesuchssteller1) {
		this.gesuchssteller1 = gesuchssteller1;
	}

	@Nullable
	public Person getGesuchssteller2() {
		return gesuchssteller2;
	}

	public void setGesuchssteller2(@Nullable Person gesuchssteller2) {
		this.gesuchssteller2 = gesuchssteller2;
	}
}
