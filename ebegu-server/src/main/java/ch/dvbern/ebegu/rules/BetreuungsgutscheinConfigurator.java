package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nonnull;
import java.util.*;

import static ch.dvbern.ebegu.enums.EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX;

/**
 * Configurator, welcher die Regeln und ihre Reihenfolge konfiguriert. Als Parameter erhält er den Mandanten sowie
 * die benötigten Ebegu-Parameter
 */
public class BetreuungsgutscheinConfigurator {

	private final DateRange defaultGueltigkeit = Constants.DEFAULT_GUELTIGKEIT;

	private List<Rule> rules = new LinkedList<>();

	public List<Rule> configureRulesForMandant(@Nonnull Mandant mandant, @Nonnull Map<EbeguParameterKey, EbeguParameter> ebeguRuleParameter) {
		// TODO (team) Mandant abfragen, sobald es mehrere hat!
		useBernerRules(ebeguRuleParameter);
		return rules;
	}

	public Set<EbeguParameterKey> getRequiredParametersForMandant(@Nonnull Mandant mandant){
		// TODO (team) Mandant abfragen, sobald es mehrere hat!
		return requiredBernerParameters();
	}

	public Set<EbeguParameterKey> requiredBernerParameters(){
		Set<EbeguParameterKey> requiredParams = EnumSet.of(PARAM_MASSGEBENDES_EINKOMMEN_MAX);
		return requiredParams;
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
		BetreuungspensumAbschnittRule betreuungspensumAbschnittRule = new BetreuungspensumAbschnittRule(defaultGueltigkeit);
		rules.add(betreuungspensumAbschnittRule);
		// - Berechnen
		BetreuungspensumCalcRule betreuungspensumCalcRule = new BetreuungspensumCalcRule(defaultGueltigkeit);
		rules.add(betreuungspensumCalcRule);

		// 3. Einkommen / Einkommensverschlechterung / Maximales Einkommen
		EinkommenAbschnittRule einkommenAbschnittRule = new EinkommenAbschnittRule(defaultGueltigkeit);
		rules.add(einkommenAbschnittRule);

		EbeguParameter paramMassgebendesEinkommenMax = ebeguParameter.get(PARAM_MASSGEBENDES_EINKOMMEN_MAX);
		Objects.requireNonNull(paramMassgebendesEinkommenMax, "Parameter PARAM_MASSGEBENDES_EINKOMMEN_MAX muss gesetzt sein");
		MaximalesEinkommenCalcRule maxEinkommenCalcRule = new MaximalesEinkommenCalcRule(defaultGueltigkeit, paramMassgebendesEinkommenMax.getValueAsBigDecimal());
		rules.add(maxEinkommenCalcRule);

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
