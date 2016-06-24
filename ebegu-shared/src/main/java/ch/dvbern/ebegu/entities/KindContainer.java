package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Container-Entity fuer die Kinder: Diese muss für jeden Benutzertyp (GS, JA) einzeln gefuehrt werden,
 * damit die Veraenderungen / Korrekturen angezeigt werden koennen.
 */
@Audited
@Entity
@Table(indexes = {
	@Index(columnList = "kindNummer,gesuch_id", name = "IX_kindcontainer_gesuch_kind_nummer")
})
public class KindContainer extends AbstractEntity {

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
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "kind")
	private Set<Betreuung> betreuungen = new HashSet<>();


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

}
