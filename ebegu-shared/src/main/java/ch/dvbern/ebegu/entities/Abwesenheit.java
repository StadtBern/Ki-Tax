package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Entity fuer Abwesenheit.
 */
@Audited
@Entity
public class Abwesenheit extends AbstractDateRangedEntity {

	private static final long serialVersionUID = -6776981643150835840L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_betreuung_id"), nullable = false)
	private Betreuung betreuung;


	public Abwesenheit() {

	}

	public Abwesenheit(@Nonnull Abwesenheit toCopy, @Nonnull Betreuung betreuung) {
		super(toCopy);
		this.betreuung = betreuung;
	}

	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}
}
