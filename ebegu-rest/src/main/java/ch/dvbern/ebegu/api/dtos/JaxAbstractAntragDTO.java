package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

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
}
