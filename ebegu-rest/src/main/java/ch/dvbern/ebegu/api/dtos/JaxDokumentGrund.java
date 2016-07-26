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
public class JaxDokumentGrund {


	@Enumerated(value = EnumType.STRING)
	@NotNull
	private DokumentGrundTyp dokumentGrundTyp;

	@Nullable
	private String fullname;

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
	public String getFullname() {
		return fullname;
	}

	public void setFullname(@Nullable String fullname) {
		this.fullname = fullname;
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
