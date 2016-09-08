package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entity für die Erfassung von Einkommensverschlechterungen für das Gesuch
 * Speichern der Entscheidung ob eine Einkommensverschlechterung geltend gemacht werden möchte sowie die Auswahl der
 * Jahreshälfte, Monat des Ereignisses sowie deren Grund
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@Entity
public class EinkommensverschlechterungInfo extends AbstractEntity {


	private static final long serialVersionUID = 3952202946246235539L;

	@NotNull
	@Column(nullable = false)
	private Boolean einkommensverschlechterung = Boolean.FALSE;

	@NotNull
	@Column(nullable = false)
	private Boolean ekvFuerBasisJahrPlus1;

	@NotNull
	@Column(nullable = false)
	private Boolean ekvFuerBasisJahrPlus2;

	@Nullable
	@Column(nullable = true)
	private Boolean gemeinsameSteuererklaerung_BjP1;

	@Nullable
	@Column(nullable = true)
	private Boolean gemeinsameSteuererklaerung_BjP2;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String grundFuerBasisJahrPlus1;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String grundFuerBasisJahrPlus2;

	@Nullable
	@Column(nullable = true)
	private LocalDate stichtagFuerBasisJahrPlus1;

	@Nullable
	@Column(nullable = true)
	private LocalDate stichtagFuerBasisJahrPlus2;

	@NotNull
	@Valid
	@OneToOne(optional = false, mappedBy = "einkommensverschlechterungInfo")
	private Gesuch gesuch;

	public EinkommensverschlechterungInfo(){
	}

	/**
	 * Copy Constructor. ACHTUNG kopiert nur daten die in dieser Klasse definiert sind
	 */
	public EinkommensverschlechterungInfo(EinkommensverschlechterungInfo that) {
		this.einkommensverschlechterung = that.einkommensverschlechterung;
		this.ekvFuerBasisJahrPlus1 = that.ekvFuerBasisJahrPlus1;
		this.ekvFuerBasisJahrPlus2 = that.ekvFuerBasisJahrPlus2;
		this.gemeinsameSteuererklaerung_BjP1 = that.gemeinsameSteuererklaerung_BjP1;
		this.gemeinsameSteuererklaerung_BjP2 = that.gemeinsameSteuererklaerung_BjP2;
		this.grundFuerBasisJahrPlus1 = that.grundFuerBasisJahrPlus1;
		this.grundFuerBasisJahrPlus2 = that.grundFuerBasisJahrPlus2;
		this.stichtagFuerBasisJahrPlus1 = that.stichtagFuerBasisJahrPlus1;
		this.stichtagFuerBasisJahrPlus2 = that.stichtagFuerBasisJahrPlus2;
	}


	public Boolean getEinkommensverschlechterung() {
		return einkommensverschlechterung;
	}


	public void setEinkommensverschlechterung(final Boolean einkommensverschlechterung) {
		this.einkommensverschlechterung = einkommensverschlechterung;
	}

	public Boolean getEkvFuerBasisJahrPlus1() {
		return ekvFuerBasisJahrPlus1;
	}

	public void setEkvFuerBasisJahrPlus1(final Boolean ekvFuerBasisJahrPlus1) {
		this.ekvFuerBasisJahrPlus1 = ekvFuerBasisJahrPlus1;
	}

	public Boolean getEkvFuerBasisJahrPlus2() {
		return ekvFuerBasisJahrPlus2;
	}

	public void setEkvFuerBasisJahrPlus2(final Boolean ekvFuerBasisJahrPlus2) {
		this.ekvFuerBasisJahrPlus2 = ekvFuerBasisJahrPlus2;
	}

	@Nullable
	public String getGrundFuerBasisJahrPlus1() {
		return grundFuerBasisJahrPlus1;
	}

	public void setGrundFuerBasisJahrPlus1(@Nullable final String grundFuerBasisJahrPlus1) {
		this.grundFuerBasisJahrPlus1 = grundFuerBasisJahrPlus1;
	}

	@Nullable
	public String getGrundFuerBasisJahrPlus2() {
		return grundFuerBasisJahrPlus2;
	}

	public void setGrundFuerBasisJahrPlus2(@Nullable final String grundFuerBasisJahrPlus2) {
		this.grundFuerBasisJahrPlus2 = grundFuerBasisJahrPlus2;
	}

	@Nullable
	public LocalDate getStichtagFuerBasisJahrPlus1() {
		return stichtagFuerBasisJahrPlus1;
	}

	public void setStichtagFuerBasisJahrPlus1(@Nullable final LocalDate stichtagFuerBasisJahrPlus1) {
		this.stichtagFuerBasisJahrPlus1 = stichtagFuerBasisJahrPlus1;
	}

	@Nullable
	public LocalDate getStichtagFuerBasisJahrPlus2() {
		return stichtagFuerBasisJahrPlus2;
	}

	public void setStichtagFuerBasisJahrPlus2(@Nullable final LocalDate stichtagFuerBasisJahrPlus2) {
		this.stichtagFuerBasisJahrPlus2 = stichtagFuerBasisJahrPlus2;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;

		if (gesuch != null &&
			(gesuch.getEinkommensverschlechterungInfo() == null || !gesuch.getEinkommensverschlechterungInfo().equals(this))) {
			gesuch.setEinkommensverschlechterungInfo(this);
		}
	}

	@Nullable
	public Boolean getGemeinsameSteuererklaerung_BjP1() {
		return gemeinsameSteuererklaerung_BjP1;
	}

	public void setGemeinsameSteuererklaerung_BjP1(@Nullable Boolean gemeinsameSteuererklaerung_BjP1) {
		this.gemeinsameSteuererklaerung_BjP1 = gemeinsameSteuererklaerung_BjP1;
	}

	@Nullable
	public Boolean getGemeinsameSteuererklaerung_BjP2() {
		return gemeinsameSteuererklaerung_BjP2;
	}

	public void setGemeinsameSteuererklaerung_BjP2(@Nullable Boolean gemeinsameSteuererklaerung_BjP2) {
		this.gemeinsameSteuererklaerung_BjP2 = gemeinsameSteuererklaerung_BjP2;
	}
}
