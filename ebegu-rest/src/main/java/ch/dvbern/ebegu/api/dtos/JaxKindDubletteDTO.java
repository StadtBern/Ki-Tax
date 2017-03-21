package ch.dvbern.ebegu.api.dtos;

import java.io.Serializable;

/**
 * DTO fuer Kind Container
 */
public class JaxKindDubletteDTO  implements Serializable {

	private static final long serialVersionUID = -1297026881674137397L;

	private String gesuchId;
	private long fallNummer;
	private Integer kindNummer;


	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public Integer getKindNummer() {
		return kindNummer;
	}

	public void setKindNummer(Integer kindNummer) {
		this.kindNummer = kindNummer;
	}
}
