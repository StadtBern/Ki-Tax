package ch.dvbern.ebegu.dto;

/**
 * DTO das Resultat einer DublettenSuche bei den Kindern.
 * GesuchId und KindNummer werden fuer die Zusammenstellung des Links gebraucht, die FallNummer zur Anzeige auf dem GUI.
 */
public class KindDubletteDTO {

	private String gesuchId;
	private long fallNummer;
	private Integer kindNummer;

	public KindDubletteDTO(String gesuchId, long fallNummer, Integer kindNummer) {
		this.gesuchId = gesuchId;
		this.fallNummer = fallNummer;
		this.kindNummer = kindNummer;
	}

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
