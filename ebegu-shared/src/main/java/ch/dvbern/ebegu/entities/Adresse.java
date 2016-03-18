package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.converters.LocalDatePersistenceConverter;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Entitaet zum Speichern von Adressen in der Datenbank.
 */
@Audited
@Entity
public class Adresse extends AbstractEntity {

	private static final long serialVersionUID = -7687645920281069260L;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String strasse = "";

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String hausnummer = null;

	@Size(max = Constants.PLZ_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = true, length = Constants.PLZ_LENGTH)
	private String plz = "";

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String ort = "";

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String gemeinde = null;

	@NotNull
	@Column(nullable = false)
	@Convert(converter = LocalDatePersistenceConverter.class)
	private LocalDate gueltigAb;

	@NotNull
	@Column(nullable = false)
	@Convert(converter = LocalDatePersistenceConverter.class)
	private LocalDate gueltigBis;


	public Adresse() {
	}

	@Nonnull
	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(@Nonnull String strasse) {
		this.strasse = strasse;
	}

	@Nullable
	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(@Nullable String hausnummer) {
		this.hausnummer = hausnummer;
	}

	@Nonnull
	public String getPlz() {
		return plz;
	}

	public void setPlz(@Nonnull String plz) {
		this.plz = plz;
	}

	@Nonnull
	public String getOrt() {
		return ort;
	}

	@Nullable
	public String getGemeinde() {
		return gemeinde;
	}

	public void setGemeinde(@Nullable String gemeinde) {
		this.gemeinde = gemeinde;
	}

	public void setOrt(@Nonnull String ort) {
		this.ort = ort;
	}

	public LocalDate getGueltigAb() { return gueltigAb; }

	public void setGueltigAb(LocalDate gueltigAb) { this.gueltigAb = gueltigAb; }

	public LocalDate getGueltigBis() { return gueltigBis; }

	public void setGueltigBis(LocalDate gueltigBis) { this.gueltigBis = gueltigBis; }

}
