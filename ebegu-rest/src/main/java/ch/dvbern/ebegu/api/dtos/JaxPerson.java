package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * DTO fuer Stammdaten der Person (kennt adresse)
 */
@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxPerson extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297026901664130397L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	private String vorname;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	private String nachname;

	@NotNull
	private Geschlecht geschlecht;

	@Pattern(regexp = Constants.REGEX_EMAIL, message = "{validator.constraints.Email.message}")
	@Size(min = 5, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	private String mail;

	@NotNull()
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate geburtsdatum;

	@Pattern(regexp = Constants.REGEX_TELEFON_MOBILE, message = "{error_invalid_mobilenummer}")
	private String mobile;


	@Pattern(regexp = Constants.REGEX_TELEFON, message = "{error_invalid_mobilenummer}")
	private String telefon;


	@Pattern(regexp = Constants.REGEX_TELEFON, message = "{error_invalid_mobilenummer}")
	private String telefonAusland;

	private String zpvNumber; //todo team, es ist noch offen was das genau fuer ein identifier ist

	//Adressen
	private JaxAdresse wohnAdresse;

	private JaxAdresse alternativeAdresse;

	private JaxAdresse umzugAdresse;


	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getTelefonAusland() {
		return telefonAusland;
	}

	public void setTelefonAusland(String telefonAusland) {
		this.telefonAusland = telefonAusland;
	}

	public String getZpvNumber() {
		return zpvNumber;
	}

	public void setZpvNumber(String zpvNumber) {
		this.zpvNumber = zpvNumber;
	}

	public JaxAdresse getWohnAdresse() {
		return wohnAdresse;
	}

	public void setWohnAdresse(JaxAdresse wohnAdresse) {
		this.wohnAdresse = wohnAdresse;
	}

	public JaxAdresse getAlternativeAdresse() {
		return alternativeAdresse;
	}

	public void setAlternativeAdresse(JaxAdresse alternativeAdresse) {
		this.alternativeAdresse = alternativeAdresse;
	}

	public JaxAdresse getUmzugAdresse() {
		return umzugAdresse;
	}

	public void setUmzugAdresse(JaxAdresse umzugAdresse) {
		this.umzugAdresse = umzugAdresse;
	}
}
