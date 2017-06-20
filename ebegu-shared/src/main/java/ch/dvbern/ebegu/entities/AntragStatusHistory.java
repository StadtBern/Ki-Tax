package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AntragStatus;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity um eine History von AntragStatus zu speichern.
 */
@Audited
@Entity
public class AntragStatusHistory extends AbstractEntity implements Comparable<AntragStatusHistory>{

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
	@Column(nullable = false, columnDefinition = "DATETIME(6)")
	private LocalDateTime timestampVon;

	@Column(nullable = true, columnDefinition = "DATETIME(6)")
	private LocalDateTime timestampBis;

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

	public LocalDateTime getTimestampVon() {
		return timestampVon;
	}

	public void setTimestampVon(LocalDateTime datum) {
		this.timestampVon = datum;
	}

	public LocalDateTime getTimestampBis() {
		return timestampBis;
	}

	public void setTimestampBis(LocalDateTime timestampBis) {
		this.timestampBis = timestampBis;
	}

	public AntragStatus getStatus() {
		return status;
	}

	public void setStatus(AntragStatus status) {
		this.status = status;
	}

	@Override
	public int compareTo(AntragStatusHistory o) {
		CompareToBuilder cb = new CompareToBuilder();
		cb.append(this.getTimestampVon(), o.getTimestampVon())
			.append(this.getId(), o.getId());
		return cb.toComparison();
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
		final AntragStatusHistory otherAntragStatusHistory = (AntragStatusHistory) other;
		return Objects.equals(getGesuch().getId(), otherAntragStatusHistory.getGesuch().getId()) && // the content is not relevant
			Objects.equals(getBenutzer().getId(), otherAntragStatusHistory.getBenutzer().getId()) && // the content is not relevant
			Objects.equals(getTimestampVon(), otherAntragStatusHistory.getTimestampVon()) &&
			Objects.equals(getTimestampBis(), otherAntragStatusHistory.getTimestampBis()) &&
			Objects.equals(getStatus(), otherAntragStatusHistory.getStatus());
	}
}
