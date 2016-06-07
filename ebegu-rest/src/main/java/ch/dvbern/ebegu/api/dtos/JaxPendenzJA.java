package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO fuer Pendenzen
 */
@XmlRootElement(name = "pendenz")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxPendenzJA {

	@NotNull
	private Long fallNummer;

	@NotNull
	private String familienName;

	@NotNull
	private AntragTyp antragTyp;

	@NotNull
	private JaxGesuchsperiode gesuchsperiode;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate eingangsdatum = null;

//	@NotNull
//	private PendenzStatus pendenzStatus;

//	private User bearbeiter;

	@NotNull
	private List<BetreuungsangebotTyp> angebote;

	@NotNull
	private List<String> institutionen;
}
