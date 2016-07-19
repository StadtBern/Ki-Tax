package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Configurator, welcher die Regeln und ihre Reihenfolge konfiguriert. Als Parameter erhält er den Mandanten sowie
 * die benötigten Ebegu-Parameter
 */
public class BetreuungsgutscheinConfigurator {

	private final DateRange defaultGueltigkeit = Constants.DEFAULT_GUELTIGKEIT;

	private List<Rule> rules = new LinkedList<>();

	public List<Rule> configureRulesForMandant(Mandant mandant, Map<EbeguParameterKey, EbeguParameter> ebeguParameter) {
		// TODO (team) Mandant abfragen, sobald es mehrere hat!
		useBernerRules(ebeguParameter);
		return rules;
	}


	private void useBernerRules(Map<EbeguParameterKey, EbeguParameter> ebeguParameter) {

		// GRUNDREGELN

		// 1. Erwerbspensum: Erstellt die grundlegenden Zeitschnitze (keine Korrekturen, nur einfügen)
		ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);
		rules.add(erwerbspensumRule);

		// 2. Betreuungspensum
		// - Daten Fachstelle
		FachstelleDataRule fachstelleDataRule = new FachstelleDataRule(defaultGueltigkeit);
		rules.add(fachstelleDataRule);
		// - Daten Betreuung
		BetreuungspensumDataRule betreuungspensumDataRule = new BetreuungspensumDataRule(defaultGueltigkeit);
		rules.add(betreuungspensumDataRule);
		// - Berechnen
		BetreuungspensumCalcRule betreuungspensumCalcRule = new BetreuungspensumCalcRule(defaultGueltigkeit);
		rules.add(betreuungspensumCalcRule);

		// 3. Einkommen / Einkommensverschlechterung / Maximales Einkommen
		EbeguParameter paramMassgebendesEinkommenMax = ebeguParameter.get(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX);
		Objects.requireNonNull(paramMassgebendesEinkommenMax, "Parameter PARAM_MASSGEBENDES_EINKOMMEN_MAX muss gesetzt sein");
		MaximalesEinkommenRule maxEinkommenRule = new MaximalesEinkommenRule(defaultGueltigkeit, paramMassgebendesEinkommenMax.getAsBigDecimal());
		rules.add(maxEinkommenRule);

		// REDUKTIONSREGELN

		// Abwesenheit
		AbwesenheitRule abwesenheitRule = new AbwesenheitRule(defaultGueltigkeit);
		rules.add(abwesenheitRule);

		// Betreuungsangebot Tagesschule nicht berechnen
		BetreuungsangebotTypRule betreuungsangebotTypRule = new BetreuungsangebotTypRule(defaultGueltigkeit);
		rules.add(betreuungsangebotTypRule);

		// Mindestalter Kind
		MindestalterRule mindestalterRule = new MindestalterRule(defaultGueltigkeit);
		rules.add(mindestalterRule);

		// Wohnsitz (Zuzug und Wegzug)
		WohnsitzRule wohnsitzRule = new WohnsitzRule(defaultGueltigkeit);
		rules.add(wohnsitzRule);

		// Einreichungsfrist
		EinreichungsfristRule einreichungsfristRule = new EinreichungsfristRule(defaultGueltigkeit);
		rules.add(einreichungsfristRule);
	}
}
