package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.VerfuegungZeitabschnittComparator;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hefr on 27.06.16.
 */
public class ErwerbspensumRuleTest {

	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);

	private final LocalDate DATUM_1 = LocalDate.of(2016, Month.APRIL, 1);
	private final LocalDate DATUM_2 = LocalDate.of(2016, Month.SEPTEMBER, 1);
	private final LocalDate DATUM_3 = LocalDate.of(2016, Month.OCTOBER, 1);
	private final LocalDate DATUM_4 = LocalDate.of(2016, Month.DECEMBER, 1);


	@Test
	public void createVerfuegungsZeitabschnitte() throws Exception {

	}

	@Test
	public void testNurEinErwerbspensumBeiZweiGesuchstellern() throws Exception {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer();
		Gesuch gesuch = betreuungspensumContainer.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(createErwerbspensum(Constants.START_OF_TIME, Constants.END_OF_TIME, 80, 10));
		gesuch.getGesuchsteller1().addErwerbspensumContainer(createErwerbspensum(DATUM_2, DATUM_4, 60, 0));

//		Betreuung defaultBetreuung = TestDataUtil.createDefaultBetreuung();
//		BetreuungspensumContainer betPensContainer = TestDataUtil.createBetPensContainer(defaultBetreuung);

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000")));
		for (VerfuegungZeitabschnitt zeitabschnitt : result) {
			System.out.println(zeitabschnitt);
		};
		Collections.sort(result, new VerfuegungZeitabschnittComparator());

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.size());
		VerfuegungZeitabschnitt first = result.get(0);
		VerfuegungZeitabschnitt second = result.get(1);
		VerfuegungZeitabschnitt third = result.get(2);
		Assert.assertEquals(DATUM_1, first.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_2.minusDays(1), first.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_2, second.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_3, second.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(DATUM_3.plusDays(1), third.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(DATUM_4, third.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(40, first.getErwerbspensumGS1());
		Assert.assertEquals(100, second.getErwerbspensumGS1());
		Assert.assertEquals(60, third.getErwerbspensumGS1());


	}


//	private BetreuungspensumContainer createGesuchWithBetreuungspensumContainer() {
//		Gesuch gesuch = new Gesuch();
//		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
//		gesuch.setFamiliensituation(new Familiensituation());
//		gesuch.getFamiliensituation().setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
//		gesuch.getFamiliensituation().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
//		gesuch.setGesuchsteller1(new Gesuchsteller());
//		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
//		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationSV(new FinanzielleSituation());
//		gesuch.setGesuchsteller2(new Gesuchsteller());
//		gesuch.getGesuchsteller2().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
//		gesuch.getGesuchsteller2().getFinanzielleSituationContainer().setFinanzielleSituationSV(new FinanzielleSituation());
//
//		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
//		betreuungspensumContainer.setBetreuung(new Betreuung());
//		betreuungspensumContainer.getBetreuung().setKind(new KindContainer());
//		betreuungspensumContainer.getBetreuung().getKind().setGesuch(gesuch);
//		return betreuungspensumContainer;
//	}

	private ErwerbspensumContainer createErwerbspensum(LocalDate von, LocalDate bis, int pensum, int zuschlag) {
		ErwerbspensumContainer erwerbspensumContainer = new ErwerbspensumContainer();
		Erwerbspensum erwerbspensum = new Erwerbspensum();
		erwerbspensum.setPensum(pensum);
		erwerbspensum.setZuschlagsprozent(zuschlag);
		erwerbspensum.setGueltigkeit(new DateRange(von, bis));
		erwerbspensumContainer.setErwerbspensumJA(erwerbspensum);
		return erwerbspensumContainer;
	}

}
