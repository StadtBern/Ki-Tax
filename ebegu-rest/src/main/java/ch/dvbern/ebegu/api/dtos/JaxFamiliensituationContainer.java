package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * DTO fuer FamiliensituationenContainer
 */
@XmlRootElement(name = "familiensituationContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFamiliensituationContainer extends JaxAbstractDTO{

	private static final long serialVersionUID = 5217224327005193232L;

	private JaxFamiliensituation familiensituationJA;

	private JaxFamiliensituation familiensituationGS;

	private JaxFamiliensituation familiensituationErstgesuch;

	public JaxFamiliensituation getFamiliensituationJA() {
		return familiensituationJA;
	}

	public void setFamiliensituationJA(JaxFamiliensituation familiensituationJA) {
		this.familiensituationJA = familiensituationJA;
	}

	public JaxFamiliensituation getFamiliensituationGS() {
		return familiensituationGS;
	}

	public void setFamiliensituationGS(JaxFamiliensituation familiensituationGS) {
		this.familiensituationGS = familiensituationGS;
	}

	public JaxFamiliensituation getFamiliensituationErstgesuch() {
		return familiensituationErstgesuch;
	}

	public void setFamiliensituationErstgesuch(JaxFamiliensituation familiensituationErstgesuch) {
		this.familiensituationErstgesuch = familiensituationErstgesuch;
	}
}
