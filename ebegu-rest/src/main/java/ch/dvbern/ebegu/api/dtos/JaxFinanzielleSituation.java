package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;

/**
 * DTO fuer Finanzielle Situation
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFinanzielleSituation extends JaxAbstractFinanzielleSituation {

	private static final long serialVersionUID = -403919135454757656L;

	@Nullable
	private BigDecimal nettolohn;

	public BigDecimal getNettolohn() {
		return nettolohn;
	}

	public void setNettolohn(final BigDecimal nettolohn) {
		this.nettolohn = nettolohn;
	}

}
