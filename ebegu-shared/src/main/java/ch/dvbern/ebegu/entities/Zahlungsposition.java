package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.ebegu.util.MathUtil;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entitaet zum Speichern von Zahlungspositionen in der Datenbank.
 */
@Audited
@Entity
public class Zahlungsposition extends AbstractEntity implements Comparable<Zahlungsposition> {

	private static final long serialVersionUID = -8403487439884700618L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlungsposition_zahlung_id"), nullable = false)
	private Zahlung zahlung;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlungsposition_verfuegungZeitabschnitt_id"), nullable = false)
	private VerfuegungZeitabschnitt verfuegungZeitabschnitt;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ZahlungspositionStatus status;

	@NotNull
	@Column(nullable = false)
	private BigDecimal betrag;

	@NotNull
	@Column(nullable = false)
	private boolean ignoriert;

	public Zahlung getZahlung() {
		return zahlung;
	}

	public void setZahlung(Zahlung zahlung) {
		this.zahlung = zahlung;
	}

	public VerfuegungZeitabschnitt getVerfuegungZeitabschnitt() {
		return verfuegungZeitabschnitt;
	}

	public void setVerfuegungZeitabschnitt(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		this.verfuegungZeitabschnitt = verfuegungZeitabschnitt;
	}

	public ZahlungspositionStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungspositionStatus status) {
		this.status = status;
	}

	public BigDecimal getBetrag() {
		return betrag;
	}

	public void setBetrag(BigDecimal betrag) {
		this.betrag = betrag;
	}

	public Kind getKind() {
		return verfuegungZeitabschnitt.getVerfuegung().getBetreuung().getKind().getKindJA();
	}

	public boolean isIgnoriert() {
		return ignoriert;
	}

	public void setIgnoriert(boolean ignoriert) {
		this.ignoriert = ignoriert;
	}

	@Override
	public int compareTo(@Nonnull Zahlungsposition o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getVerfuegungZeitabschnitt().getVerfuegung().getBetreuung().getBGNummer(), o.getVerfuegungZeitabschnitt().getVerfuegung().getBetreuung().getBGNummer());
		builder.append(this.getVerfuegungZeitabschnitt().getGueltigkeit().getGueltigAb(), o.getVerfuegungZeitabschnitt().getGueltigkeit().getGueltigAb());
		builder.append(this.getBetrag(), o.getBetrag());
		return builder.toComparison();
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof Zahlungsposition)) {
			return false;
		}
		final Zahlungsposition otherZahlungsposition = (Zahlungsposition) other;
		return EbeguUtil.isSameObject(getVerfuegungZeitabschnitt(), otherZahlungsposition.getVerfuegungZeitabschnitt()) &&
			Objects.equals(getStatus(), otherZahlungsposition.getStatus()) &&
			MathUtil.isSame(getBetrag(), otherZahlungsposition.getBetrag()) &&
			Objects.equals(isIgnoriert(), otherZahlungsposition.isIgnoriert());
	}
}
