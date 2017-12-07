package ch.dvbern.ebegu.api.dtos;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "externalRechnungsAdresse")
public class JaxExternalRechnungsAdresse implements Serializable {

	private static final long serialVersionUID = -1691356089500336392L;

	@Nonnull
	private String vorname;

	@Nonnull
	private String nachname;

	@Nonnull
	private String strasse;

	@Nullable
	private String hausnummer;

	@Nullable
	private String zusatzzeile;

	@Nonnull
	private String plz;

	@Nonnull
	private String ort;

	@Nonnull
	private String land;


	public JaxExternalRechnungsAdresse(@Nonnull String vorname, @Nonnull String nachname, @Nonnull String strasse,
		@Nullable String hausnummer, @Nullable String zusatzzeile, @Nonnull String plz, @Nonnull String ort, @Nonnull String land) {
		this.vorname = vorname;
		this.nachname = nachname;
		this.strasse = strasse;
		this.hausnummer = hausnummer;
		this.zusatzzeile = zusatzzeile;
		this.plz = plz;
		this.ort = ort;
		this.land = land;
	}

	@Nonnull
	public String getVorname() {
		return vorname;
	}

	public void setVorname(@Nonnull String vorname) {
		this.vorname = vorname;
	}

	@Nonnull
	public String getNachname() {
		return nachname;
	}

	public void setNachname(@Nonnull String nachname) {
		this.nachname = nachname;
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

	@Nullable
	public String getZusatzzeile() {
		return zusatzzeile;
	}

	public void setZusatzzeile(@Nullable String zusatzzeile) {
		this.zusatzzeile = zusatzzeile;
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

	public void setOrt(@Nonnull String ort) {
		this.ort = ort;
	}

	@Nonnull
	public String getLand() {
		return land;
	}

	public void setLand(@Nonnull String land) {
		this.land = land;
	}

}
