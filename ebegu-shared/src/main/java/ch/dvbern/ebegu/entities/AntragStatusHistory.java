package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AntragStatus;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity um eine History von AntragStatus zu speichern.
 */
@Audited
@Entity
public class AntragStatusHistory extends AbstractEntity {

	private static final long serialVersionUID = -9032257320864372570L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_antragstatus_history_antrag_id"), nullable = false)
	private Gesuch gesuch;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_antragstatus_history_benutzer_id"), nullable = false)
	private Benutzer benutzer = null;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime datum;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AntragStatus status;



	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	public LocalDateTime getDatum() {
		return datum;
	}

	public void setDatum(LocalDateTime datum) {
		this.datum = datum;
	}

	public AntragStatus getStatus() {
		return status;
	}

	public void setStatus(AntragStatus status) {
		this.status = status;
	}
}
