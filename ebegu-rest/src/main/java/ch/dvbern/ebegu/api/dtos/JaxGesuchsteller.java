package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * DTO fuer Stammdaten der Gesuchsteller
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuchsteller extends JaxAbstractPersonDTO {

	private static final long serialVersionUID = -1297026901664130397L;

	@Pattern(regexp = Constants.REGEX_EMAIL, message = "{validator.constraints.Email.message}")
	@Size(min = 5, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	private String mail;

	@Pattern(regexp = Constants.REGEX_TELEFON_MOBILE, message = "{error_invalid_mobilenummer}")
	private String mobile;

	@Pattern(regexp = Constants.REGEX_TELEFON, message = "{error_invalid_mobilenummer}")
	private String telefon;

	private String telefonAusland;

	private String ewkPersonId;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate ewkAbfrageDatum;


	private boolean diplomatenstatus;


	public String getMail() {
		return mail;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(final String telefon) {
		this.telefon = telefon;
	}

	public String getTelefonAusland() {
		return telefonAusland;
	}

	public void setTelefonAusland(final String telefonAusland) {
		this.telefonAusland = telefonAusland;
	}

	public String getEwkPersonId() {
		return ewkPersonId;
	}

	public void setEwkPersonId(final String ewkPersonId) {
		this.ewkPersonId = ewkPersonId;
	}

	public LocalDate getEwkAbfrageDatum() {
		return ewkAbfrageDatum;
	}

	public void setEwkAbfrageDatum(LocalDate ewkAbfrageDatum) {
		this.ewkAbfrageDatum = ewkAbfrageDatum;
	}

	public boolean isDiplomatenstatus() {
		return diplomatenstatus;
	}

	public void setDiplomatenstatus(final boolean diplomatenstatus) {
		this.diplomatenstatus = diplomatenstatus;
	}

}
