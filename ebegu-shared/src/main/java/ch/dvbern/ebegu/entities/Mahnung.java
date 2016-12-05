package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

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

	@NotNull
	@Column(nullable = false)
	private boolean active = true;


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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
