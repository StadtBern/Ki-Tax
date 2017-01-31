package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.dto.suchfilter.lucene.EBEGUGermanAnalyzer;
import ch.dvbern.ebegu.dto.suchfilter.lucene.Searchable;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.TreeSet;

/**
 * Container-Entity fuer die Kinder: Diese muss für jeden Benutzertyp (GS, JA) einzeln gefuehrt werden,
 * damit die Veraenderungen / Korrekturen angezeigt werden koennen.
 */
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"kindNummer", "gesuch_id"}, name = "UK_kindcontainer_gesuch_kind_nummer")
)
@Indexed
@Analyzer(impl = EBEGUGermanAnalyzer.class)
public class KindContainer extends AbstractEntity implements Comparable<KindContainer>, Searchable {

	private static final long serialVersionUID = -6784985260190035840L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_kind_container_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_kind_container_kindgs_id"), nullable = true)
	private Kind kindGS;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_kind_container_kindja_id"), nullable = true)
	@IndexedEmbedded
	private Kind kindJA;

	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer kindNummer = 1;

	/**
	 * nextNumberBetreuung ist die Nummer, die die naechste Betreuung bekommen wird. Aus diesem Grund ist es by default 1
	 * Dieses Feld darf nicht mit der Anzahl der Betreuungen verwechselt werden, da sie sehr unterschiedlich sein koennen falls mehrere Betreuungen geloescht wurden
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer nextNumberBetreuung = 1;

	@Nullable
	@Valid
	@SortNatural
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "kind")
	private Set<Betreuung> betreuungen = new TreeSet<>();


	public KindContainer() {
	}


	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public Kind getKindGS() {
		return kindGS;
	}

	public void setKindGS(Kind kindGS) {
		this.kindGS = kindGS;
	}

	public Kind getKindJA() {
		return kindJA;
	}

	public void setKindJA(Kind kindJA) {
		this.kindJA = kindJA;
	}

	public Integer getKindNummer() {
		return kindNummer;
	}

	public void setKindNummer(Integer kindNummer) {
		this.kindNummer = kindNummer;
	}

	public Integer getNextNumberBetreuung() {
		return nextNumberBetreuung;
	}

	public void setNextNumberBetreuung(Integer nextNumberBetreuung) {
		this.nextNumberBetreuung = nextNumberBetreuung;
	}

	public Set<Betreuung> getBetreuungen() {
		return betreuungen;
	}

	public void setBetreuungen(Set<Betreuung> betreuungen) {
		this.betreuungen = betreuungen;
	}

	@Override
	public int compareTo(KindContainer other) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getKindNummer(), other.getKindNummer());
		compareToBuilder.append(this.getId(), other.getId());
		return compareToBuilder.toComparison();
	}

	public KindContainer copyForMutation(KindContainer mutation, @Nonnull Gesuch gesuchMutation) {
		super.copyForMutation(mutation);
		mutation.setGesuch(gesuchMutation);
		mutation.setKindGS(null);
		mutation.setKindJA(this.getKindJA().copyForMutation(new Kind()));
		mutation.setKindNummer(this.getKindNummer());
		mutation.setNextNumberBetreuung(this.getNextNumberBetreuung());
		if (this.getBetreuungen() != null) {
			mutation.setBetreuungen(new TreeSet<>());
			for (Betreuung betreuung : this.getBetreuungen()) {
				mutation.getBetreuungen().add(betreuung.copyForMutation(new Betreuung(), mutation));
			}
		}
		return mutation;
	}

	@Nonnull
	@Override
	public String getSearchResultId() {
		return this.getId();
	}

	@Nonnull
	@Override
	public String getSearchResultSummary() {
		if(getKindJA()!=null){
			return getKindJA().getFullName();
		}
		return "-";
	}

	@Nullable
	@Override
	public String getSearchResultAdditionalInformation() {
		return this.toString();
	}

	@Override
	public String getOwningGesuchId() {
		return getGesuch().getId();
	}
}
