package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.DokumentGrundTyp;

import javax.annotation.Nullable;
import javax.persistence.*;
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

	@Nullable
	private String fullName;

	@Nullable
	private String tag;

	@Nullable
	private Set<JaxDokument> dokumente = new HashSet<>();

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
}
