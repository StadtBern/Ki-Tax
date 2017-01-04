package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entitaet zum Speichern von Gesuch in der Datenbank.
 */
@Audited
@Entity
public class Gesuch extends AbstractEntity {

	private static final long serialVersionUID = -8403487439884700618L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_fall_id"))
	private Fall fall;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_antrag_gesuchsperiode_id"))
	private Gesuchsperiode gesuchsperiode;

	@Column(nullable = true)
	private LocalDate eingangsdatum;

	@Column(nullable = true)
	private LocalDate freigabeDatum;

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
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsteller_container1_id"), nullable = true)
	private GesuchstellerContainer gesuchsteller1;

	@Valid
	@Nullable
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsteller_container2_id"), nullable = true)
	private GesuchstellerContainer gesuchsteller2;

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

	@Column(nullable = false)
	private boolean hasFSDokument = true;


	public Gesuch() {
	}


	@Nullable
	public GesuchstellerContainer getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable GesuchstellerContainer gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public GesuchstellerContainer getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable GesuchstellerContainer gesuchsteller2) {
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

		if (this.dokumentGrunds == null) {
			this.dokumentGrunds = new HashSet<>();
		}

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

	public LocalDate getFreigabeDatum() {
		return freigabeDatum;
	}

	public void setFreigabeDatum(LocalDate freigabeDatum) {
		this.freigabeDatum = freigabeDatum;
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

	public int getLaufnummer() {
		return laufnummer;
	}

	public void setLaufnummer(int laufnummer) {
		this.laufnummer = laufnummer;
	}

	public boolean isHasFSDokument() {
		return hasFSDokument;
	}

	public void setHasFSDokument(boolean hasFSDokument) {
		this.hasFSDokument = hasFSDokument;
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
		String bothFamiliennamen = (this.getGesuchsteller1() != null ? this.getGesuchsteller1().extractNachname() : "");
		bothFamiliennamen += this.getGesuchsteller2() != null ? ", " + this.getGesuchsteller2().extractNachname() : "";
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

	/**
	 *
	 * @return false wenn es ein kind gibt dass eine nicht schulamt betreuung hat, wenn es kein kind oder betr gibt wird false zurueckgegeben
	 */
	@Transient
	public boolean hasOnlyBetreuungenOfSchulamt() {
		//noinspection SimplifyStreamApiCallChains
		List<Betreuung> allBetreuungen = kindContainers.stream().flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.collect(Collectors.toList());
		return !allBetreuungen.isEmpty() && allBetreuungen.stream().allMatch(betreuung -> betreuung.getBetreuungsangebotTyp().isSchulamt());

	}

	@Transient
	public boolean hasBetreuungOfSchulamt() {
		return kindContainers.stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.anyMatch(betreuung -> betreuung.getBetreuungsangebotTyp().isSchulamt());
	}

	public Familiensituation extractFamiliensituation() {
		if (familiensituationContainer != null) {
			return familiensituationContainer.extractFamiliensituation();
		}
		return null;
	}

	public Familiensituation extractFamiliensituationErstgesuch() {
		if (familiensituationContainer != null) {
			return familiensituationContainer.getFamiliensituationErstgesuch();
		}
		return null;
	}

	public void initFamiliensituationContainer() {
		familiensituationContainer = new FamiliensituationContainer();
		familiensituationContainer.setFamiliensituationJA(new Familiensituation());
	}

	public Gesuch copyForMutation(Gesuch mutation, Eingangsart eingangsart) {
		super.copyForMutation(mutation);
		mutation.setEingangsart(eingangsart);
		mutation.setFall(this.getFall());
		mutation.setGesuchsperiode(this.getGesuchsperiode());
		mutation.setEingangsdatum(null);
		mutation.setStatus(eingangsart == Eingangsart.PAPIER ?  AntragStatus.IN_BEARBEITUNG_JA : AntragStatus.IN_BEARBEITUNG_GS);
		mutation.setTyp(AntragTyp.MUTATION);
		mutation.setLaufnummer(this.getLaufnummer() + 1);

		if (this.getGesuchsteller1() != null) {
			mutation.setGesuchsteller1(this.getGesuchsteller1().copyForMutation(new GesuchstellerContainer()));
		}
		if (this.getGesuchsteller2() != null) {
			mutation.setGesuchsteller2(this.getGesuchsteller2().copyForMutation(new GesuchstellerContainer()));
		}
		for (KindContainer kindContainer : this.getKindContainers()) {
			mutation.addKindContainer(kindContainer.copyForMutation(new KindContainer(), mutation));
		}
		mutation.setAntragStatusHistories(new LinkedHashSet<>());

		if (this.getFamiliensituationContainer() != null) {
			mutation.setFamiliensituationContainer(this.getFamiliensituationContainer().copyForMutation(new FamiliensituationContainer(), this.isMutation()));
		}

		if (this.getEinkommensverschlechterungInfoContainer() != null) {
			mutation.setEinkommensverschlechterungInfoContainer(this.getEinkommensverschlechterungInfoContainer().copyForMutation(new EinkommensverschlechterungInfoContainer(), mutation));
		}

		if (this.getDokumentGrunds() != null) {
			mutation.setDokumentGrunds(new HashSet<>());
			for (DokumentGrund dokumentGrund : this.getDokumentGrunds()) {
				mutation.addDokumentGrund(dokumentGrund.copyForMutation(new DokumentGrund()));
			}
		}
		return mutation;
	}
}
