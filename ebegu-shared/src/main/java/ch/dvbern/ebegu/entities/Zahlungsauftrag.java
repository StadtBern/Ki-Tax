package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.ZahlungauftragStatus;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitaet zum Speichern von einem Zahlungsauftrag in der Datenbank.
 */
@Audited
@Entity
public class Zahlungsauftrag extends AbstractDateRangedEntity {


	private static final long serialVersionUID = 5758088668232796741L;


	@NotNull
	@Column(nullable = false)
	private LocalDate datumFaellig; // Nur benoetigt fuer die Information an Postfinance -> ISO File

	@NotNull
	@Column(nullable = false)
	private LocalDateTime datumGeneriert; // Zeitpunkt, an welchem der Auftrag erstellt wurde, d.h. bis hierhin wurden Mutationen beruecksichtigt

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ZahlungauftragStatus status = ZahlungauftragStatus.ENTWURF;

	@NotNull
	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String beschrieb;

	@NotNull
	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Column(nullable = false, length = Constants.DB_TEXTAREA_LENGTH)
	private String filecontent; // TODO (team) im EntityListener sicherstellen, dass nach auslösung nicht mehr verändert

	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "zahlungsauftrag")
	private List<Zahlung> zahlungen = new ArrayList<>();


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

	public String getFilecontent() {
		return filecontent;
	}

	public void setFilecontent(String filecontent) {
		this.filecontent = filecontent;
	}

	@Nonnull
	public List<Zahlung> getZahlungen() {
		return zahlungen;
	}

	public void setZahlungen(@Nonnull List<Zahlung> zahlungen) {
		this.zahlungen = zahlungen;
	}
}
