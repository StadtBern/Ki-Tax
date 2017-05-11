package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.dto.suchfilter.lucene.EBEGUGermanAnalyzer;
import ch.dvbern.ebegu.dto.suchfilter.lucene.Searchable;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entitaet zum Speichern von GesuchContainer in der Datenbank.
 */
@Audited
@Entity
@Indexed
@Analyzer(impl = EBEGUGermanAnalyzer.class)
public class GesuchstellerContainer extends AbstractEntity implements Searchable {

	private static final long serialVersionUID = -8403117439764700618L;


	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchsteller_container_gesuchstellergs_id"), nullable = true)
	private Gesuchsteller gesuchstellerGS;

	@Valid
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchsteller_container_gesuchstellerja_id"), nullable = true)
	@IndexedEmbedded
	private Gesuchsteller gesuchstellerJA;

	@Nullable
	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchstellerContainer")
	private FinanzielleSituationContainer finanzielleSituationContainer;

	@Nullable
	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchstellerContainer")
	private EinkommensverschlechterungContainer einkommensverschlechterungContainer;

	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchstellerContainer")
	private Set<ErwerbspensumContainer> erwerbspensenContainers = new HashSet<>();

	@Valid
	@Size(min = 1)
	@Nonnull
	// es handelt sich um eine "private" Relation, das heisst Adressen koennen nie einer anderen Gesuchsteller zugeordnet werden
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchstellerContainer")
	private List<GesuchstellerAdresseContainer> adressen = new ArrayList<>();



	public GesuchstellerContainer() {}

	public boolean addAdresse(@Nonnull final GesuchstellerAdresseContainer gesuchstellerAdresseContainer) {
		gesuchstellerAdresseContainer.setGesuchstellerContainer(this);
		return !adressen.contains(gesuchstellerAdresseContainer) && adressen.add(gesuchstellerAdresseContainer);
	}

	public Gesuchsteller getGesuchstellerGS() {
		return gesuchstellerGS;
	}

	public void setGesuchstellerGS(Gesuchsteller gesuchstellerGS) {
		this.gesuchstellerGS = gesuchstellerGS;
	}

	public Gesuchsteller getGesuchstellerJA() {
		return gesuchstellerJA;
	}

	public void setGesuchstellerJA(Gesuchsteller gesuchstellerJA) {
		this.gesuchstellerJA = gesuchstellerJA;
	}

	@Nonnull
	public List<GesuchstellerAdresseContainer> getAdressen() {
		return adressen;
	}

	public void setAdressen(@Nonnull final List<GesuchstellerAdresseContainer> adressen) {
		this.adressen = adressen;
	}

	@Nullable
	public FinanzielleSituationContainer getFinanzielleSituationContainer() {
		return finanzielleSituationContainer;
	}

	@Nonnull
	public Set<ErwerbspensumContainer> getErwerbspensenContainers() {
		return erwerbspensenContainers;
	}

	@Nonnull
	public Set<ErwerbspensumContainer> getErwerbspensenContainersNotEmpty() {
		if (!erwerbspensenContainers.isEmpty()) {
			return erwerbspensenContainers;
		}

		final Set<ErwerbspensumContainer> erwerbspensen = new HashSet();
		final ErwerbspensumContainer erwerbspensum = new ErwerbspensumContainer();
		Erwerbspensum pensumJA = new Erwerbspensum();
		pensumJA.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		pensumJA.setPensum(0);
		erwerbspensum.setErwerbspensumJA(pensumJA);
		erwerbspensen.add(erwerbspensum);
		return erwerbspensen;
	}

	public void setErwerbspensenContainers(@Nonnull final Set<ErwerbspensumContainer> erwerbspensenContainers) {
		this.erwerbspensenContainers = erwerbspensenContainers;
	}

	public void setFinanzielleSituationContainer(@Nullable final FinanzielleSituationContainer finanzielleSituationContainer) {
		this.finanzielleSituationContainer = finanzielleSituationContainer;
		if (finanzielleSituationContainer != null &&
			(finanzielleSituationContainer.getGesuchsteller() == null || !finanzielleSituationContainer.getGesuchsteller().equals(this))) {
			finanzielleSituationContainer.setGesuchsteller(this);
		}
	}

	public boolean addErwerbspensumContainer(final ErwerbspensumContainer erwerbspensumToAdd) {
		erwerbspensumToAdd.setGesuchsteller(this);
		return !erwerbspensenContainers.contains(erwerbspensumToAdd) &&
			erwerbspensenContainers.add(erwerbspensumToAdd);
	}

	@Nullable
	public EinkommensverschlechterungContainer getEinkommensverschlechterungContainer() {
		return einkommensverschlechterungContainer;
	}

	public void setEinkommensverschlechterungContainer(@Nullable final EinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		this.einkommensverschlechterungContainer = einkommensverschlechterungContainer;
		if (einkommensverschlechterungContainer != null &&
			(einkommensverschlechterungContainer.getGesuchsteller() == null || !einkommensverschlechterungContainer.getGesuchsteller().equals(this))) {
			einkommensverschlechterungContainer.setGesuchsteller(this);
		}
	}

	/**
	 * Gibt den Namen des GesuchstellerJA oder ein Leerzeichen wenn er nicht existiert
	 */
	public String extractNachname() {
		if (this.gesuchstellerJA != null) {
			return this.gesuchstellerJA.getNachname();
		}
		return "";
	}

	/**
	 * Gibt den FullNamen des GesuchstellerJA oder ein Leerzeichen wenn er nicht existiert
	 */
	public String extractFullName() {
		if (this.gesuchstellerJA != null) {
			return this.gesuchstellerJA.getFullName();
		}
		return "";
	}

	@Nonnull
	public GesuchstellerContainer copyForMutation(@Nonnull GesuchstellerContainer mutation) {
		super.copyForMutation(mutation);
		mutation.setVorgaengerId(this.getId());
		mutation.setGesuchstellerGS(null);
		if (this.getGesuchstellerJA() != null) {
			mutation.setGesuchstellerJA(this.getGesuchstellerJA().copyForMutation(new Gesuchsteller()));
		}
		if (this.getFinanzielleSituationContainer() != null) {
			mutation.setFinanzielleSituationContainer(this.getFinanzielleSituationContainer().copyForMutation(new FinanzielleSituationContainer(), this));
		}
		if (this.getEinkommensverschlechterungContainer() != null) {
			mutation.setEinkommensverschlechterungContainer(this.getEinkommensverschlechterungContainer().copyForMutation(new EinkommensverschlechterungContainer(), this));
		}
		for (ErwerbspensumContainer erwerbspensumContainer : this.getErwerbspensenContainers()) {
			mutation.addErwerbspensumContainer(erwerbspensumContainer.copyForMutation(new ErwerbspensumContainer(), this));
		}
		for (GesuchstellerAdresseContainer gesuchstellerAdresse : this.getAdressen()) {
			if (gesuchstellerAdresse.getGesuchstellerAdresseJA() != null) {
				mutation.addAdresse(gesuchstellerAdresse.copyForMutation(new GesuchstellerAdresseContainer(), this));
			}
		}
		return mutation;
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Nonnull
	public GesuchstellerContainer copyForErneuerung(@Nonnull GesuchstellerContainer folgegesuch, @Nonnull Gesuchsperiode gesuchsperiodeFolgegesuch) {
		super.copyForErneuerung(folgegesuch);
		folgegesuch.setGesuchstellerGS(null);
		if (this.getGesuchstellerJA() != null) {
			folgegesuch.setGesuchstellerJA(this.getGesuchstellerJA().copyForErneuerung(new Gesuchsteller()));
		}
		for (GesuchstellerAdresseContainer gesuchstellerAdresse : this.getAdressen()) {
			if (gesuchstellerAdresse.getGesuchstellerAdresseJA() != null) {
				// Nur aktuelle und zukuenftige Adressen kopieren
				if (!gesuchstellerAdresse.extractGueltigkeit().endsBefore(gesuchsperiodeFolgegesuch.getGueltigkeit().getGueltigAb())) {
					GesuchstellerAdresseContainer adresseContainer = gesuchstellerAdresse.copyForErneuerung(new GesuchstellerAdresseContainer(), this);
					folgegesuch.addAdresse(adresseContainer);
				}
			}
		}
		return folgegesuch;
	}

	@Nullable
	public GesuchstellerAdresse getWohnadresseAm(LocalDate stichtag) {
		for (GesuchstellerAdresseContainer adresse : getAdressen()) {
			if (AdresseTyp.WOHNADRESSE.equals(adresse.extractAdresseTyp()) && adresse.extractGueltigkeit().contains(stichtag)) {
				return adresse.getGesuchstellerAdresseJA();
			}
		}
		return null;
	}

	@Nonnull
	public List<Erwerbspensum> getErwerbspensenAm(LocalDate stichtag) {
		List<Erwerbspensum> erwerbspensenInZeitraum = new ArrayList<>();
		for (ErwerbspensumContainer erwerbspensumContainer : getErwerbspensenContainersNotEmpty()) {
			if (erwerbspensumContainer.getErwerbspensumJA().getGueltigkeit().contains(stichtag)) {
				erwerbspensenInZeitraum.add(erwerbspensumContainer.getErwerbspensumJA());
			}
		}
		return erwerbspensenInZeitraum;
	}

	@Nonnull
	@Override
	public String getSearchResultId() {
		return getId();
	}

	@Nonnull
	@Override
	public String getSearchResultSummary() {
		return extractFullName();
	}

	@Nullable
	@Override
	public String getSearchResultAdditionalInformation() {
		return toString();
	}

	@Override
	public String getOwningGesuchId() {
		return null;   //leider nicht ohne serviceabfrage verfuegbar
	}
}
