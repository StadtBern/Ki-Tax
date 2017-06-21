package ch.dvbern.ebegu.dto;

import java.time.LocalDateTime;
import java.util.Objects;

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
	private LocalDateTime timestampErstelltGesuch;

	public KindDubletteDTO(String gesuchId, long fallNummer, Integer kindNummerOriginal, Integer kindNummerDublette,
		LocalDateTime timestampErstelltGesuch) {
		this.gesuchId = gesuchId;
		this.fallNummer = fallNummer;
		this.kindNummerOriginal = kindNummerOriginal;
		this.kindNummerDublette = kindNummerDublette;
		this.timestampErstelltGesuch = timestampErstelltGesuch;
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

	public LocalDateTime getTimestampErstelltGesuch() {
		return timestampErstelltGesuch;
	}

	public void setTimestampErstelltGesuch(LocalDateTime timestampErstelltGesuch) {
		this.timestampErstelltGesuch = timestampErstelltGesuch;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		KindDubletteDTO otherDublette = (KindDubletteDTO) obj;
		return getFallNummer() == otherDublette.getFallNummer() &&
			Objects.equals(getKindNummerOriginal(), otherDublette.getKindNummerOriginal());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFallNummer(), getKindNummerOriginal());
	}
}
