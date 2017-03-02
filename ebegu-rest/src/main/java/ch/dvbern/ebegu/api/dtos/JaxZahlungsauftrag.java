package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;
import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.ZahlungauftragStatus;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO fuer Zahlungsauftrag
 */
@XmlRootElement(name = "zahlungsauftrag")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxZahlungsauftrag extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = 5908117979039694339L;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumFaellig;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime datumGeneriert;

	@NotNull
	private ZahlungauftragStatus status;

	@NotNull
	private String beschrieb;

	@NotNull
	private BigDecimal betragTotalAuftrag;

	@Nonnull
	private List<JaxZahlung> zahlungen = new ArrayList<>();

	public LocalDate getDatumFaellig() {
		return datumFaellig;
	}

	public void setDatumFaellig(LocalDate datumFaellig) {
		this.datumFaellig = datumFaellig;
	}

	public LocalDateTime getDatumGeneriert() {
		return datumGeneriert;
	}

	public void setDatumGeneriert(LocalDateTime datumGeneriert) {
		this.datumGeneriert = datumGeneriert;
	}

	public ZahlungauftragStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungauftragStatus status) {
		this.status = status;
	}


	public String getBeschrieb() {
		return beschrieb;
	}

	public void setBeschrieb(String beschrieb) {
		this.beschrieb = beschrieb;
	}

	@Nonnull
	public List<JaxZahlung> getZahlungen() {
		return zahlungen;
	}

	public void setZahlungen(@Nonnull List<JaxZahlung> zahlungen) {
		this.zahlungen = zahlungen;
	}

	public BigDecimal getBetragTotalAuftrag() {
		return betragTotalAuftrag;
	}

	public void setBetragTotalAuftrag(BigDecimal betragTotalAuftrag) {
		this.betragTotalAuftrag = betragTotalAuftrag;
	}
}
