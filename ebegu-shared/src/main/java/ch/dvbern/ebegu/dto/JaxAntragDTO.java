package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;
import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.*;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO fuer Pendenzen
 */
@XmlRootElement(name = "pendenz")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAntragDTO implements Serializable {

	private static final long serialVersionUID = -1277026654764135397L;

	//probably unused
	public JaxAntragDTO(String antragId, LocalDate gesuchsperiodeGueltigAb, LocalDate gesuchsperiodeGueltigBis,
						@Nullable LocalDate eingangsdatum, AntragTyp antragTyp, int laufnummer, Eingangsart eingangsart) {
		this.antragId = antragId;
		this.gesuchsperiodeGueltigAb = gesuchsperiodeGueltigAb;
		this.gesuchsperiodeGueltigBis = gesuchsperiodeGueltigBis;
		this.eingangsdatum = eingangsdatum;
		this.antragTyp = antragTyp;
		this.laufnummer = laufnummer;
		this.eingangsart = eingangsart;
	}

	//constructor fuer query
	public JaxAntragDTO(String antragId, LocalDate gesuchsperiodeGueltigAb, LocalDate gesuchsperiodeGueltigBis,
						@Nullable LocalDate eingangsdatum, AntragTyp antragTyp, AntragStatus antragStatus, int laufnummer,
						Eingangsart eingangsart, String besitzerUsername) {
		this.antragId = antragId;
		this.gesuchsperiodeGueltigAb = gesuchsperiodeGueltigAb;
		this.gesuchsperiodeGueltigBis = gesuchsperiodeGueltigBis;
		this.eingangsdatum = eingangsdatum;
		this.antragTyp = antragTyp;
		this.verfuegt = antragStatus.isAnyStatusOfVerfuegt();
		this.beschwerdeHaengig = antragStatus.equals(AntragStatus.BESCHWERDE_HAENGIG);
		this.laufnummer = laufnummer;
		this.eingangsart = eingangsart;
		this.besitzerUsername = besitzerUsername;
	}

	@NotNull
	private String antragId = null;

	@NotNull
	private Eingangsart eingangsart;

	@Nullable
	private String besitzerUsername;

	@NotNull
	private long fallNummer;

	@NotNull
	private String familienName;

	@NotNull
	private AntragTyp antragTyp;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gesuchsperiodeGueltigAb = null;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gesuchsperiodeGueltigBis = null;

	@NotNull
	private String verantwortlicher;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate eingangsdatum = null;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime aenderungsdatum = null;

	@NotNull
	private Set<BetreuungsangebotTyp> angebote;

	@NotNull
	private Set<String> institutionen;

	@NotNull
	private AntragStatusDTO status;

	@NotNull
	private int laufnummer;

	private boolean verfuegt;

	private boolean beschwerdeHaengig;

	public JaxAntragDTO() {

	}

	public String getAntragId() {
		return antragId;
	}

	public void setAntragId(String antragId) {
		this.antragId = antragId;
	}

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public String getFamilienName() {
		return familienName;
	}

	public void setFamilienName(String familienName) {
		this.familienName = familienName;
	}

	public AntragTyp getAntragTyp() {
		return antragTyp;
	}

	public void setAntragTyp(AntragTyp antragTyp) {
		this.antragTyp = antragTyp;
	}

	public LocalDate getGesuchsperiodeGueltigAb() {
		return gesuchsperiodeGueltigAb;
	}

	public void setGesuchsperiodeGueltigAb(LocalDate gesuchsperiodeGueltigAb) {
		this.gesuchsperiodeGueltigAb = gesuchsperiodeGueltigAb;
	}

	public LocalDate getGesuchsperiodeGueltigBis() {
		return gesuchsperiodeGueltigBis;
	}

	public void setGesuchsperiodeGueltigBis(LocalDate gesuchsperiodeGueltigBis) {
		this.gesuchsperiodeGueltigBis = gesuchsperiodeGueltigBis;
	}

	public String getVerantwortlicher() {
		return verantwortlicher;
	}

	public void setVerantwortlicher(String verantwortlicher) {
		this.verantwortlicher = verantwortlicher;
	}

	@Nullable
	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(@Nullable LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	@Nullable
	public LocalDateTime getAenderungsdatum() {
		return aenderungsdatum;
	}

	public void setAenderungsdatum(@Nullable LocalDateTime aenderungsdatum) {
		this.aenderungsdatum = aenderungsdatum;
	}

	public Set<BetreuungsangebotTyp> getAngebote() {
		return angebote;
	}

	public void setAngebote(Set<BetreuungsangebotTyp> angebote) {
		this.angebote = angebote;
	}

	public Set<String> getInstitutionen() {
		return institutionen;
	}

	public void setInstitutionen(Set<String> institutionen) {
		this.institutionen = institutionen;
	}

	public AntragStatusDTO getStatus() {
		return status;
	}

	public void setStatus(AntragStatusDTO status) {
		this.status = status;
	}

	public boolean isVerfuegt() {
		return verfuegt;
	}

	public void setVerfuegt(boolean verfuegt) {
		this.verfuegt = verfuegt;
	}

	public boolean isBeschwerdeHaengig() {
		return beschwerdeHaengig;
	}

	public void setBeschwerdeHaengig(boolean beschwerdeHaengig) {
		this.beschwerdeHaengig = beschwerdeHaengig;
	}

	@Nullable
	public int getLaufnummer() {
		return laufnummer;
	}

	public void setLaufnummer(@Nullable int laufnummer) {
		this.laufnummer = laufnummer;
	}

	public Eingangsart getEingangsart() {
		return eingangsart;
	}

	public void setEingangsart(Eingangsart eingangsart) {
		this.eingangsart = eingangsart;
	}

	@Nullable
	public String getBesitzerUsername() {
		return besitzerUsername;
	}

	public void setBesitzerUsername(@Nullable String besitzerUsername) {
		this.besitzerUsername = besitzerUsername;
	}
}
