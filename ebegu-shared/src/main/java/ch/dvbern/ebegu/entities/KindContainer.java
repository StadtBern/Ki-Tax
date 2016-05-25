package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.validation.Valid;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Container-Entity für die Kinder: Diese muss für jeden Benutzertyp (GS, JA) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
public class KindContainer extends AbstractEntity {

	private static final long serialVersionUID = -6784985260190035840L;

	@NotNull
	@ManyToOne(optional = false)
	private Gesuch gesuch;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private Kind kindGS;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private Kind kindJA;

	@Nullable
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

	public Set<Betreuung> getBetreuungen() {
		return betreuungen;
	}

	public void setBetreuungen(Set<Betreuung> betreuungen) {
		this.betreuungen = betreuungen;
	}

}
