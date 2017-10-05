package ch.dvbern.ebegu.entities;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.ModulName;
import org.hibernate.envers.Audited;

/**
 * Entity for the Module of the Tageschulangebote.
 */
@Audited
@Entity
public class Modul extends AbstractEntity {

	private static final long serialVersionUID = -8403411439182708718L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_modul_institution_stammdaten_id"), nullable = false)
	private InstitutionStammdaten instStammdaten;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private DayOfWeek wochentag;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private ModulName modulname;

	@Column(nullable = false)
	private LocalTime zeitVon;

	@Column(nullable = false)
	private LocalTime zeitBis;

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof Modul)) {
			return false;
		}
		final Modul otherModul = (Modul) other;
		return getModulname() == otherModul.getModulname() &&
			getWochentag() == otherModul.getWochentag() &&
			Objects.equals(getZeitVon(), otherModul.getZeitVon()) &&
			Objects.equals(getZeitBis(), otherModul.getZeitBis());
	}

	public DayOfWeek getWochentag() {
		return wochentag;
	}

	public void setWochentag(DayOfWeek wochentag) {
		this.wochentag = wochentag;
	}

	public ModulName getModulname() {
		return modulname;
	}

	public void setModulname(ModulName modulname) {
		this.modulname = modulname;
	}

	public LocalTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(LocalTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	public LocalTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(LocalTime zeitBis) {
		this.zeitBis = zeitBis;
	}

	public InstitutionStammdaten getInstStammdaten() {
		return instStammdaten;
	}

	public void setInstStammdaten(InstitutionStammdaten instStammdaten) {
		this.instStammdaten = instStammdaten;
	}
}
