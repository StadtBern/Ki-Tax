package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.ZahlungStatus;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitaet zum Speichern von Zahlungen (=Auftrag fuer 1 Kita) in der Datenbank.
 */
@Audited
@Entity
public class Zahlung extends AbstractEntity {

	private static final long serialVersionUID = 8975199813240034719L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlung_zahlungsauftrag_id"), nullable = false)
	private Zahlungsauftrag zahlungsauftrag;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Zahlung_institution_id"))
	private Institution institution;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ZahlungStatus status;

	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "zahlung")
	private List<Zahlungsposition> zahlungspositionen = new ArrayList<>();


	public Zahlungsauftrag getZahlungsauftrag() {
		return zahlungsauftrag;
	}

	public void setZahlungsauftrag(Zahlungsauftrag zahlungsauftrag) {
		this.zahlungsauftrag = zahlungsauftrag;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
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
}
