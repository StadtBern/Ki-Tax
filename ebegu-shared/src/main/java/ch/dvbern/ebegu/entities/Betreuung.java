package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.validators.CheckBetreuungspensum;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlapping;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Entity fuer Betreuungen.
 */
@Audited
@Entity
@CheckBetreuungspensum
@CheckBetreuungspensumDatesOverlapping
@Table(
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"betreuungNummer", "kind_id"}, name = "UK_betreuung_kind_betreuung_nummer"),
		@UniqueConstraint(columnNames = {"verfuegung_id"}, name = "UK_betreuung_verfuegung_id")    //hibernate ignoriert den namen leider
	}
)
public class Betreuung extends AbstractEntity {

	private static final long serialVersionUID = -6776987863150835840L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_kind_id"), nullable = false)
	private KindContainer kind;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_institution_stammdaten_id"), nullable = false)
	private InstitutionStammdaten institutionStammdaten;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private Betreuungsstatus betreuungsstatus;

	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "betreuung")
	private Set<BetreuungspensumContainer> betreuungspensumContainers = new TreeSet<>();

	@Nullable
	@Column(nullable = true)
	private Boolean schulpflichtig = false;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer betreuungNummer = 1;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_verfuegung_id"), nullable = true, unique = true)
	private Verfuegung verfuegung;

	@NotNull
	@Column(nullable = false)
	private Boolean vertrag = false;

	@NotNull
	@Column(nullable = false)
	private Boolean erweiterteBeduerfnisse = false;



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

	@Nullable
	public Boolean getSchulpflichtig() {
		return schulpflichtig;
	}

	public void setSchulpflichtig(@Nullable Boolean schulpflichtig) {
		this.schulpflichtig = schulpflichtig;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public Integer getBetreuungNummer() {
		return betreuungNummer;
	}

	public void setBetreuungNummer(Integer betreuungNummer) {
		this.betreuungNummer = betreuungNummer;
	}

	public Verfuegung getVerfuegung() {
		return verfuegung;
	}

	public void setVerfuegung(Verfuegung verfuegung) {
		this.verfuegung = verfuegung;
	}

	public Boolean getVertrag() {
		return vertrag;
	}

	public void setVertrag(Boolean vertrag) {
		this.vertrag = vertrag;
	}

	public Boolean getErweiterteBeduerfnisse() {
		return erweiterteBeduerfnisse;
	}

	public void setErweiterteBeduerfnisse(Boolean erweiterteBeduerfnisse) {
		this.erweiterteBeduerfnisse = erweiterteBeduerfnisse;
	}

	public boolean isSame(Betreuung otherBetreuung) {
		if (this == otherBetreuung) {
			return true;
		}
		if (otherBetreuung == null || getClass() != otherBetreuung.getClass()) {
			return false;
		}

		boolean bemSame = Objects.equals(this.getBemerkungen(), otherBetreuung.getBemerkungen());
		boolean pensenSame =  this.getBetreuungspensumContainers().stream().allMatch(
			(pensCont) -> otherBetreuung.getBetreuungspensumContainers().stream().anyMatch(otherPensenCont -> otherPensenCont.isSame(pensCont)));
		boolean statusSame = Objects.equals(this.getBetreuungsstatus(), otherBetreuung.getBetreuungsstatus());
		boolean stammdatenSame = this.getInstitutionStammdaten().isSame(otherBetreuung.getInstitutionStammdaten());
		return bemSame && pensenSame && statusSame && stammdatenSame;
	}

	@Transient
	public Gesuchsperiode extractGesuchsperiode(){
		Objects.requireNonNull(this.getKind(), "Can not extract Gesuchsperiode because Kind is null");
		Objects.requireNonNull(this.getKind().getGesuch(), "Can not extract Gesuchsperiode because Gesuch is null");
		return this.getKind().getGesuch().getGesuchsperiode();
	}

	@Transient
	public Gesuch extractGesuch() {
		Objects.requireNonNull(this.getKind(), "Can not extract Gesuchsperiode because Kind is null");
		return this.getKind().getGesuch();
	}

	@Transient
	public boolean isAngebotKita() {
		return BetreuungsangebotTyp.KITA.equals(getInstitutionStammdaten().getBetreuungsangebotTyp());
	}

	@Transient
	public boolean isAngebotTageselternKleinkinder() {
		return BetreuungsangebotTyp.TAGESELTERN.equals(getInstitutionStammdaten().getBetreuungsangebotTyp()) &&
			getSchulpflichtig() != null && getSchulpflichtig().equals(Boolean.FALSE);
	}

	/**
	 * Erstellt die BetreuungsId als zusammengesetzten String aus Jahr, FallId, KindId und BetreuungsNummer
     */
	@Transient
	public String getBetreuungNummerTotal() {
		String year = ("" + getKind().getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()).substring(2);
		String fall = StringUtils.leftPad("" + getKind().getGesuch().getFall().getFallNummer(), Constants.FALLNUMMER_LENGTH, '0');
		String kind = "" + getKind().getKindNummer();
		String betreuung = "" + getBetreuungNummer();
		return year + "." + fall + "." + kind + "." + betreuung;
	}
}
