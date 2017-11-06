/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.entities;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.dto.suchfilter.lucene.BGNummerBridge;
import ch.dvbern.ebegu.dto.suchfilter.lucene.EBEGUGermanAnalyzer;
import ch.dvbern.ebegu.dto.suchfilter.lucene.Searchable;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.validators.CheckAbwesenheitDatesOverlapping;
import ch.dvbern.ebegu.validators.CheckBetreuungspensum;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumDatesOverlapping;
import ch.dvbern.ebegu.validators.CheckGrundAblehnung;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ClassBridge;
import org.hibernate.search.annotations.Indexed;

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
		@UniqueConstraint(columnNames = { "betreuungNummer", "kind_id" }, name = "UK_betreuung_kind_betreuung_nummer"),
		@UniqueConstraint(columnNames = { "verfuegung_id" }, name = "UK_betreuung_verfuegung_id")    //hibernate ignoriert den namen leider
	}
)
@Indexed()
@Analyzer(impl = EBEGUGermanAnalyzer.class)
@ClassBridge(name = "bGNummer", impl = BGNummerBridge.class, analyze = Analyze.NO)
public class Betreuung extends AbstractEntity implements Comparable<Betreuung>, Searchable {

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
	@SortNatural
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

	@Nullable
	@Valid
	@OneToOne(optional = true, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_verfuegung_id"), nullable = true)
	private Verfuegung verfuegung;

	@Nullable
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_belegung_tagesschule_id"), nullable = true)
	private BelegungTagesschule belegungTagesschule;

	@Nullable
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuung_belegung_ferieninsel_id"), nullable = true)
	private BelegungFerieninsel belegungFerieninsel;

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

	@Nullable
	@Column(nullable = true)
	private Boolean betreuungMutiert;

	@Nullable
	@Column(nullable = true)
	private Boolean abwesenheitMutiert;

	@Column(nullable = false)
	private boolean gueltig = false;

	public Betreuung() {
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

	@Nullable
	public Verfuegung getVerfuegung() {
		return verfuegung;
	}

	public void setVerfuegung(@Nullable Verfuegung verfuegung) {
		this.verfuegung = verfuegung;
	}

	@Nullable
	public BelegungTagesschule getBelegungTagesschule() {
		return belegungTagesschule;
	}

	public void setBelegungTagesschule(@Nullable BelegungTagesschule belegungTagesschule) {
		this.belegungTagesschule = belegungTagesschule;
	}

	@Nullable
	public BelegungFerieninsel getBelegungFerieninsel() {
		return belegungFerieninsel;
	}

	public void setBelegungFerieninsel(@Nullable BelegungFerieninsel belegungFerieninsel) {
		this.belegungFerieninsel = belegungFerieninsel;
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

	@Nullable
	public Boolean getBetreuungMutiert() {
		return betreuungMutiert;
	}

	public void setBetreuungMutiert(@Nullable Boolean betreuungMutiert) {
		this.betreuungMutiert = betreuungMutiert;
	}

	@Nullable
	public Boolean getAbwesenheitMutiert() {
		return abwesenheitMutiert;
	}

	public void setAbwesenheitMutiert(@Nullable Boolean abwesenheitMutiert) {
		this.abwesenheitMutiert = abwesenheitMutiert;
	}

	public boolean isGueltig() {
		return gueltig;
	}

	public void setGueltig(boolean gueltig) {
		this.gueltig = gueltig;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//by default just the fields that belong to the Betreuung itself
		return this.isSame(other, false, false);
	}

	public boolean isSame(AbstractEntity other, boolean inklAbwesenheiten, boolean inklStatus) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof Betreuung)) {
			return false;
		}
		final Betreuung otherBetreuung = (Betreuung) other;

		boolean pensenSame = this.getBetreuungspensumContainers().stream().allMatch(
			(pensCont) -> otherBetreuung.getBetreuungspensumContainers().stream().anyMatch(otherPensenCont -> otherPensenCont.isSame(pensCont)));

		boolean abwesenheitenSame = true;
		if (inklAbwesenheiten) {
			abwesenheitenSame = this.getAbwesenheitContainers().stream().allMatch(
				(abwesenheitCont) -> otherBetreuung.getAbwesenheitContainers().stream().anyMatch(otherAbwesenheitCont -> otherAbwesenheitCont.isSame(abwesenheitCont)));
		}
		boolean statusSame = true;
		if (inklStatus) {
			statusSame = Objects.equals(this.getBetreuungsstatus(), otherBetreuung.getBetreuungsstatus());
		}
		boolean stammdatenSame = this.getInstitutionStammdaten().isSame(otherBetreuung.getInstitutionStammdaten());
		boolean erwBeduerfnisseSame = Objects.equals(getErweiterteBeduerfnisse(), otherBetreuung
			.getErweiterteBeduerfnisse());
		return pensenSame && abwesenheitenSame && statusSame && stammdatenSame && erwBeduerfnisseSame;
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

	@Nullable
	@Transient
	public BetreuungsangebotTyp getBetreuungsangebotTyp() {
		if (getInstitutionStammdaten() != null) {
			return getInstitutionStammdaten().getBetreuungsangebotTyp();
		}
		return null;
	}

	@Transient
	public String getBetreuungsangebotTypTranslated() {
		return ServerMessageUtil.translateEnumValue(getBetreuungsangebotTyp());
	}

	/**
	 * Erstellt die BG-Nummer als zusammengesetzten String aus Jahr, FallId, KindId und BetreuungsNummer
	 */
	@Transient
	@SuppressFBWarnings("NM_CONFUSING")
	public String getBGNummer() {
		if (getKind().getGesuch() != null) {
			String kindNumberAsString = String.valueOf(getKind().getKindNummer());
			String betreuung = String.valueOf(getBetreuungNummer());
			return getKind().getGesuch().getJahrAndFallnummer() + '.' + kindNumberAsString + '.' + betreuung;
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

	public Betreuung copyForMutation(@Nonnull Betreuung mutation, @Nonnull KindContainer kindContainerMutation) {
		super.copyForMutation(mutation);
		mutation.setKind(kindContainerMutation);
		mutation.setInstitutionStammdaten(this.getInstitutionStammdaten());
		// Bereits verfuegte Betreuungen werden als BESTAETIGT kopiert, alle anderen behalten ihren Status
		if (this.getBetreuungsstatus().isGeschlossen()) {
			// Falls sämtliche Betreuungspensum-Container dieser Betreuung ein effektives Pensum von 0 haben, handelt es sich um die
			// Verfügung eines stornierten Platzes. Wir übernehmen diesen als "STORNIERT"
			if (hasAnyNonZeroPensum()) {
				mutation.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
			} else {
				mutation.setBetreuungsstatus(Betreuungsstatus.STORNIERT);
			}
		} else {
			mutation.setBetreuungsstatus(this.getBetreuungsstatus());
		}
		for (BetreuungspensumContainer betreuungspensumContainer : this.getBetreuungspensumContainers()) {
			mutation.getBetreuungspensumContainers().add(betreuungspensumContainer.copyForMutation(new BetreuungspensumContainer(), mutation));
		}
		for (AbwesenheitContainer abwesenheitContainer : this.getAbwesenheitContainers()) {
			mutation.getAbwesenheitContainers().add(abwesenheitContainer.copyForMutation(new AbwesenheitContainer(), mutation));
		}
		mutation.setGrundAblehnung(this.getGrundAblehnung());
		mutation.setBetreuungNummer(this.getBetreuungNummer());
		mutation.setVerfuegung(null);
		mutation.setVertrag(this.getVertrag());
		mutation.setErweiterteBeduerfnisse(this.getErweiterteBeduerfnisse());
		mutation.setDatumAblehnung(this.getDatumAblehnung());
		mutation.setDatumBestaetigung(this.getDatumBestaetigung());
		mutation.setBetreuungMutiert(null);
		mutation.setAbwesenheitMutiert(null);
		mutation.setGueltig(false);
		return mutation;
	}

	private boolean hasAnyNonZeroPensum() {
		for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
			if (betreuungspensumContainer.getBetreuungspensumJA().getPensum() > 0) {
				return true;
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public String getSearchResultId() {
		return getId();
	}

	@Nonnull
	@Override
	public String getSearchResultSummary() {
		return getKind().getSearchResultSummary() + " " + getBGNummer();
	}

	@Nullable
	@Override
	public String getSearchResultAdditionalInformation() {
		return toString();
	}

	@Override
	public String getOwningGesuchId() {
		return extractGesuch().getId();
	}

	@Override
	public String getOwningFallId() {
		return extractGesuch().getFall().getId();
	}
}
