package ch.dvbern.ebegu.api.dtos;

import java.time.LocalDate;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;

/**
 * DTO fuer Gesuchsperiode
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuchsperiode extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -2495737706808699744L;

	@NotNull
	private GesuchsperiodeStatus status;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumFreischaltungTagesschule;


	public GesuchsperiodeStatus getStatus() {
		return status;
	}

	public void setStatus(GesuchsperiodeStatus status) {
		this.status = status;
	}

	public LocalDate getDatumFreischaltungTagesschule() {
		return datumFreischaltungTagesschule;
	}

	public void setDatumFreischaltungTagesschule(LocalDate datumFreischaltungTagesschule) {
		this.datumFreischaltungTagesschule = datumFreischaltungTagesschule;
	}
}
