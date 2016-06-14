package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity fuer Kinder.
 */
@Audited
@Entity
public class Kind extends AbstractPersonEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@Max(100)
	@Min(0)
	@NotNull
	@Column(nullable = false)
	private Integer wohnhaftImGleichenHaushalt;

	@Column(nullable = true)
	@Nullable
	private Boolean unterstuetzungspflicht = false;

	@Column(nullable = false)
	@NotNull
	private Boolean familienErgaenzendeBetreuung = false;

	@Column(nullable = true)
	@Nullable
	private Boolean mutterspracheDeutsch;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private PensumFachstelle pensumFachstelle;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;



	public Integer getWohnhaftImGleichenHaushalt() {
		return wohnhaftImGleichenHaushalt;
	}

	public void setWohnhaftImGleichenHaushalt(Integer wohnhaftImGleichenHaushalt) {
		this.wohnhaftImGleichenHaushalt = wohnhaftImGleichenHaushalt;
	}

	@Nullable
	public Boolean getUnterstuetzungspflicht() {
		return unterstuetzungspflicht;
	}

	public void setUnterstuetzungspflicht(@Nullable Boolean unterstuetzungspflicht) {
		this.unterstuetzungspflicht = unterstuetzungspflicht;
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
