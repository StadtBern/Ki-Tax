package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Kinderabzug;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity fuer Kinder.
 */
@Audited
@Entity
@Indexed
public class Kind extends AbstractPersonEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@Max(100)
	@Min(0)
	@Nullable
	@Column(nullable = true)
	private Integer wohnhaftImGleichenHaushalt;

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

	@Column(nullable = true)
	@Nullable
	private Boolean einschulung;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_kind_pensum_fachstelle_id"), nullable = true)
	private PensumFachstelle pensumFachstelle;


	public Kind() {
	}


	@Nullable
	public Integer getWohnhaftImGleichenHaushalt() {
		return wohnhaftImGleichenHaushalt;
	}

	public void setWohnhaftImGleichenHaushalt(@Nullable Integer wohnhaftImGleichenHaushalt) {
		this.wohnhaftImGleichenHaushalt = wohnhaftImGleichenHaushalt;
	}

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

	public PensumFachstelle getPensumFachstelle() {
		return pensumFachstelle;
	}

	public void setPensumFachstelle(PensumFachstelle pensumFachstelle) {
		this.pensumFachstelle = pensumFachstelle;
	}

	@Nullable
	public Boolean getEinschulung() {
		return einschulung;
	}

	public void setEinschulung(@Nullable Boolean einschulung) {
		this.einschulung = einschulung;
	}

	public Kind copyForMutation(Kind mutation) {
		super.copyForMutation(mutation);
		mutation.setWohnhaftImGleichenHaushalt(this.getWohnhaftImGleichenHaushalt());
		mutation.setKinderabzug(this.getKinderabzug());
		mutation.setFamilienErgaenzendeBetreuung(this.getFamilienErgaenzendeBetreuung());
		mutation.setMutterspracheDeutsch(this.getMutterspracheDeutsch());
		mutation.setEinschulung(this.getEinschulung());
		if (this.getPensumFachstelle() != null) {
			mutation.setPensumFachstelle(this.getPensumFachstelle().copyForMutation(new PensumFachstelle()));
		}
		return mutation;
	}
}
