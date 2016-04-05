package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.EnumBeantragen;
import ch.dvbern.ebegu.enums.EnumFamiliensituation;
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
	private EnumFamiliensituation familiensituation;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	@Column(nullable = true)
	private EnumBeantragen beantragen;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

	@ManyToOne(optional = false)
	private Gesuch gesuch;


	public Familiensituation() {
	}

	@Nonnull
	public EnumFamiliensituation getFamiliensituation() {
		return familiensituation;
	}

	public void setFamiliensituation(@Nonnull EnumFamiliensituation familiensituation) {
		this.familiensituation = familiensituation;
	}

	@Nullable
	public EnumBeantragen getBeantragen() {
		return beantragen;
	}

	public void setBeantragen(@Nullable EnumBeantragen beantragen) {
		this.beantragen = beantragen;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}
}
