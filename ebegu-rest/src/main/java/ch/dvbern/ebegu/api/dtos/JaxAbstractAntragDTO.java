package ch.dvbern.ebegu.api.dtos;

import java.time.LocalDate;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.Eingangsart;

/**
 * Abstract DTO fuer Antraege.
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAbstractAntragDTO extends JaxAbstractDTO {

	private static final long serialVersionUID = -2597026918664190397L;

	@NotNull
	private JaxFall fall;

	@NotNull
	private JaxGesuchsperiode gesuchsperiode;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate eingangsdatum = null;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate eingangsdatumSTV = null;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate freigabeDatum = null;

	@NotNull
	private AntragStatusDTO status;

	@NotNull
	private AntragTyp typ;

	@NotNull
	private Eingangsart eingangsart;


	public JaxFall getFall() {
		return fall;
	}

	public void setFall(JaxFall fall) {
		this.fall = fall;
	}

	public JaxGesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(JaxGesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	@Nullable
	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(@Nullable LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	@Nullable
	public LocalDate getEingangsdatumSTV() {
		return eingangsdatumSTV;
	}

	public void setEingangsdatumSTV(@Nullable LocalDate eingangsdatumSTV) {
		this.eingangsdatumSTV = eingangsdatumSTV;
	}

	@Nullable
	public LocalDate getFreigabeDatum() {
		return freigabeDatum;
	}

	public void setFreigabeDatum(@Nullable LocalDate freigabeDatum) {
		this.freigabeDatum = freigabeDatum;
	}

	public AntragStatusDTO getStatus() {
		return status;
	}

	public void setStatus(AntragStatusDTO status) {
		this.status = status;
	}

	public AntragTyp getTyp() {
		return typ;
	}

	public void setTyp(AntragTyp typ) {
		this.typ = typ;
	}

	public Eingangsart getEingangsart() {
		return eingangsart;
	}

	public void setEingangsart(Eingangsart eingangsart) {
		this.eingangsart = eingangsart;
	}
}
