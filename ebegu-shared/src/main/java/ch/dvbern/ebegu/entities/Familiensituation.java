package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchKardinalitaet;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entitaet zum Speichern von Familiensituation in der Datenbank.
 */
@Audited
@Entity
public class Familiensituation extends AbstractEntity {

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private EnumFamilienstatus familienstatus;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	@Column(nullable = true)
	private EnumGesuchKardinalitaet gesuchKardinalitaet;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

	@ManyToOne(optional = false)
	private Gesuch gesuch;


	public Familiensituation() {
	}

	@Nonnull
	public EnumFamilienstatus getFamilienstatus() {
		return familienstatus;
	}

	public void setFamilienstatus(@Nonnull EnumFamilienstatus familienstatus) {
		this.familienstatus = familienstatus;
	}

	@Nullable
	public EnumGesuchKardinalitaet getGesuchKardinalitaet() {
		return gesuchKardinalitaet;
	}

	public void setGesuchKardinalitaet(@Nullable EnumGesuchKardinalitaet gesuchKardinalitaet) {
		this.gesuchKardinalitaet = gesuchKardinalitaet;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}
}
