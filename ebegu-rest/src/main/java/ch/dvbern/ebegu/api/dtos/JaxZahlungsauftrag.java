package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
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
	private LocalDateTime datumFaellig;

	@NotNull
	private LocalDateTime datumGeneriert;

	@NotNull
	private Boolean ausgeloest = false;

	@NotNull
	private String beschrieb;

	@NotNull
	private BigDecimal betragTotalAuftrag;

	@Nonnull
	private List<JaxZahlung> zahlungen = new ArrayList<>();

	public LocalDateTime getDatumFaellig() {
		return datumFaellig;
	}

	public void setDatumFaellig(LocalDateTime datumFaellig) {
		this.datumFaellig = datumFaellig;
	}

	public LocalDateTime getDatumGeneriert() {
		return datumGeneriert;
	}

	public void setDatumGeneriert(LocalDateTime datumGeneriert) {
		this.datumGeneriert = datumGeneriert;
	}

	public Boolean getAusgeloest() {
		return ausgeloest;
	}

	public void setAusgeloest(Boolean ausgeloest) {
		this.ausgeloest = ausgeloest;
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
