package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Stammdaten der Kinder
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxKind extends JaxAbstractPersonDTO {

	private static final long serialVersionUID = -1297026881674137397L;

	@NotNull
	private Integer wohnhaftImGleichenHaushalt;

	@Nullable
	private Boolean unterstuetzungspflicht;

	@NotNull
	private Boolean familienErgaenzendeBetreuung;

	@Nullable
	private Boolean mutterspracheDeutsch;

	@Nullable
	private JaxPensumFachstelle pensumFachstelle;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
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

	@Nullable
	public JaxPensumFachstelle getPensumFachstelle() {
		return pensumFachstelle;
	}

	public void setPensumFachstelle(@Nullable JaxPensumFachstelle pensumFachstelle) {
		this.pensumFachstelle = pensumFachstelle;
	}
}
