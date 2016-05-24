package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity fuer Betreuungen.
 */
@Audited
@Entity
public class Betreuung extends AbstractEntity {

	private static final long serialVersionUID = -6776987863150835840L;

	@NotNull
	@ManyToOne(optional = false)
	private KindContainer kind;

	@NotNull
	@ManyToOne(optional = false)
	private InstitutionStammdaten institutionStammdaten;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private Betreuungsstatus betreuungsstatus;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "betreuung")
	private Set<BetreuungspensumContainer> betreuungspensumContainers = new HashSet<>();

	@NotNull
	@Column(nullable = false)
	private Boolean schulpflichtig = false;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;


	public KindContainer getKind() {
		return kind;
	}

	public void setKind(KindContainer kind) {
		this.kind = kind;
	}

	public InstitutionStammdaten getInstitutionStammdaten() {
		return institutionStammdaten;
	}

	public void setInstitutionStammdaten(InstitutionStammdaten institutionStammdaten) {
		this.institutionStammdaten = institutionStammdaten;
	}

	public Betreuungsstatus getBetreuungsstatus() {
		return betreuungsstatus;
	}

	public void setBetreuungsstatus(Betreuungsstatus betreuungsstatus) {
		this.betreuungsstatus = betreuungsstatus;
	}

	public Set<BetreuungspensumContainer> getBetreuungspensumContainers() {
		return betreuungspensumContainers;
	}

	public void setBetreuungspensumContainers(Set<BetreuungspensumContainer> betreuungspensumContainers) {
		this.betreuungspensumContainers = betreuungspensumContainers;
	}

	public Boolean getSchulpflichtig() {
		return schulpflichtig;
	}

	public void setSchulpflichtig(Boolean schulpflichtig) {
		this.schulpflichtig = schulpflichtig;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}
}
