package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity fuer Kinder.
 */
@Audited
@Entity
public class Kind extends AbstractPersonEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Kinderabzug kinderabzug;

	@Column(nullable = false)
	@NotNull
	private Boolean familienErgaenzendeBetreuung = false;

	@Column(nullable = true)
	@Nullable
	private Boolean mutterspracheDeutsch;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_kind_pensum_fachstelle_id"), nullable = true)
	private PensumFachstelle pensumFachstelle;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;


	public Kinderabzug getKinderabzug() {
		return kinderabzug;
	}

	public void setKinderabzug(Kinderabzug kinderabzug) {
		this.kinderabzug = kinderabzug;
	}

	public Boolean getFamilienErgaenzendeBetreuung() {
		return familienErgaenzendeBetreuung;
	}

	public void setFamilienErgaenzendeBetreuung(Boolean familienErgaenzendeBetreuung) {
		this.familienErgaenzendeBetreuung = familienErgaenzendeBetreuung;
	}

	@Nullable
	public Boolean getMutterspracheDeutsch() {
		return mutterspracheDeutsch;
	}

	public void setMutterspracheDeutsch(@Nullable Boolean mutterspracheDeutsch) {
		this.mutterspracheDeutsch = mutterspracheDeutsch;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public PensumFachstelle getPensumFachstelle() {
		return pensumFachstelle;
	}

	public void setPensumFachstelle(PensumFachstelle pensumFachstelle) {
		this.pensumFachstelle = pensumFachstelle;
	}
}
