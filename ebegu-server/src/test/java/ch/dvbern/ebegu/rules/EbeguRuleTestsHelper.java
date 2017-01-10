package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.rules.initalizer.RestanspruchInitializer;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator.createInitialenRestanspruch;

/**
 * Hilfsklasse fuer Ebegu-Rule-Tests
 */
public class EbeguRuleTestsHelper {

	private static BigDecimal MAX_EINKOMMEN = new BigDecimal("159000");

	private static final ErwerbspensumAbschnittRule erwerbspensumAbschnittRule = new ErwerbspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final ErwerbspensumCalcRule erwerbspensumCalcRule = new ErwerbspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final FachstelleAbschnittRule fachstelleAbschnittRule = new FachstelleAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final FachstelleCalcRule fachstelleCalcRule = new FachstelleCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final BetreuungspensumAbschnittRule betreuungspensumAbschnittRule = new BetreuungspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final BetreuungspensumCalcRule betreuungspensumCalcRule = new BetreuungspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final RestanspruchLimitCalcRule restanspruchLimitCalcRule = new RestanspruchLimitCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final WohnhaftImGleichenHaushaltCalcRule wohnhaftImGleichenHaushaltRule = new WohnhaftImGleichenHaushaltCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final EinkommenAbschnittRule einkommenAbschnittRule = new EinkommenAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final EinkommenCalcRule maximalesEinkommenCalcRule = new EinkommenCalcRule(Constants.DEFAULT_GUELTIGKEIT, MAX_EINKOMMEN);
	private static final BetreuungsangebotTypCalcRule betreuungsangebotTypCalcRule = new BetreuungsangebotTypCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final EinreichungsfristAbschnittRule einreichungsfristAbschnittRule = new EinreichungsfristAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final EinreichungsfristCalcRule einreichungsfristCalcRule = new EinreichungsfristCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final WohnsitzAbschnittRule wohnsitzAbschnittRule = new WohnsitzAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final WohnsitzCalcRule wohnsitzCalcRule = new WohnsitzCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final MindestalterAbschnittRule mindestalterAbschnittRule = new MindestalterAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final MindestalterCalcRule mindestalterCalcRule = new MindestalterCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final AbwesenheitAbschnittRule abwesenheitAbschnittRule = new AbwesenheitAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final AbwesenheitCalcRule abwesenheitCalcRule = new AbwesenheitCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final ZivilstandsaenderungAbschnittRule zivilstandsaenderungAbschnittRule = new ZivilstandsaenderungAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private static final RestanspruchInitializer restanspruchInitializer = new RestanspruchInitializer();

	protected static List<VerfuegungZeitabschnitt> calculate(Betreuung betreuung) {
		// Abschnitte
		List<VerfuegungZeitabschnitt> initialenRestanspruchAbschnitte = createInitialenRestanspruch(betreuung.extractGesuchsperiode());
		TestDataUtil.calculateFinanzDaten(betreuung.extractGesuch());
		return calculate(betreuung, initialenRestanspruchAbschnitte);
	}

	/**
	 * Testhilfsmethode die eine Betreuung so berechnet als haette es vorher schon eine Betreuung gegeben welche einen Teil des anspruchs
	 * aufgebraucht hat, es wird  als bestehnder Restnanspruch der Wert von existingRestanspruch genommen
	 */
	protected static List<VerfuegungZeitabschnitt> calculateWithRemainingRestanspruch(Betreuung betreuung, int existingRestanspruch) {
		// Abschnitte
		List<VerfuegungZeitabschnitt> initialenRestanspruchAbschnitte = createInitialenRestanspruch(betreuung.extractGesuchsperiode());
		TestDataUtil.calculateFinanzDaten(betreuung.extractGesuch());
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : initialenRestanspruchAbschnitte) {
			verfuegungZeitabschnitt.setAnspruchspensumRest(existingRestanspruch);
		}
		return calculate(betreuung, initialenRestanspruchAbschnitte);
	}

	@Nonnull
	private static List<VerfuegungZeitabschnitt> calculate(Betreuung betreuung, List<VerfuegungZeitabschnitt> initialenRestanspruchAbschnitte) {
		List<VerfuegungZeitabschnitt> result = erwerbspensumAbschnittRule.calculate(betreuung, initialenRestanspruchAbschnitte);
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = fachstelleAbschnittRule.calculate(betreuung, result);
		result = zivilstandsaenderungAbschnittRule.calculate(betreuung, result);
		result = einkommenAbschnittRule.calculate(betreuung, result);
		result = einreichungsfristAbschnittRule.calculate(betreuung, result);
		result = wohnsitzAbschnittRule.calculate(betreuung, result);
		result = mindestalterAbschnittRule.calculate(betreuung, result);
		result = abwesenheitAbschnittRule.calculate(betreuung, result);
		// Anspruch
		result = erwerbspensumCalcRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		result = fachstelleCalcRule.calculate(betreuung, result);
		// Wohnhaft
		result = wohnhaftImGleichenHaushaltRule.calculate(betreuung, result);
		// Restanspruch
		// Reduktionen
		result = maximalesEinkommenCalcRule.calculate(betreuung, result);
		result = betreuungsangebotTypCalcRule.calculate(betreuung, result);
		result = einreichungsfristCalcRule.calculate(betreuung, result);
		result = wohnsitzCalcRule.calculate(betreuung, result);
		result = mindestalterCalcRule.calculate(betreuung, result);
		result = abwesenheitCalcRule.calculate(betreuung, result);
		result = restanspruchLimitCalcRule.calculate(betreuung, result);
		return result;
	}

	public static List<VerfuegungZeitabschnitt> initializeRestanspruchForNextBetreuung(Betreuung currentBetreuung, List<VerfuegungZeitabschnitt> zeitabschnitte){
		return restanspruchInitializer.createVerfuegungsZeitabschnitte(currentBetreuung, zeitabschnitte);
	}

	public static Betreuung createBetreuungWithPensum(LocalDate von, LocalDate bis, BetreuungsangebotTyp angebot, int pensum) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(angebot);
		betreuung.setBetreuungspensumContainers(new LinkedHashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		DateRange gueltigkeit = new DateRange(von, bis);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum(gueltigkeit));
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		return betreuung;
	}
}
