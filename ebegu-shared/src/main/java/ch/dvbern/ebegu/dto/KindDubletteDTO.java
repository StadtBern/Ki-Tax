package ch.dvbern.ebegu.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DTO das Resultat einer DublettenSuche bei den Kindern.
 * GesuchId und KindNummer werden fuer die Zusammenstellung des Links gebraucht, die FallNummer zur Anzeige auf dem GUI.
 */
public class KindDubletteDTO {

	private String gesuchId;
	private long fallNummer;
	private Integer kindNummerOriginal;
	private Integer kindNummerDublette;

	public KindDubletteDTO(String gesuchId, long fallNummer, Integer kindNummerOriginal, Integer kindNummerDublette) {
		this.gesuchId = gesuchId;
		this.fallNummer = fallNummer;
		this.kindNummerOriginal = kindNummerOriginal;
		this.kindNummerDublette = kindNummerDublette;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public String getGesuchId() {
		return gesuchId;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public Integer getKindNummerOriginal() {
		return kindNummerOriginal;
	}

	public void setKindNummerOriginal(Integer kindNummerOriginal) {
		this.kindNummerOriginal = kindNummerOriginal;
	}

	public Integer getKindNummerDublette() {
		return kindNummerDublette;
	}

	public void setKindNummerDublette(Integer kindNummerDublette) {
		this.kindNummerDublette = kindNummerDublette;
	}
}
