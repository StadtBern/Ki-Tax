package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dieses Objekt repraesentiert einen Zeitabschnitt wahrend eines Betreeungsgutscheinantrags waehrend dem die Faktoren
 * die fuer die Berechnung des Gutscheins der Betreuung relevant sind konstant geblieben sind.
 *
 */
public class BGPensumZeitabschnitt extends AbstractDateRangedEntity{

	private static final long serialVersionUID = 7250339356897563374L;


	private int betreuungspensum;
	private int anspruchspensumOriginal;
	private int anspruchberechtigtesPensum;
	private BigDecimal vollkosten;
	private BigDecimal elternbeitrag;
	private BigDecimal verguenstigung;
	private BigDecimal abzugFamGroesse;
	private BigDecimal massgebendesEinkommen;

	private List<String> bemerkungen;

	private String status;


	/**
	 * Erstellt einen Zeitabschnitt mit der gegebenen gueltigkeitsdauer
	 * @param gueltigkeit
	 */
	public BGPensumZeitabschnitt(DateRange gueltigkeit) {
		this.setGueltigkeit(new DateRange(gueltigkeit));

	}
}
