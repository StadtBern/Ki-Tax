package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;
import java.time.LocalDate;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Objects;

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
	private LocalDate stichtagFuerBasisJahrPlus1; //Ereignisdatum

	@Nullable
	@Column(nullable = true)
	private LocalDate stichtagFuerBasisJahrPlus2;

	@NotNull
	@Column(nullable = false)
	private Boolean ekvBasisJahrPlus1Annulliert = false;

	@NotNull
	@Column(nullable = false)
	private Boolean ekvBasisJahrPlus2Annulliert = false;


	public EinkommensverschlechterungInfo() {
	}

	public EinkommensverschlechterungInfo(EinkommensverschlechterungInfo other) {
		if (other != null) {
			this.einkommensverschlechterung = other.getEinkommensverschlechterung();
			this.ekvFuerBasisJahrPlus1 = other.getEkvFuerBasisJahrPlus1();
			this.ekvFuerBasisJahrPlus2 = other.getEkvFuerBasisJahrPlus2();
			this.gemeinsameSteuererklaerung_BjP1 = other.getGemeinsameSteuererklaerung_BjP1();
			this.gemeinsameSteuererklaerung_BjP2 = other.getGemeinsameSteuererklaerung_BjP2();
			this.grundFuerBasisJahrPlus1 = other.getGrundFuerBasisJahrPlus1();
			this.grundFuerBasisJahrPlus2 = other.getGrundFuerBasisJahrPlus2();
			this.stichtagFuerBasisJahrPlus1 = other.getStichtagFuerBasisJahrPlus1();
			this.stichtagFuerBasisJahrPlus2 = other.getStichtagFuerBasisJahrPlus2();
		}
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

	public Boolean getEkvBasisJahrPlus1Annulliert() {
		return ekvBasisJahrPlus1Annulliert;
	}

	public void setEkvBasisJahrPlus1Annulliert(Boolean ekvBasisJahrPlus1Annulliert) {
		this.ekvBasisJahrPlus1Annulliert = ekvBasisJahrPlus1Annulliert;
	}

	public Boolean getEkvBasisJahrPlus2Annulliert() {
		return ekvBasisJahrPlus2Annulliert;
	}

	public void setEkvBasisJahrPlus2Annulliert(Boolean ekvBasisJahrPlus2Annulliert) {
		this.ekvBasisJahrPlus2Annulliert = ekvBasisJahrPlus2Annulliert;
	}

	public EinkommensverschlechterungInfo copyForMutation(EinkommensverschlechterungInfo mutation) {
		super.copyForMutation(mutation);
		mutation.setEinkommensverschlechterung(this.getEinkommensverschlechterung());
		mutation.setEkvFuerBasisJahrPlus1(this.getEkvFuerBasisJahrPlus1());
		mutation.setEkvFuerBasisJahrPlus2(this.getEkvFuerBasisJahrPlus2());
		mutation.setGemeinsameSteuererklaerung_BjP1(this.getGemeinsameSteuererklaerung_BjP1());
		mutation.setGemeinsameSteuererklaerung_BjP2(this.getGemeinsameSteuererklaerung_BjP2());
		mutation.setGrundFuerBasisJahrPlus1(this.getGrundFuerBasisJahrPlus1());
		mutation.setGrundFuerBasisJahrPlus2(this.getGrundFuerBasisJahrPlus2());
		mutation.setStichtagFuerBasisJahrPlus1(this.getStichtagFuerBasisJahrPlus1());
		mutation.setStichtagFuerBasisJahrPlus2(this.getStichtagFuerBasisJahrPlus2());
		mutation.setEkvBasisJahrPlus1Annulliert(this.getEkvBasisJahrPlus1Annulliert());
		mutation.setEkvBasisJahrPlus2Annulliert(this.getEkvBasisJahrPlus2Annulliert());
		return mutation;
	}

	@Transient
	@Nullable
	public LocalDate getStichtagGueltigFuerBasisJahrPlus1() {
		if (stichtagFuerBasisJahrPlus1 != null) {
			return stichtagFuerBasisJahrPlus1.plusMonths(1);
		}
		return null;
	}

	@Transient
	@Nullable
	public LocalDate getStichtagGueltigFuerBasisJahrPlus2() {
		if (stichtagFuerBasisJahrPlus2 != null) {
			return stichtagFuerBasisJahrPlus2.plusMonths(1);
		}
		return null;
	}

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof EinkommensverschlechterungInfo)) {
			return false;
		}
		final EinkommensverschlechterungInfo otherEKVInfo = (EinkommensverschlechterungInfo) other;
		// if there is no EKV (einkommensverschlechterung==false) there is no need to compare the rest
		//noinspection SimplifiableIfStatement -> for clarity sake
		if (Objects.equals(getEinkommensverschlechterung(), otherEKVInfo.getEinkommensverschlechterung())
			&& Boolean.FALSE.equals(getEinkommensverschlechterung())) {
			return true;
		}
		return isSameBasisJahrPlus1(otherEKVInfo) && isSameBasisJahrPlus2(otherEKVInfo);
	}

	private boolean isSameBasisJahrPlus1(EinkommensverschlechterungInfo otherEKVInfo) {
		// if BasisJahrPlus1 is not set (ekvFuerBasisJahrPlus1==false) there is no need to compare the rest
		//noinspection SimplifiableIfStatement -> for clarity sake
		if (Objects.equals(getEkvFuerBasisJahrPlus1(), otherEKVInfo.getEkvFuerBasisJahrPlus1())
			&& Boolean.FALSE.equals(getEkvFuerBasisJahrPlus1())) {
			return true;
		}
		return EbeguUtil.isSameOrNullBoolean(getGemeinsameSteuererklaerung_BjP1(), otherEKVInfo.getGemeinsameSteuererklaerung_BjP1()) &&
			Objects.equals(getGrundFuerBasisJahrPlus1(), otherEKVInfo.getGrundFuerBasisJahrPlus1()) &&
			Objects.equals(getStichtagFuerBasisJahrPlus1(), otherEKVInfo.getStichtagFuerBasisJahrPlus1());
	}

	private boolean isSameBasisJahrPlus2(EinkommensverschlechterungInfo otherEKVInfo) {
		// if BasisJahrPlus2 is not set (ekvFuerBasisJahrPlus2==false) there is no need to compare the rest
		//noinspection SimplifiableIfStatement -> for clarity sake
		if (Objects.equals(getEkvFuerBasisJahrPlus2(), otherEKVInfo.getEkvFuerBasisJahrPlus2())
			&& Boolean.FALSE.equals(getEkvFuerBasisJahrPlus2())) {
			return true;
		}
		return EbeguUtil.isSameOrNullBoolean(getGemeinsameSteuererklaerung_BjP2(), otherEKVInfo.getGemeinsameSteuererklaerung_BjP2()) &&
			Objects.equals(getGrundFuerBasisJahrPlus2(), otherEKVInfo.getGrundFuerBasisJahrPlus2()) &&
			Objects.equals(getStichtagFuerBasisJahrPlus2(), otherEKVInfo.getStichtagFuerBasisJahrPlus2());
	}
}
