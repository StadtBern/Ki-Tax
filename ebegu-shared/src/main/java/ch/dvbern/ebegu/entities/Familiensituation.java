package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
		this.familienstatus = that.familienstatus;
		this.gemeinsameSteuererklaerung = that.gemeinsameSteuererklaerung;
		this.gesuchstellerKardinalitaet = that.gesuchstellerKardinalitaet;
		this.aenderungPer = that.aenderungPer;
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
					return EnumGesuchstellerKardinalitaet.ZU_ZWEIT.equals(this.getGesuchstellerKardinalitaet());
				case VERHEIRATET:
				case KONKUBINAT:
				case LAENGER_FUENF_JAHRE:
					return true;
			}
		}
		return false;
	}

	public Familiensituation copyForMutation(Familiensituation mutation) {
		super.copyForMutation(mutation);
		mutation.setFamilienstatus(this.getFamilienstatus());
		mutation.setGemeinsameSteuererklaerung(this.getGemeinsameSteuererklaerung());
		mutation.setGesuchstellerKardinalitaet(this.gesuchstellerKardinalitaet);
		mutation.setAenderungPer(this.aenderungPer);
		return mutation;
	}
}
