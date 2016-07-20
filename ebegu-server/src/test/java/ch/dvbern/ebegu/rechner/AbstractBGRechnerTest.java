package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinConfigurator;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Before;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Superklasse für BG-Rechner-Tests
 */
public class AbstractBGRechnerTest {


	protected BetreuungsgutscheinEvaluator evaluator;

	@Before
	public void setUpCalcuator() {
		Map<EbeguParameterKey, EbeguParameter> ebeguParameter = new HashMap<>();
		EbeguParameter paramMaxEinkommen = new EbeguParameter(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX, "159000");
		ebeguParameter.put(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX, paramMaxEinkommen);
		BetreuungsgutscheinConfigurator configurator = new BetreuungsgutscheinConfigurator();
		List<Rule> rules = configurator.configureRulesForMandant(null, ebeguParameter);
		evaluator = new BetreuungsgutscheinEvaluator(rules);
	}

	/**
	 * Stellt alle für die Berechnung benötigten Parameter zusammen
     */
	protected BGRechnerParameterDTO getParameter() {
		BGRechnerParameterDTO parameterDTO = new BGRechnerParameterDTO();
		parameterDTO.setBeitragKantonProTag(new BigDecimal("107.19"));
		parameterDTO.setBeitragStadtProTag(new BigDecimal("7"));
		parameterDTO.setAnzahlTageMaximal(new BigDecimal("244"));
		parameterDTO.setAnzahlStundenProTagMaximal(new BigDecimal("11.5"));
		parameterDTO.setKostenProStundeMaximalKitaTagi(new BigDecimal("11.91"));
		parameterDTO.setKostenProStundeMinimal(new BigDecimal("0.75"));
		parameterDTO.setMassgebendesEinkommenMaximal(new BigDecimal("158690"));
		parameterDTO.setMassgebendesEinkommenMinimal(new BigDecimal("42540"));
		parameterDTO.setAnzahlTageTagi(new BigDecimal("240"));
		parameterDTO.setAnzahlStundenProTagTagi(new BigDecimal("7"));
		parameterDTO.setKostenProStundeMaximalTageseltern(new BigDecimal("9.16"));
		parameterDTO.setBabyAlterInMonaten(12);
		parameterDTO.setBabyFaktor(new BigDecimal("1.5"));
		return parameterDTO;
	}

	/**
	 * Erstellt eine Verfügung mit einem einzelnen Zeitabschnitt und den für Tagi und Tageseltern notwendigen
	 * Parametern zusammen
     */
	protected Verfuegung prepareVerfuegungTagiUndTageseltern(LocalDate von, LocalDate bis, int anspruch, BigDecimal massgebendesEinkommen) {
		return createVerfuegung(von, bis, anspruch, massgebendesEinkommen);
	}

	/**
	 * Erstellt eine Verfügung mit einem einzelnen Zeitabschnitt und den für Kita notwendigen
	 * Parametern zusammen
     */
	protected Verfuegung prepareVerfuegungKita(LocalDate geburtsdatumKind,
										 BigDecimal anzahlTageKita, BigDecimal anzahlStundenProTagKita,
										 LocalDate von, LocalDate bis,
										 int anspruch, BigDecimal massgebendesEinkommen) {

		Betreuung betreuung = new Betreuung();
		InstitutionStammdaten institutionStammdaten = new InstitutionStammdaten();
		institutionStammdaten.setOeffnungsstunden(anzahlStundenProTagKita);
		institutionStammdaten.setOeffnungstage(anzahlTageKita);
		betreuung.setInstitutionStammdaten(institutionStammdaten);
		Kind kind = new Kind();
		kind.setGeburtsdatum(geburtsdatumKind);
		KindContainer kindContainer = new KindContainer();
		kindContainer.setKindJA(kind);
		betreuung.setKind(kindContainer);

		Verfuegung verfuegung = createVerfuegung(von, bis, anspruch, massgebendesEinkommen);
		verfuegung.setBetreuung(betreuung);
		return verfuegung;
	}

	/**
	 * Erstellt eine Verfügung mit den übergebenen Parametern
     */
	private Verfuegung createVerfuegung(LocalDate von, LocalDate bis, int anspruch, BigDecimal massgebendesEinkommen) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(new DateRange(von, bis));
		zeitabschnitt.setAnspruchberechtigtesPensum(anspruch);
		zeitabschnitt.setMassgebendesEinkommen(massgebendesEinkommen);
		List<VerfuegungZeitabschnitt> zeitabschnittList = new ArrayList<>();
		zeitabschnittList.add(zeitabschnitt);
		Verfuegung verfuegung = new Verfuegung();
		verfuegung.setZeitabschnitte(zeitabschnittList);
		return verfuegung;
	}
}
