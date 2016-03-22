package ch.dvbern.ebegu.enums;


import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

/**
 * Enum fuer die Laender (ISO 3166-1 Alpha 2 Code)
 */
public enum Land {

	CH("100", 1), //
	AL("201"), //
	AD("202"), //
	BE("204"), //
	BG("205"), //
	DK("206"), //
	DE("207", 2), //
	DE_OLD("208", false), //replaced by 207
	FI("211"), //
	FR("212", 4), //
	GR("214"), //
	GB("215"), //
	IE("216"), //
	IS("217"), //
	IT("218", 5), //
	JU_OLD("220", false), //
	LI("222"), //
	LU("223"), //
	MT("224"), //
	MC("226"), //
	NL("227"), //
	NO("228"), //
	AT("229", 3), //
	PL("230"), //
	PT("231"), //
	RO("232"), //
	SM("233"), //
	SE("234"), //
	RU_OLD("235", false), //
	ES("236"), //
	CZ_OLD("238", false), //
	TR("239"), //
	HU("240"), //
	VA("241"), //
	CY("242"), //
	SK("243"), //
	CZ("244"), //
	RS("248"), //serbien
	JU("249", false), //ex jugoslavien
	HR("250"), //
	SI("251"), //
	BA("252"), //
	ME("254"), //montenegro
	MK("255"), //
	/**
	 * Kosovo: Dies ist <b>kein ISO-Code</b>. Für die Schnittstelle Coop wird ein zweistelliger Code
	 * benötigt.
	 */
	XZ("256"), //Kosovo
	EE("260"), //
	LV("261"), //
	LT("262"), //
	MD("263"), //
	RU("264"), //
	UA("265"), //
	BY("266"), //
	GF("301"), //
	ET("302"), //
	DJ("303"), //
	DZ("304"), //
	AO("305"), //
	BW("307"), //
	BI("308"), //
	BJ("309"), //
	CI("310"), //
	GA("311"), //
	GM("312"), //
	GH("313"), //
	GW("314"), //
	GN("315"), //
	CM("317"), //
	CV("319"), //
	KE("320"), //
	KM("321"), //
	CG("322"), //
	CD("323"), //
	LS("324"), //
	LR("325"), //
	LY("326"), //
	MG("327"), //
	MW("329"), //
	ML("330"), //
	MA("331"), //
	MR("332"), //
	MU("333"), //
	MZ("334"), //
	NE("335"), //
	NG("336"), //
	BF("337"), //burkina faso
	ZW("340"), //
	RW("341"), //
	ZM("343"), //
	ST("344"), //sao tome
	SN("345"), //
	SC("346"), //
	SL("347"), //
	SO("348"), //
	ZA("349"), //
	SD("350"), //
	NA("351"), //Namibia
	SZ("352"), //
	TZ("353"), //
	TG("354"), //
	TD("356"), //
	TN("357"), //
	UG("358"), //
	EG("359"), //
	CF("360"), //
	ER("362"), //Eritrea
	EH("372"), //Westsahara
	AR("401"), //
	BS("402"), //
	BB("403"), //
	BO("405"), //
	BR("406"), //
	CL("407"), //
	CR("408"), //
	DO("409"), //
	EC("410"), //
	SV("411"), //
	GT("415"), //
	GY("417"), //
	HT("418"), //Haiti
	BZ("419"), //Belize
	HN("420"), //
	JM("421"), //
	CA("423"), //
	CO("424"), //
	CU("425"), //
	MX("427"), //
	NI("429"), //
	PA("430"), //
	PY("431"), //
	PE("432"), //
	SR("435"), //
	TT("436"), //
	UY("437"), //
	VE("438"), //
	US("439"), //
	GD("441"), //Grenada
	AG("442"), //Antigua und Barbuda
	LC("443"), //
	VC("444"), //St. Vincent
	KN("445"), //St. Kittis
	AF("501"), //
	BH("502"), //
	BT("503"), //
	BN("504"), //Brunei
	MM("505"), //
	LK("506"), //
	TW("507"), //
	CN("508"), //
	IN("510"), //
	ID("511"), //
	IQ("512"), //
	IR("513"), //
	IL("514"), //
	JP("515"), //
	YE("516"), //Jemen
	JO("517"), //
	KH("518"), //Kambodscha
	QA("519"), //
	KW("521"), //
	LA("522"), //
	LB("523"), //
	MY("525"), //
	MV("526"), //
	OM("527"), //
	MN("528"), //
	NP("529"), //
	KP("530"), //
	VN_OLD2("531", false), //replaced by 545
	AE("532"), //
	PK("533"), //
	PH("534"), //
	SA("535"), //
	IN_OLD("536", false), //replaced by 510
	SG("537"), //
	YE_OLD("538", false), //
	KR("539"), //
	VN_OLD("540", false), //replaced by 545
	SY("541"), //
	TH("542"), //
	VN("545"), //
	BD("546"), //
	TL("547"), // Timor-Leste
	PS("550"), // Palästina
	AM("560"), //
	AZ("561"), //
	GE("562"), //
	KZ("563"), //
	KG("564"), //
	TJ("565"), //
	TM("566"), //
	UZ("567"), //
	AU("601"), //
	FJ("602"), //
	NR("604"), //
	VU("605"), //
	NZ("607"), //
	PG("608"), //
	TO("610"), //
	WS("612"), //
	SB("614"), // Salomon Inseln
	TV("615"), //
	KI("616"), // Kiribati
	MH("617"), //
	FM("618"), //
	PW("619"), // Palau
	NICHTANERKANNT("997"), // ohne Nationalität und nicht anerkannte Nationalitäten
	STAATENLOS("998"), //
	UNBEKANNT("999");


	private final String bsvCode;
	private final boolean valid;
	private final int sortierung;

	private static final int SORTIERUNG_GELOESCHT = 0;
	private static final int SORTIERUNG_DEFAULT = 100;


	private Land(final String bsvCode, final boolean valid, final int sortierung) {
		this.bsvCode = bsvCode;
		this.valid = valid;
		this.sortierung = valid ? sortierung : SORTIERUNG_GELOESCHT;
	}

	private Land(final String bsvCode, final boolean valid) {
		this(bsvCode, valid, valid ? SORTIERUNG_DEFAULT : SORTIERUNG_GELOESCHT);
	}

	private Land(final String bsvCode, final int sortierung) {
		this(bsvCode, true, sortierung);
	}

	private Land(final String bsvCode) {
		this(bsvCode, true, SORTIERUNG_DEFAULT);
	}

	/**
	 * code defined by bsv (eg. "100"=CH) - never null
	 *
	 * @return
	 */
	public String getBsvCode() {
		return bsvCode;
	}

	/**
	 * Is this {@link Land} still valid? Unused and replaced codes will be marked as invalid.
	 *
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}


	public boolean isSchweiz() {
		return this.equals(Land.CH);
	}

	public boolean isPlzRelevant() {
		return isSchweiz() || this.equals(Land.LI);
	}

	public int getSortierung() {
		return sortierung;
	}

	public static Land getByBsvCode(final String bsvCode) {
		for (final Land land : values()) {
			if (bsvCode.equals(land.getBsvCode())) {
				return land;
			}
		}
		return null;
	}

	@Nullable
	public static Land getByBsvCode(final Integer bsvCode) {
		if (bsvCode == null) {
			return null;
		}
		for (final Land land : values()) {
			if (bsvCode.equals(Integer.valueOf(land.getBsvCode()))) {
				return land;
			}
		}
		return null;
	}

	@Nullable
	public static Land fromString(String code) {
		Validate.notNull(code, "Laendercode muss gesetzt sein");
		for (final Land land : values()) {
			if (code.equalsIgnoreCase(land.name())) {
				return land;
			}
		}
		throw new IllegalArgumentException("Unkown Country Code " + code);
	}
}
