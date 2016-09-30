package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Taetigkeit;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.validators.CheckZuschlagErwerbspensumMaxZuschlag;
import ch.dvbern.ebegu.validators.CheckZuschlagErwerbspensumZuschlagUndGrund;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Erwerbspensum eines Gesuchstellers  todo homa erbe von pensum
 */
@Entity
@Audited
@CheckZuschlagErwerbspensumZuschlagUndGrund
@CheckZuschlagErwerbspensumMaxZuschlag
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
	@Column(nullable = true)
	private Integer zuschlagsprozent;

	@Column(nullable = true)
	@Nullable
	private String bezeichnung;


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
		return pensumIsSame && taetigkeitSame && zuschlagSame;
	}

	public String getName() {

		if(bezeichnung == null || bezeichnung.isEmpty()){
			return taetigkeit + " " + getPensum() + "%";
		}else{
			return bezeichnung;
		}
	}

	@Nullable
	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(@Nullable String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
}
