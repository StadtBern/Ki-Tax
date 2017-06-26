package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import javax.annotation.Nullable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "dokumentGrund")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDokumentGrund extends JaxAbstractDTO {


	private static final long serialVersionUID = -1451729857998697429L;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private DokumentGrundTyp dokumentGrundTyp;

	@NotNull
	private DokumentTyp dokumentTyp;

	@Nullable
	private String fullName;

	@Nullable
	private String tag;

	@Nullable
	private DokumentGrundPersonType personType;

	@Nullable
	private Integer personNumber;

	@Nullable
	private Set<JaxDokument> dokumente = new HashSet<>();

	private boolean needed = true;

	public DokumentGrundTyp getDokumentGrundTyp() {
		return dokumentGrundTyp;
	}

	public void setDokumentGrundTyp(DokumentGrundTyp dokumentGrundTyp) {
		this.dokumentGrundTyp = dokumentGrundTyp;
	}

	@Nullable
	public String getFullName() {
		return fullName;
	}

	public void setFullName(@Nullable String fullName) {
		this.fullName = fullName;
	}

	@Nullable
	public String getTag() {
		return tag;
	}

	public void setTag(@Nullable String tag) {
		this.tag = tag;
	}

	@Nullable
	public Set<JaxDokument> getDokumente() {
		return dokumente;
	}

	public void setDokumente(@Nullable Set<JaxDokument> dokumente) {
		this.dokumente = dokumente;
	}

	public DokumentTyp getDokumentTyp() {
		return dokumentTyp;
	}

	public void setDokumentTyp(DokumentTyp dokumentTyp) {
		this.dokumentTyp = dokumentTyp;
	}

	public boolean isNeeded() {
		return needed;
	}

	public void setNeeded(boolean needed) {
		this.needed = needed;
	}

	public boolean isEmpty() {
		return getDokumente() == null || getDokumente().size() <= 0;
	}

	@Nullable
	public DokumentGrundPersonType getPersonType() {
		return personType;
	}

	public void setPersonType(@Nullable DokumentGrundPersonType personType) {
		this.personType = personType;
	}

	@Nullable
	public Integer getPersonNumber() {
		return personNumber;
	}

	public void setPersonNumber(@Nullable Integer personNumber) {
		this.personNumber = personNumber;
	}
}
