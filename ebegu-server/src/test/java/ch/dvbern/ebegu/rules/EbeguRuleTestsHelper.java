package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.Constants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator.createInitialenRestanspruch;

/**
 * Created by hefr on 24.08.16.
 */
public class EbeguRuleTestsHelper {

	private static BigDecimal MAX_EINKOMMEN = new BigDecimal("159000");

	private static final ErwerbspensumAbschnittRule erwerbspensumAbschnittRule = new ErwerbspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final ErwerbspensumCalcRule erwerbspensumCalcRule = new ErwerbspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final FachstelleAbschnittRule fachstelleAbschnittRule = new FachstelleAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final FachstelleCalcRule fachstelleCalcRule = new FachstelleCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final BetreuungspensumAbschnittRule betreuungspensumAbschnittRule = new BetreuungspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final BetreuungspensumCalcRule betreuungspensumCalcRule = new BetreuungspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final RestanspruchCalcRule restanspruchCalcRule = new RestanspruchCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final RestanspruchEvaluator restanspruchEvaluator = new RestanspruchEvaluator(Constants.DEFAULT_GUELTIGKEIT);
	private static final WohnhaftImGleichenHaushaltCalcRule wohnhaftImGleichenHaushaltRule = new WohnhaftImGleichenHaushaltCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final EinkommenAbschnittRule einkommenAbschnittRule = new EinkommenAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final EinkommenCalcRule maximalesEinkommenCalcRule = new EinkommenCalcRule(Constants.DEFAULT_GUELTIGKEIT, MAX_EINKOMMEN);
	private static final BetreuungsangebotTypCalcRule betreuungsangebotTypCalcRule = new BetreuungsangebotTypCalcRule(Constants.DEFAULT_GUELTIGKEIT);

	protected final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	protected final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	protected static List<VerfuegungZeitabschnitt> calculate(Betreuung betreuung) {
		// Abschnitte
		List<VerfuegungZeitabschnitt> result = erwerbspensumAbschnittRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = fachstelleAbschnittRule.calculate(betreuung, result);
		result = einkommenAbschnittRule.calculate(betreuung, result);
		// Anspruch
		result = erwerbspensumCalcRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		result = fachstelleCalcRule.calculate(betreuung, result);
		// Wohnhaft
		result = wohnhaftImGleichenHaushaltRule.calculate(betreuung, result);
		// Restanspruch
		result = restanspruchCalcRule.calculate(betreuung, result);
		// Reduktionen
		result = maximalesEinkommenCalcRule.calculate(betreuung, result);
		result = betreuungsangebotTypCalcRule.calculate(betreuung, result);
		return result;
	}
}
