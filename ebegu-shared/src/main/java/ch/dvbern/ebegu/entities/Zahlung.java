package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.ebegu.util.MathUtil;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entitaet zum Speichern von Zahlungen (=Auftrag fuer 1 Kita) in der Datenbank.
 */
@Audited
@Entity
public class Zahlung extends AbstractEntity implements Comparable<Zahlung>{

	private static final long serialVersionUID = 8975199813240034719L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlung_zahlungsauftrag_id"), nullable = false)
	private Zahlungsauftrag zahlungsauftrag;


	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlung_institutionStammdaten_id"))
	private InstitutionStammdaten institutionStammdaten;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ZahlungStatus status;

	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "zahlung")
	private List<Zahlungsposition> zahlungspositionen = new ArrayList<>();

	@Nonnull
	private BigDecimal betragTotalZahlung;


	public Zahlungsauftrag getZahlungsauftrag() {
		return zahlungsauftrag;
	}

	public void setZahlungsauftrag(Zahlungsauftrag zahlungsauftrag) {
		this.zahlungsauftrag = zahlungsauftrag;
	}

	public InstitutionStammdaten getInstitutionStammdaten() {
		return institutionStammdaten;
	}

	public void setInstitutionStammdaten(InstitutionStammdaten institutionStammdaten) {
		this.institutionStammdaten = institutionStammdaten;
	}

	public ZahlungStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungStatus status) {
		this.status = status;
	}

	@Nonnull
	public List<Zahlungsposition> getZahlungspositionen() {
		return zahlungspositionen;
	}

	public void setZahlungspositionen(@Nonnull List<Zahlungsposition> zahlungspositionen) {
		this.zahlungspositionen = zahlungspositionen;
	}

	@Nonnull
	public BigDecimal getBetragTotalZahlung() {
		return betragTotalZahlung;
	}

	public void setBetragTotalZahlung(@Nonnull BigDecimal betragTotalZahlung) {
		this.betragTotalZahlung = betragTotalZahlung;
	}

	@Override
	public int compareTo(@Nonnull Zahlung o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getInstitutionStammdaten().getInstitution().getName(), o.getInstitutionStammdaten().getInstitution().getName());
		builder.append(this.getZahlungsauftrag().getDatumFaellig(), o.getZahlungsauftrag().getDatumFaellig());
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
		final Zahlung otherZahlung = (Zahlung) other;
		return Objects.equals(getStatus(), otherZahlung.getStatus()) &&
			EbeguUtil.isSameObject(getInstitutionStammdaten(), otherZahlung.getInstitutionStammdaten()) &&
			MathUtil.isSame(getBetragTotalZahlung(), otherZahlung.getBetragTotalZahlung());
	}
}
