package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entitaet fuer Mahnungen
 */
@Audited
@Entity
public class Mahnung extends AbstractEntity {

	private static final long serialVersionUID = -4210097012467874096L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_mahnung_gesuch_id"))
	private Gesuch gesuch;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MahnungTyp mahnungTyp;

	@NotNull
	@Column(nullable = false)
	private LocalDate datumFristablauf;

	@NotNull
	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Column(nullable = false, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

	@Column(nullable = true, columnDefinition = "DATETIME(6)")
	private LocalDateTime timestampAbgeschlossen;

	/**
	 * This parameter must be set to true when the Mahnung has already been treated,
	 * so that we can distinguish between a Mahnung with a fristDatum in the past and a
	 * Mahnung that has really been set as expired.
	 */
	@NotNull
	@Column(nullable = false)
	private Boolean abgelaufen = false;


	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public MahnungTyp getMahnungTyp() {
		return mahnungTyp;
	}

	public void setMahnungTyp(MahnungTyp mahnungTyp) {
		this.mahnungTyp = mahnungTyp;
	}

	public LocalDate getDatumFristablauf() {
		return datumFristablauf;
	}

	public void setDatumFristablauf(LocalDate datumFristablauf) {
		this.datumFristablauf = datumFristablauf;
	}

	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public LocalDateTime getTimestampAbgeschlossen() {
		return timestampAbgeschlossen;
	}

	public void setTimestampAbgeschlossen(LocalDateTime timestampBeendet) {
		this.timestampAbgeschlossen = timestampBeendet;
	}

	public Boolean getAbgelaufen() {
		return abgelaufen;
	}

	public void setAbgelaufen(Boolean abgelaufen) {
		this.abgelaufen = abgelaufen;
	}
}
