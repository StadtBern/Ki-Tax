package ch.dvbern.ebegu.entities;

import java.time.LocalDate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von Familiensituation in der Datenbank.
 */
@Audited
@Entity
public class Familiensituation extends AbstractEntity {

	private static final long serialVersionUID = -6534582356181164632L;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private EnumFamilienstatus familienstatus;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	@Column(nullable = true)
	private EnumGesuchstellerKardinalitaet gesuchstellerKardinalitaet;

	@Column(nullable = true)
	private Boolean gemeinsameSteuererklaerung;

	@Column(nullable = true)
	private LocalDate aenderungPer;


	public Familiensituation() {
	}

	public Familiensituation(Familiensituation that) {
		if (that != null) {
			this.familienstatus = that.getFamilienstatus();
			this.gemeinsameSteuererklaerung = that.getGemeinsameSteuererklaerung();
			this.gesuchstellerKardinalitaet = that.getGesuchstellerKardinalitaet();
			this.aenderungPer = that.getAenderungPer();
		}
	}

	@Nonnull
	public EnumFamilienstatus getFamilienstatus() {
		return familienstatus;
	}

	public void setFamilienstatus(@Nonnull EnumFamilienstatus familienstatus) {
		this.familienstatus = familienstatus;
	}

	@Nullable
	public EnumGesuchstellerKardinalitaet getGesuchstellerKardinalitaet() {
		return gesuchstellerKardinalitaet;
	}

	public void setGesuchstellerKardinalitaet(@Nullable EnumGesuchstellerKardinalitaet gesuchstellerKardinalitaet) {
		this.gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
	}

	public Boolean getGemeinsameSteuererklaerung() {
		return gemeinsameSteuererklaerung;
	}

	public void setGemeinsameSteuererklaerung(Boolean gemeinsameSteuererklaerung) {
		this.gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
	}

	public LocalDate getAenderungPer() {
		return aenderungPer;
	}

	public void setAenderungPer(LocalDate aenderungPer) {
		this.aenderungPer = aenderungPer;
	}

	@Transient
	public boolean hasSecondGesuchsteller() {
		if (this.familienstatus != null) {
			switch (this.familienstatus) {
				case ALLEINERZIEHEND:
				case WENIGER_FUENF_JAHRE:
					return EnumGesuchstellerKardinalitaet.ZU_ZWEIT == this.getGesuchstellerKardinalitaet();
				case VERHEIRATET:
				case KONKUBINAT:
				case LAENGER_FUENF_JAHRE:
					return true;
			}
		}
		return false;
	}

	@Nonnull
	public Familiensituation copyForMutation(@Nonnull Familiensituation mutation) {
		super.copyForMutation(mutation);
		mutation.setAenderungPer(this.aenderungPer);
		return copyForMutationOrErneuerung(mutation);
	}

	@Nonnull
	public Familiensituation copyForErneuerung(@Nonnull Familiensituation folgeEntity) {
		super.copyForErneuerung(folgeEntity);
		return copyForMutationOrErneuerung(folgeEntity);
	}

	@Nonnull
	private Familiensituation copyForMutationOrErneuerung(@Nonnull Familiensituation mutation) {
		mutation.setFamilienstatus(this.getFamilienstatus());
		mutation.setGemeinsameSteuererklaerung(this.getGemeinsameSteuererklaerung());
		mutation.setGesuchstellerKardinalitaet(this.gesuchstellerKardinalitaet);
		return mutation;
	}

	@Override
	public boolean isSame(@Nullable AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof Familiensituation)) {
			return false;
		}
		final Familiensituation otherFamiliensituation = (Familiensituation) other;
		return Objects.equals(getAenderungPer(), otherFamiliensituation.getAenderungPer()) &&
			Objects.equals(getFamilienstatus(), otherFamiliensituation.getFamilienstatus()) &&
			Objects.equals(getGesuchstellerKardinalitaet(), otherFamiliensituation.getGesuchstellerKardinalitaet()) &&
			EbeguUtil.isSameOrNullBoolean(getGemeinsameSteuererklaerung(), otherFamiliensituation.getGemeinsameSteuererklaerung());

	}
}
