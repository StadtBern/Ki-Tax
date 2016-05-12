package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.Gesuchsteller;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Erwerbspensum Container
 */
@XmlRootElement(name = "erwerbspensumcontainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxErwerbspensumContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = 4879926292956257345L;

	@Valid
	private JaxErwerbspensum erwerbspensumGS;

	@Valid
	private JaxErwerbspensum erwerbspensumJA;


	public JaxErwerbspensum getErwerbspensumJA() {
		return erwerbspensumJA;
	}

	public void setErwerbspensumJA(JaxErwerbspensum erwerbspensumJA) {
		this.erwerbspensumJA = erwerbspensumJA;
	}

	public JaxErwerbspensum getErwerbspensumGS() {
		return erwerbspensumGS;
	}

	public void setErwerbspensumGS(JaxErwerbspensum erwerbspensumGS) {
		this.erwerbspensumGS = erwerbspensumGS;
	}
}
