package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

/**
 * Entitaet zum Speichern von Gesuch in der Datenbank.
 */
@Audited
@Entity
public class Gesuch extends AbstractEntity {

	private static final long serialVersionUID = -8403487439884700618L;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_fall_id"))
	private Fall fall;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_antrag_gesuchsperiode_id"))
	private Gesuchsperiode gesuchsperiode;

	@Column(nullable = true)
	private LocalDate eingangsdatum;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AntragStatus status;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AntragTyp typ = AntragTyp.GESUCH;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Eingangsart eingangsart = Eingangsart.PAPIER;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsteller1_id"))
	private Gesuchsteller gesuchsteller1;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsteller2_id"))
	private Gesuchsteller gesuchsteller2;

	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuch")
	@OrderBy("kindNummer")
	private Set<KindContainer> kindContainers = new LinkedHashSet<>();

	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "gesuch", fetch = FetchType.LAZY)
	@OrderBy("datum")
	private Set<AntragStatusHistory> antragStatusHistories = new LinkedHashSet<>();

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_familiensituation_container_id"))
	private FamiliensituationContainer familiensituationContainer;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_einkommensverschlechterungInfoContainer_id"))
	private EinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainer;

	@Transient
	private FinanzDatenDTO finanzDatenDTO;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

	@Nullable
	@Valid
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "gesuch")
	private Set<DokumentGrund> dokumentGrunds;

	@NotNull
	@Min(0)
	@Column(nullable = false)
	private int laufnummer = 0;


	public Gesuch() {
	}

	public Gesuch(@Nonnull Gesuch toCopy) {
		//TODO (hefr) Eingangsart???
		this.setVorgaengerId(toCopy.getId());
		this.setFall(toCopy.getFall());
		this.setGesuchsperiode(toCopy.getGesuchsperiode());
		this.setEingangsdatum(null);
		this.setStatus(AntragStatus.IN_BEARBEITUNG_JA); //TODO (team) abhaengig vom eingeloggten Benutzer!
		this.setTyp(AntragTyp.MUTATION);

		if (toCopy.getGesuchsteller1() != null) {
			this.setGesuchsteller1(new Gesuchsteller(toCopy.getGesuchsteller1()));
		}
		if (toCopy.getGesuchsteller2() != null) {
			this.setGesuchsteller2(new Gesuchsteller(toCopy.getGesuchsteller2()));
		}
		for (KindContainer kindContainer : toCopy.getKindContainers()) {
			this.addKindContainer(new KindContainer(kindContainer, this));
		}
		this.setAntragStatusHistories(new LinkedHashSet<>());
		this.setFamiliensituationContainer(new FamiliensituationContainer(toCopy.getFamiliensituationContainer(), toCopy.isMutation()));

		if (toCopy.getEinkommensverschlechterungInfoContainer() != null) {
			this.setEinkommensverschlechterungInfoContainer(new EinkommensverschlechterungInfoContainer(toCopy.getEinkommensverschlechterungInfoContainer()));
		}

		if (toCopy.dokumentGrunds != null) {
			this.dokumentGrunds = new HashSet<>();

			for (DokumentGrund dokumentGrund : toCopy.dokumentGrunds) {
				this.addDokumentGrund(new DokumentGrund(dokumentGrund));
			}
		}

		this.setBemerkungen("Mutation des Gesuchs vom " + toCopy.getEingangsdatum()); //TODO hefr test only!
		this.setLaufnummer(toCopy.getLaufnummer() + 1);
	}

	@Nullable
	public Gesuchsteller getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable final Gesuchsteller gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public Gesuchsteller getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable final Gesuchsteller gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	public Set<KindContainer> getKindContainers() {
		return kindContainers;
	}

	public void setKindContainers(final Set<KindContainer> kindContainers) {
		this.kindContainers = kindContainers;
	}

	@Nullable
	public FamiliensituationContainer getFamiliensituationContainer() {
		return familiensituationContainer;
	}

	public void setFamiliensituationContainer(@Nullable FamiliensituationContainer familiensituationContainer) {
		this.familiensituationContainer = familiensituationContainer;
	}

	public Set<AntragStatusHistory> getAntragStatusHistories() {
		return antragStatusHistories;
	}

	public void setAntragStatusHistories(Set<AntragStatusHistory> antragStatusHistories) {
		this.antragStatusHistories = antragStatusHistories;
	}

	@Nullable
	public EinkommensverschlechterungInfo extractEinkommensverschlechterungInfo() {
		if (einkommensverschlechterungInfoContainer != null) {
			return einkommensverschlechterungInfoContainer.getEinkommensverschlechterungInfoJA();
		}
		return null;
	}

	public boolean addKindContainer(@NotNull final KindContainer kindContainer) {
		kindContainer.setGesuch(this);
		return this.kindContainers.add(kindContainer);
	}

	public boolean addDokumentGrund(@NotNull final DokumentGrund dokumentGrund) {
		dokumentGrund.setGesuch(this);
		return this.dokumentGrunds.add(dokumentGrund);
	}

	public FinanzDatenDTO getFinanzDatenDTO() {
		return finanzDatenDTO;
	}

	public void setFinanzDatenDTO(FinanzDatenDTO finanzDatenDTO) {
		this.finanzDatenDTO = finanzDatenDTO;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public Fall getFall() {
		return fall;
	}

	public final void setFall(Fall fall) {
		this.fall = fall;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public final void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public final void setEingangsdatum(LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	public AntragStatus getStatus() {
		return status;
	}

	public final void setStatus(AntragStatus status) {
		this.status = status;
	}

	public AntragTyp getTyp() {
		return typ;
	}

	public final void setTyp(AntragTyp typ) {
		this.typ = typ;
	}

	public Eingangsart getEingangsart() {
		return eingangsart;
	}

	public void setEingangsart(Eingangsart eingangsart) {
		this.eingangsart = eingangsart;
	}

	@Nullable
	public Set<DokumentGrund> getDokumentGrunds() {
		return dokumentGrunds;
	}

	public void setDokumentGrunds(@Nullable Set<DokumentGrund> dokumentGrunds) {
		this.dokumentGrunds = dokumentGrunds;
	}

	@Nullable
	public int getLaufnummer() {
		return laufnummer;
	}

	public void setLaufnummer(@Nullable int laufnummer) {
		this.laufnummer = laufnummer;
	}

	@Nullable
	public EinkommensverschlechterungInfoContainer getEinkommensverschlechterungInfoContainer() {
		return einkommensverschlechterungInfoContainer;
	}

	public void setEinkommensverschlechterungInfoContainer(@Nullable EinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainer) {
		this.einkommensverschlechterungInfoContainer = einkommensverschlechterungInfoContainer;
	}

	@SuppressWarnings("ObjectEquality")
	public boolean isSame(Gesuch otherAntrag) {
		if (this == otherAntrag) {
			return true;
		}
		if (otherAntrag == null || getClass() != otherAntrag.getClass()) {
			return false;
		}
		return (Objects.equals(this.getEingangsdatum(), otherAntrag.getEingangsdatum())
			&& Objects.equals(this.getFall(), otherAntrag.getFall())
			&& Objects.equals(this.getGesuchsperiode(), otherAntrag.getGesuchsperiode()));
	}

	public String getAntragNummer() {
		if (getGesuchsperiode() == null) {
			return "-";
		}
		return Integer.toString(getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()).substring(2)
			+ "." + StringUtils.leftPad("" + getFall().getFallNummer(), Constants.FALLNUMMER_LENGTH, '0');
	}

	@Transient
	public List<Betreuung> extractAllBetreuungen() {
		final List<Betreuung> list = new ArrayList<>();
		for (final KindContainer kind : getKindContainers()) {
			list.addAll(kind.getBetreuungen());
		}
		return list;
	}

	@Transient
	public Betreuung extractBetreuungById(String betreuungId) {
		for (KindContainer kind : getKindContainers()) {
			for (Betreuung betreuung : kind.getBetreuungen()) {
				if (betreuung.getId().equals(betreuungId)) {
					return betreuung;
				}
			}
		}
		return null;
	}

	/**
	 * @return Den Familiennamen beider Gesuchsteller falls es 2 gibt, sonst Familiennamen von GS1
	 */
	@Transient
	public String extractFamiliennamenString() {
		String bothFamiliennamen = (this.getGesuchsteller1() != null ? this.getGesuchsteller1().getNachname() : "");
		bothFamiliennamen += this.getGesuchsteller2() != null ? ", " + this.getGesuchsteller2().getNachname() : "";
		return bothFamiliennamen;
	}

	@Transient
	public boolean isMutation() {
		return this.typ == AntragTyp.MUTATION;
	}

	@Transient
	public boolean hasBetreuungOfInstitution(@Nullable final Institution institution) {
		if (institution == null) {
			return false;
		}
		return kindContainers.stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.anyMatch(betreuung -> betreuung.getInstitutionStammdaten().getInstitution().equals(institution));

	}

	@Transient
	public boolean hasBetreuungOfSchulamt() {
		return kindContainers.stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.anyMatch(betreuung -> betreuung.getBetreuungsangebotTyp().isSchulamt());
	}

	public Familiensituation extractFamiliensituation() {
		if(familiensituationContainer != null){
			return familiensituationContainer.extractFamiliensituation();
		}
		return null;
	}

	public Familiensituation extractFamiliensituationErstgesuch() {
		if(familiensituationContainer != null){
			return familiensituationContainer.getFamiliensituationErstgesuch();
		}
		return null;
	}

	public void initFamiliensituationContainer() {
		familiensituationContainer = new FamiliensituationContainer();
		familiensituationContainer.setFamiliensituationJA(new Familiensituation());
	}
}
