package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.ZahlungStatus;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * DTO fuer Zahlungen
 */
@XmlRootElement(name = "zahlung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxZahlung extends JaxAbstractDTO {

	private static final long serialVersionUID = 1661454343875422672L;

	@NotNull
	private String institutionsName;

	@NotNull
	private ZahlungStatus status;

	@NotNull
	private BigDecimal betragTotalZahlung;

	public String getInstitutionsName() {
		return institutionsName;
	}

	public void setInstitutionsName(String institutionsName) {
		this.institutionsName = institutionsName;
	}

	public ZahlungStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungStatus status) {
		this.status = status;
	}

	public BigDecimal getBetragTotalZahlung() {
		return betragTotalZahlung;
	}

	public void setBetragTotalZahlung(BigDecimal betragTotalZahlung) {
		this.betragTotalZahlung = betragTotalZahlung;
	}
}
