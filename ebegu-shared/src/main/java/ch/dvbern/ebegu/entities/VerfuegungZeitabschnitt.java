package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.rules.RuleKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.util.MathUtil;
import com.google.common.base.Joiner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * Dieses Objekt repraesentiert einen Zeitabschnitt wahrend eines Betreeungsgutscheinantrags waehrend dem die Faktoren
 * die fuer die Berechnung des Gutscheins der Betreuung relevant sind konstant geblieben sind.
 */
@Entity
@Audited
public class VerfuegungZeitabschnitt extends AbstractDateRangedEntity implements Comparable<VerfuegungZeitabschnitt> {

	private static final long serialVersionUID = 7250339356897563374L;


	// Zwischenresulate aus DATA-Rules ("Abschnitt")

	@Transient
	private int erwerbspensumGS1;

	@Transient
	private int erwerbspensumGS2;

	@Transient
	private int fachstellenpensum;

	@Transient
	private boolean zuSpaetEingereicht;

	@Transient
	private boolean wohnsitzNichtInGemeindeGS1;

	@Transient
	private boolean wohnsitzNichtInGemeindeGS2;

	@Transient
	private boolean kindMinestalterUnterschritten;

	@Transient
	// Wenn Vollkosten bezahlt werden muessen, werden die Vollkosten berechnet und als Elternbeitrag gesetzt
	private boolean bezahltVollkosten;

	@Transient
	private int anspruchspensumRest;

	@Max(100)
	@Min(0)
	@NotNull
	@Column(nullable = false)
	private int betreuungspensum;

	@Max(100)
	@Min(0)
	@NotNull
	@Column(nullable = false)
	private int anspruchberechtigtesPensum; // = Anpsruch für diese Kita, bzw. Tageseltern Kleinkinder


	@Column(nullable = true)
	private BigDecimal betreuungsstunden;

	@Column(nullable = true)
	private BigDecimal vollkosten = ZERO;

	@Column(nullable = true)
	private BigDecimal elternbeitrag = ZERO;

	@Column(nullable = true)
	private BigDecimal abzugFamGroesse = null;

	@Column(nullable = true)
	private BigDecimal massgebendesEinkommenVorAbzugFamgr = ZERO;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen = "";

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_verfuegung_zeitabschnitt_verfuegung_id"), nullable = false)
	private Verfuegung verfuegung;


	public VerfuegungZeitabschnitt() {
	}

	/**
	 * Erstellt einen Zeitabschnitt mit der gegebenen gueltigkeitsdauer
	 */
	public VerfuegungZeitabschnitt(DateRange gueltigkeit) {
		this.setGueltigkeit(new DateRange(gueltigkeit));
	}

	public int getErwerbspensumGS1() {
		return erwerbspensumGS1;
	}

	public void setErwerbspensumGS1(int erwerbspensumGS1) {
		this.erwerbspensumGS1 = erwerbspensumGS1;
	}

	public int getErwerbspensumGS2() {
		return erwerbspensumGS2;
	}

	public void setErwerbspensumGS2(int erwerbspensumGS2) {
		this.erwerbspensumGS2 = erwerbspensumGS2;
	}

	public int getBetreuungspensum() {
		return betreuungspensum;
	}

	public void setBetreuungspensum(int betreuungspensum) {
		this.betreuungspensum = betreuungspensum;
	}

	public int getFachstellenpensum() {
		return fachstellenpensum;
	}

	public void setFachstellenpensum(int fachstellenpensum) {
		this.fachstellenpensum = fachstellenpensum;
	}

	public int getAnspruchspensumRest() {
		return anspruchspensumRest;
	}

	public void setAnspruchspensumRest(int anspruchspensumRest) {
		this.anspruchspensumRest = anspruchspensumRest;
	}


	public void setAnspruchberechtigtesPensum(int anspruchberechtigtesPensum) {
		this.anspruchberechtigtesPensum = anspruchberechtigtesPensum;
	}

	public BigDecimal getBetreuungsstunden() {
		return betreuungsstunden;
	}

	public void setBetreuungsstunden(BigDecimal betreuungsstunden) {
		this.betreuungsstunden = betreuungsstunden;
	}

	public BigDecimal getVollkosten() {
		return vollkosten;
	}

	public void setVollkosten(BigDecimal vollkosten) {
		this.vollkosten = vollkosten;
	}

	public BigDecimal getElternbeitrag() {
		return elternbeitrag;
	}

	public void setElternbeitrag(BigDecimal elternbeitrag) {
		this.elternbeitrag = elternbeitrag;
	}

	public BigDecimal getAbzugFamGroesse() {
		return abzugFamGroesse;
	}

	public void setAbzugFamGroesse(BigDecimal abzugFamGroesse) {
		this.abzugFamGroesse = abzugFamGroesse;
	}

	/**
	 * @return berechneter Wert. Zieht vom massgebenenEinkommenVorAbzug den Familiengroessen Abzug ab
	 */
	public BigDecimal getMassgebendesEinkommen() {
		return MathUtil.EXACT.subtract(massgebendesEinkommenVorAbzugFamgr,
			this.abzugFamGroesse == null ? BigDecimal.ZERO : this.abzugFamGroesse);
	}


	public BigDecimal getMassgebendesEinkommenVorAbzFamgr() {
		return massgebendesEinkommenVorAbzugFamgr;
	}

	public void setMassgebendesEinkommenVorAbzugFamgr(BigDecimal massgebendesEinkommenVorAbzugFamgr) {
		this.massgebendesEinkommenVorAbzugFamgr = massgebendesEinkommenVorAbzugFamgr;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public Verfuegung getVerfuegung() {
		return verfuegung;
	}

	public void setVerfuegung(Verfuegung verfuegung) {
		this.verfuegung = verfuegung;
	}

	public boolean isZuSpaetEingereicht() {
		return zuSpaetEingereicht;
	}

	public void setZuSpaetEingereicht(boolean zuSpaetEingereicht) {
		this.zuSpaetEingereicht = zuSpaetEingereicht;
	}

	public boolean isBezahltVollkosten() {
		return bezahltVollkosten;
	}

	public void setBezahltVollkosten(boolean bezahltVollkosten) {
		this.bezahltVollkosten = bezahltVollkosten;
	}

	public boolean isWohnsitzNichtInGemeindeGS1() {
		return wohnsitzNichtInGemeindeGS1;
	}

	public void setWohnsitzNichtInGemeindeGS1(boolean wohnsitzNichtInGemeindeGS1) {
		this.wohnsitzNichtInGemeindeGS1 = wohnsitzNichtInGemeindeGS1;
	}

	public boolean isWohnsitzNichtInGemeindeGS2() {
		return wohnsitzNichtInGemeindeGS2;
	}

	public void setWohnsitzNichtInGemeindeGS2(boolean wohnsitzNichtInGemeindeGS2) {
		this.wohnsitzNichtInGemeindeGS2 = wohnsitzNichtInGemeindeGS2;
	}

	public boolean isKindMinestalterUnterschritten() {
		return kindMinestalterUnterschritten;
	}

	public void setKindMinestalterUnterschritten(boolean kindMinestalterUnterschritten) {
		this.kindMinestalterUnterschritten = kindMinestalterUnterschritten;
	}

	/**
	 * Addiert die Daten von "other" zu diesem VerfuegungsZeitabschnitt
	 */
	public void add(VerfuegungZeitabschnitt other) {
		this.setBetreuungspensum(this.getBetreuungspensum() + other.getBetreuungspensum());
		this.setFachstellenpensum(this.getFachstellenpensum() + other.getFachstellenpensum());
		this.setAnspruchspensumRest(this.getAnspruchspensumRest() + other.getAnspruchspensumRest());
		this.setAnspruchberechtigtesPensum(this.getAnspruchberechtigtesPensum() + other.getAnspruchberechtigtesPensum());
		BigDecimal newBetreuungsstunden = ZERO;
		if (this.getBetreuungsstunden() != null) {
			newBetreuungsstunden = newBetreuungsstunden.add(this.getBetreuungsstunden());
		}
		if (other.getBetreuungsstunden() != null) {
			newBetreuungsstunden = newBetreuungsstunden.add(other.getBetreuungsstunden());
		}
		this.setBetreuungsstunden(newBetreuungsstunden);
		this.setErwerbspensumGS1(this.getErwerbspensumGS1() + other.getErwerbspensumGS1());
		this.setErwerbspensumGS2(this.getErwerbspensumGS2() + other.getErwerbspensumGS2());
		BigDecimal massgebendesEinkommenVorAbzugFamgr = ZERO;
		if (this.getMassgebendesEinkommenVorAbzFamgr() != null) {
			massgebendesEinkommenVorAbzugFamgr = massgebendesEinkommenVorAbzugFamgr.add(this.getMassgebendesEinkommenVorAbzFamgr());
		}
		if (other.getMassgebendesEinkommenVorAbzFamgr() != null) {
			massgebendesEinkommenVorAbzugFamgr = massgebendesEinkommenVorAbzugFamgr.add(other.getMassgebendesEinkommenVorAbzFamgr());
		}
		this.setMassgebendesEinkommenVorAbzugFamgr(massgebendesEinkommenVorAbzugFamgr);

		this.addBemerkung(other.getBemerkungen());
		this.setZuSpaetEingereicht(this.isZuSpaetEingereicht() || other.isZuSpaetEingereicht());
		this.setWohnsitzNichtInGemeindeGS1(this.isWohnsitzNichtInGemeindeGS1() || other.isWohnsitzNichtInGemeindeGS1());
		this.setWohnsitzNichtInGemeindeGS2(this.isWohnsitzNichtInGemeindeGS2() || other.isWohnsitzNichtInGemeindeGS2());
		this.setBezahltVollkosten(this.isBezahltVollkosten() || other.isBezahltVollkosten());
		this.setKindMinestalterUnterschritten(this.isKindMinestalterUnterschritten() || other.isKindMinestalterUnterschritten());
		// Der Familiengroessen Abzug kann nicht linear addiert werden, daher darf es hier nie uebschneidungen geben
		if (other.getAbzugFamGroesse() != null) {
			Validate.isTrue(this.getAbzugFamGroesse() == null, "Familiengoressenabzug kann nicht gemerged werden" );
			this.setAbzugFamGroesse(other.getAbzugFamGroesse());
		}
	}

	public void addBemerkung(RuleKey ruleKey, MsgKey msgKey) {
		String bemerkungsText =  ServerMessageUtil.translateEnumValue(msgKey);
		this.addBemerkung(ruleKey.name() + ": " + bemerkungsText);

		}

	public void addBemerkung(RuleKey ruleKey, MsgKey msgKey, Object... args) {
		String bemerkungsText =  ServerMessageUtil.translateEnumValue(msgKey, args);
		this.addBemerkung(ruleKey.name() + ": " + bemerkungsText);
	}

	/**
	 * Fügt eine Bemerkung zur Liste hinzu
	 */
	public void addBemerkung(String bem) {
		this.bemerkungen = Joiner.on("\n").skipNulls().join(
			StringUtils.defaultIfBlank(this.bemerkungen, null),
			StringUtils.defaultIfBlank(bem, null)
		);
	}

	/**
	 * Fügt mehrere Bemerkungen zur Liste hinzu
	 */
	public void addAllBemerkungen(@Nonnull List<String> bemerkungenList) {
		List<String> listOfStrings = new ArrayList<>();
		listOfStrings.add(this.bemerkungen);
		listOfStrings.addAll(bemerkungenList);
		this.bemerkungen = String.join(";", listOfStrings);
	}

	/**
	 * Dieses Pensum ist abhängig vom Erwerbspensum der Eltern respektive von dem durch die Fachstelle definierten Pensum.
	 * <p>
	 * Dieses Pensum kann grösser oder kleiner als das Betreuungspensum sein.
	 * <p>
	 * Beispiel: Zwei Eltern arbeiten zusammen 140%. In diesem Fall ist das anspruchsberechtigte Pensum 40%.
	 */

	public int getAnspruchberechtigtesPensum() {
		return anspruchberechtigtesPensum;
	}

	/**
	 * Das BG-Pensum (Pensum des Gutscheins) wird zum BG-Tarif berechnet und kann höchstens so gross sein, wie das Betreuungspensum.
	 * Falls das anspruchsberechtigte Pensum unter dem Betreuungspensum liegt, entspricht das BG-Pensum dem
	 * anspruchsberechtigten Pensum.
	 * <p>
	 * Ein Kind mit einem Betreuungspensum von 60% und einem anspruchsberechtigten Pensum von 40% hat ein BG-Pensum von 40%.
	 * Ein Kind mit einem Betreuungspensum von 40% und einem anspruchsberechtigten Pensum von 60% hat ein BG-Pensum von 40%.
	 */
	public int getBgPensum() {
		return Math.min(getBetreuungspensum(), getAnspruchberechtigtesPensum());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(Constants.DATE_FORMATTER.format(getGueltigkeit().getGueltigAb())).append(" - ").append(Constants.DATE_FORMATTER.format(getGueltigkeit().getGueltigBis())).append("] ")
			.append(" EP GS1: ").append(erwerbspensumGS1).append("\t")
			.append(" EP GS2: ").append(erwerbspensumGS2).append("\t")
			.append(" BetrPensum: ").append(betreuungspensum).append("\t")
			.append(" Anspruch: ").append(anspruchberechtigtesPensum).append("\t")
			.append(" BG-Pensum: ").append(getBgPensum()).append("\t")
			.append(" Vollkosten: ").append(vollkosten).append("\t")
			.append(" Elternbeitrag: ").append(elternbeitrag).append("\t")
			.append(" Bemerkungen: ").append(bemerkungen).append("\t")
			.append(" Einkommen: ").append(massgebendesEinkommenVorAbzugFamgr).append("\t")
			.append(" Abzug Fam: ").append(abzugFamGroesse);
		return sb.toString();
	}

	public boolean isSame(VerfuegungZeitabschnitt that) {
		if (this == that) {
			return true;
		}
		return erwerbspensumGS1 == that.erwerbspensumGS1 &&
			erwerbspensumGS2 == that.erwerbspensumGS2 &&
			betreuungspensum == that.betreuungspensum &&
			fachstellenpensum == that.fachstellenpensum &&
			anspruchspensumRest == that.anspruchspensumRest &&
			anspruchberechtigtesPensum == that.anspruchberechtigtesPensum &&
			Objects.equals(abzugFamGroesse, that.abzugFamGroesse) &&
			Objects.equals(massgebendesEinkommenVorAbzugFamgr, that.massgebendesEinkommenVorAbzugFamgr) &&
			zuSpaetEingereicht == that.zuSpaetEingereicht &&
			wohnsitzNichtInGemeindeGS1 == that.wohnsitzNichtInGemeindeGS1 &&
			wohnsitzNichtInGemeindeGS2 == that.wohnsitzNichtInGemeindeGS2 &&
			bezahltVollkosten == that.bezahltVollkosten &&
			kindMinestalterUnterschritten == that.kindMinestalterUnterschritten;
	}

	/**
	 * Gibt den Betrag des Gutscheins zurück.
	 */
	public BigDecimal getVerguenstigung() {
		if (vollkosten != null && elternbeitrag != null) {
			return vollkosten.subtract(elternbeitrag);
		}
		return ZERO;
	}

	@Override
	public int compareTo(VerfuegungZeitabschnitt other) {
		int result = this.getGueltigkeit().getGueltigAb().compareTo(other.getGueltigkeit().getGueltigAb());
		if (result == 0) {
			result = this.getGueltigkeit().getGueltigBis().compareTo(other.getGueltigkeit().getGueltigBis());
		}
		return result;
	}
}
