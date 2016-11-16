package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.validators.CheckAbwesenheitDatesOverlapping;
import ch.dvbern.ebegu.validators.CheckBetreuungspensum;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlapping;
import ch.dvbern.ebegu.validators.CheckGrundAblehnung;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Entity fuer Betreuungen.
 */
@Audited
@Entity
@CheckGrundAblehnung
@CheckBetreuungspensum
@CheckBetreuungspensumDatesOverlapping
@CheckAbwesenheitDatesOverlapping
@Table(
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"betreuungNummer", "kind_id"}, name = "UK_betreuung_kind_betreuung_nummer"),
		@UniqueConstraint(columnNames = {"verfuegung_id"}, name = "UK_betreuung_verfuegung_id")    //hibernate ignoriert den namen leider
	}
)
public class Betreuung extends AbstractEntity implements Comparable<Betreuung> {

	private static final long serialVersionUID = -6776987863150835840L;

	@Transient
	private Verfuegung vorgaengerVerfuegung;

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

	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "betreuung")
	private Set<AbwesenheitContainer> abwesenheitContainers = new TreeSet<>();

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String grundAblehnung;

	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer betreuungNummer = 1;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_verfuegung_id"), nullable = true, unique = true)
	private Verfuegung verfuegung;

	@NotNull
	@Column(nullable = false)
	private Boolean vertrag = false;

	@NotNull
	@Column(nullable = false)
	private Boolean erweiterteBeduerfnisse = false;

	@Nullable
	@Column(nullable = true)
	private LocalDate datumAblehnung;

	@Nullable
	@Column(nullable = true)
	private LocalDate datumBestaetigung;


	public Betreuung() {
	}


	public Betreuung(@Nonnull Betreuung toCopy, @Nonnull KindContainer kindContainer) {
		this.setVorgaengerId(toCopy.getId());
		this.kind = kindContainer;
		this.institutionStammdaten = toCopy.institutionStammdaten;
		// Bereits verfuegte Betreuungen werden als BESTAETIGT kopiert, alle anderen behalten ihren Status
		if (toCopy.betreuungsstatus.isGeschlossen()) {
			this.betreuungsstatus = Betreuungsstatus.BESTAETIGT;
		} else {
			this.betreuungsstatus = toCopy.betreuungsstatus;
		}
		for (BetreuungspensumContainer betreuungspensumContainer : toCopy.getBetreuungspensumContainers()) {
			this.betreuungspensumContainers.add(new BetreuungspensumContainer(betreuungspensumContainer, this));
		}
		for (AbwesenheitContainer abwesenheitContainer : toCopy.getAbwesenheitContainers()) {
			this.abwesenheitContainers.add(new AbwesenheitContainer(abwesenheitContainer, this));
		}
		this.grundAblehnung = toCopy.grundAblehnung;
		this.betreuungNummer = toCopy.betreuungNummer;
		this.verfuegung = null;
		this.vertrag = toCopy.vertrag;
		this.erweiterteBeduerfnisse = toCopy.erweiterteBeduerfnisse;
		this.datumAblehnung = toCopy.datumAblehnung;
		this.datumBestaetigung = toCopy.datumBestaetigung;
	}

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

	public Set<AbwesenheitContainer> getAbwesenheitContainers() {
		return abwesenheitContainers;
	}

	public void setAbwesenheitContainers(Set<AbwesenheitContainer> abwesenheiten) {
		this.abwesenheitContainers = abwesenheiten;
	}

	@Nullable
	public String getGrundAblehnung() {
		return grundAblehnung;
	}

	public void setGrundAblehnung(@Nullable String grundAblehnung) {
		this.grundAblehnung = grundAblehnung;
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

	@Nullable
	public LocalDate getDatumAblehnung() {
		return datumAblehnung;
	}

	public void setDatumAblehnung(@Nullable LocalDate datumAblehnung) {
		this.datumAblehnung = datumAblehnung;
	}

	@Nullable
	public LocalDate getDatumBestaetigung() {
		return datumBestaetigung;
	}

	public void setDatumBestaetigung(@Nullable LocalDate datumBestaetigung) {
		this.datumBestaetigung = datumBestaetigung;
	}


	public boolean isSame(Betreuung otherBetreuung) {
		if (this == otherBetreuung) {
			return true;
		}
		if (otherBetreuung == null || getClass() != otherBetreuung.getClass()) {
			return false;
		}

		boolean pensenSame = this.getBetreuungspensumContainers().stream().allMatch(
			(pensCont) -> otherBetreuung.getBetreuungspensumContainers().stream().anyMatch(otherPensenCont -> otherPensenCont.isSame(pensCont)));
		boolean statusSame = Objects.equals(this.getBetreuungsstatus(), otherBetreuung.getBetreuungsstatus());
		boolean stammdatenSame = this.getInstitutionStammdaten().isSame(otherBetreuung.getInstitutionStammdaten());
		return pensenSame && statusSame && stammdatenSame;
	}

	@Transient
	public Gesuchsperiode extractGesuchsperiode() {
		Objects.requireNonNull(this.getKind(), "Can not extract Gesuchsperiode because Kind is null");
		Objects.requireNonNull(this.getKind().getGesuch(), "Can not extract Gesuchsperiode because Gesuch is null");
		return this.getKind().getGesuch().getGesuchsperiode();
	}

	@Transient
	public Gesuch extractGesuch() {
		Objects.requireNonNull(this.getKind(), "Can not extract Gesuch because Kind is null");
		return this.getKind().getGesuch();
	}

	@Transient
	public boolean isAngebotKita() {
		return BetreuungsangebotTyp.KITA.equals(getBetreuungsangebotTyp());
	}

	@Transient
	public boolean isAngebotTageselternKleinkinder() {
		return BetreuungsangebotTyp.TAGESELTERN_KLEINKIND.equals(getBetreuungsangebotTyp());
	}

	@Transient
	public BetreuungsangebotTyp getBetreuungsangebotTyp() {
		if (getInstitutionStammdaten() != null) {
			return getInstitutionStammdaten().getBetreuungsangebotTyp();
		}
		return null;
	}

	/**
	 * Erstellt die BG-Nummer als zusammengesetzten String aus Jahr, FallId, KindId und BetreuungsNummer
	 */
	@Transient
	public String getBGNummer() {
		if (getKind().getGesuch() != null) {
			String kind = "" + getKind().getKindNummer();
			String betreuung = "" + getBetreuungNummer();
			return getKind().getGesuch().getAntragNummer() + "." + kind + "." + betreuung;
		}
		return "";
	}

	@Override
	public int compareTo(Betreuung other) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getBetreuungNummer(), other.getBetreuungNummer());
		compareToBuilder.append(this.getId(), other.getId());
		return compareToBuilder.toComparison();
	}

	/**
	 * @return die Verfuegung oder Vorgaengerverfuegung dieser Betreuung
	 */
	@Nullable
	public Verfuegung getVerfuegungOrVorgaengerVerfuegung() {
		if (getVerfuegung() != null) {
			return getVerfuegung();
		} else {
			return getVorgaengerVerfuegung();
		}
	}


	public Verfuegung getVorgaengerVerfuegung() {
		return vorgaengerVerfuegung;
	}

	public void setVorgaengerVerfuegung(Verfuegung vorgaengerVerfuegung) {
		this.vorgaengerVerfuegung = vorgaengerVerfuegung;
	}

}
