package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinConfigurator;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;
import org.junit.Assert;
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

	private static final MathUtil MATH = MathUtil.DEFAULT;


	@Before
	public void setUpCalcuator() {
		Map<EbeguParameterKey, EbeguParameter> ebeguParameter = new HashMap<>();
		EbeguParameter paramMaxEinkommen = new EbeguParameter(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX, "159000");
		ebeguParameter.put(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX, paramMaxEinkommen);
		BetreuungsgutscheinConfigurator configurator = new BetreuungsgutscheinConfigurator();
		List<Rule> rules = configurator.configureRulesForMandant(null, ebeguParameter);
		evaluator = new BetreuungsgutscheinEvaluator(rules);
	}

	public static void assertZeitabschnitt(VerfuegungZeitabschnitt abschnitt, int beantragtesPensum, int anspruchsberechtigtesPensum, int betreuungspensum, double vollkosten, double verguenstigung, double elternbeitrag) {
		Assert.assertEquals("Beantragtes Pensum " + beantragtesPensum+ " entspricht nicht " +abschnitt , beantragtesPensum, abschnitt.getBetreuungspensum());
		Assert.assertEquals(anspruchsberechtigtesPensum, abschnitt.getErwerbspensumMinusOffset());
		Assert.assertEquals(betreuungspensum, abschnitt.getAnspruchberechtigtesPensum());
		Assert.assertEquals(MATH.from(vollkosten), abschnitt.getVollkosten());
		Assert.assertEquals(MATH.from(verguenstigung), abschnitt.getVerguenstigung());
		Assert.assertEquals(MATH.from(elternbeitrag), abschnitt.getElternbeitrag());
	}

	/**
	 * Stellt alle für die Berechnung benötigten Parameter zusammen
     */
	protected BGRechnerParameterDTO getParameter() {
		BGRechnerParameterDTO parameterDTO = new BGRechnerParameterDTO();
		parameterDTO.setBeitragKantonProTagJahr1(new BigDecimal("107.19"));
		parameterDTO.setBeitragKantonProTagJahr2(new BigDecimal("107.19"));
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

	/**
	 * hilfsmethode um den {@link ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar} auf
	 * korrekte berechnung zu pruefen
	 */
	public static void checkTestfallWaeltiDagmar(Gesuch gesuch) {
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				if (betreuung.getInstitutionStammdaten().getInstitution().getId().equals(AbstractTestfall.idInstitutionAaregg)) {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Erster Monat
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 80, 80, 80, 1827.05, 1562.25, 264.80);
					// Letzter Monat
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 80, 80, 80, 1827.05, 1562.25, 264.80);
					// Kein Anspruch mehr ab Februar
					VerfuegungZeitabschnitt februar = verfuegung.getZeitabschnitte().get(6);
					assertZeitabschnitt(februar, 0, 80, 0, 0, 0, 0);
				} else {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Noch kein Anspruch bis januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 0, 80, 0, 0, 0, 0);
					// Erster Monat
					VerfuegungZeitabschnitt februar = verfuegung.getZeitabschnitte().get(6);
					assertZeitabschnitt(februar, 40, 80, 40, 913.50, 781.10, 132.40);
					// Letzter Monat
					VerfuegungZeitabschnitt juli = verfuegung.getZeitabschnitte().get(11);
					assertZeitabschnitt(juli, 40, 80, 40, 913.50, 781.10, 132.40);
				}
			}
		}
	}
}
