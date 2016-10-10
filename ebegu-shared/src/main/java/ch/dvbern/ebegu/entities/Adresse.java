package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.enums.Land;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Entitaet zum Speichern von Adressen  in der Datenbank.
 */
@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Adresse extends AbstractDateRangedEntity {


	private static final long serialVersionUID = 4637260017314382780L;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String strasse;

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String hausnummer;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String zusatzzeile;

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String plz;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String ort;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Land land = Land.CH;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String gemeinde;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String organisation;

	public Adresse() {
	}

	public Adresse(@Nonnull Adresse toCopy) {
		super(toCopy);
		this.strasse = toCopy.strasse;
		this.hausnummer = toCopy.hausnummer;
		this.zusatzzeile = toCopy.zusatzzeile;
		this.plz = toCopy.plz;
		this.ort = toCopy.ort;
		this.land = toCopy.land;
		this.gemeinde = toCopy.gemeinde;
		this.organisation = toCopy.organisation;
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

	@Nullable
	public String getZusatzzeile() {
		return zusatzzeile;
	}

	public void setZusatzzeile(@Nullable String zusatzzeile) {
		this.zusatzzeile = zusatzzeile;
	}

	public Land getLand() {
		return land;
	}

	public void setLand(Land land) {
		this.land = land;
	}

	@Nullable
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(@Nullable String organisation) {
		this.organisation = organisation;
	}

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(Adresse otherAdr) {
		if (this == otherAdr) {
			return true;
		}
		if (otherAdr == null || getClass() != otherAdr.getClass()) {
			return false;
		}
		return Objects.equals(strasse, otherAdr.strasse) &&
			Objects.equals(hausnummer, otherAdr.hausnummer) &&
			Objects.equals(zusatzzeile, otherAdr.zusatzzeile) &&
			Objects.equals(plz, otherAdr.plz) &&
			Objects.equals(ort, otherAdr.ort) &&
			land == otherAdr.land &&
			Objects.equals(gemeinde, otherAdr.gemeinde) &&
			Objects.equals(organisation, otherAdr.organisation) &&
			Objects.equals(getGueltigkeit(), otherAdr.getGueltigkeit());

	}

}
