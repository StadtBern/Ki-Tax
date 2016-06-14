package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Taetigkeit;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.validators.CheckZuschlagPensum;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Erwerbspensum eines Gesuchstellers  todo homa erbe von pensum
 */
@Entity
@Audited
@CheckZuschlagPensum
public class Erwerbspensum extends AbstractPensumEntity {

	private static final long serialVersionUID = 4649639217797690323L;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private Taetigkeit taetigkeit;

	@Column(nullable = false)
	@NotNull
	private boolean zuschlagZuErwerbspensum;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Zuschlagsgrund zuschlagsgrund;

	@Min(0)
	@Max(20) //Maximal 20%
	@Column(nullable = true)
	private Integer zuschlagsprozent;

	@Column(nullable = false)
	@NotNull
	private boolean gesundheitlicheEinschraenkungen;


	//todo homa Prozent kann wohl aus Pensum geerbt werden


	public Taetigkeit getTaetigkeit() {
		return taetigkeit;
	}

	public void setTaetigkeit(Taetigkeit taetigkeit) {
		this.taetigkeit = taetigkeit;
	}

	public boolean getZuschlagZuErwerbspensum() {
		return zuschlagZuErwerbspensum;
	}

	public void setZuschlagZuErwerbspensum(boolean zuschlagZuErwerbspensum) {
		this.zuschlagZuErwerbspensum = zuschlagZuErwerbspensum;
	}

	public boolean getGesundheitlicheEinschraenkungen() {
		return gesundheitlicheEinschraenkungen;
	}

	public void setGesundheitlicheEinschraenkungen(boolean gesundheitlicheEinschraenkungen) {
		this.gesundheitlicheEinschraenkungen = gesundheitlicheEinschraenkungen;
	}

	public Integer getZuschlagsprozent() {
		return zuschlagsprozent;
	}

	public void setZuschlagsprozent(Integer zuschlagsprozent) {
		this.zuschlagsprozent = zuschlagsprozent;
	}

	public Zuschlagsgrund getZuschlagsgrund() {
		return zuschlagsgrund;
	}

	public void setZuschlagsgrund(Zuschlagsgrund zuschlagsgrund) {
		this.zuschlagsgrund = zuschlagsgrund;
	}

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(Erwerbspensum otherErwerbspensum) {
		boolean pensumIsSame = super.isSame(otherErwerbspensum);
		boolean taetigkeitSame = Objects.equals(taetigkeit, otherErwerbspensum.getTaetigkeit());
		boolean zuschlagSame = Objects.equals(zuschlagZuErwerbspensum, otherErwerbspensum.getZuschlagZuErwerbspensum());
		boolean gesundhSame = Objects.equals(gesundheitlicheEinschraenkungen, otherErwerbspensum.getGesundheitlicheEinschraenkungen());
		return pensumIsSame && taetigkeitSame &&  zuschlagSame && gesundhSame;
	}
}
