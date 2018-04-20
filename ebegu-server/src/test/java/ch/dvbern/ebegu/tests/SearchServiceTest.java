/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ch.dvbern.ebegu.dto.suchfilter.smarttable.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.FinSitStatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.SearchService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ch.dvbern.ebegu.tets.TestDataUtil.createAndPersistFeutzYvonneGesuch;

/**
 * Arquillian Tests fuer die Klasse SearchService
 */
@SuppressWarnings("LocalVariableNamingConvention")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class SearchServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private Persistence persistence;
	@Inject
	private SearchService searchService;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private FallService fallService;
	@Inject
	private GesuchsperiodeService gesuchsperiodeService;
	@Inject
	private GesuchService gesuchService;


	@Test
	public void testSearchAntraegeOrder() {
		TestDataUtil.persistNewGesuchInStatus(AntragStatus.ERSTE_MAHNUNG, persistence, gesuchService);
		TestDataUtil.persistNewGesuchInStatus(AntragStatus.VERFUEGT, persistence, gesuchService);

		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		final Gesuch gesuch3 = createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getSort().setPredicate("fallNummer");
		filterDTO.getSort().setReverse(true);     //aufsteigend
		//nach fallnummer geordnete liste
		final Pair<Long, List<Gesuch>> resultpair = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(5), resultpair.getLeft());
		List<Gesuch> foundGesuche = resultpair.getRight();
		Assert.assertEquals(gesuch.getId(), foundGesuche.get(2).getId());
		Assert.assertEquals(gesuch3.getId(), foundGesuche.get(4).getId());
		//genau anders rum ordnen
		filterDTO.getSort().setReverse(false); //absteigend
		Pair<Long, List<Gesuch>> resultpair2 = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(5), resultpair2.getLeft());
		List<Gesuch> foundGesucheReversed = resultpair2.getRight();
		Assert.assertEquals(gesuch3.getId(), foundGesucheReversed.get(0).getId());

	}

	@Test
	public void testSearchByFamilienname() {
		TestDataUtil.persistNewGesuchInStatus(AntragStatus.ERSTE_MAHNUNG, persistence, gesuchService);
		final Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> noFilterResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(4), noFilterResult.getLeft());

		filterDTO.getSearch().getPredicateObject().setFamilienName("Becker");
		//nach fallnummer geordnete liste
		Pair<Long, List<Gesuch>> resultpair = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(1), resultpair.getLeft());
		List<Gesuch> foundGesuche = resultpair.getRight();
		final Gesuch foundGesuch0 = foundGesuche.get(0);
		Assert.assertNotNull(foundGesuch0.getGesuchsteller1());
		Assert.assertNotNull(gesuch.getGesuchsteller1());
		Assert.assertEquals(gesuch.getGesuchsteller1().getGesuchstellerJA().getNachname(),
			foundGesuch0.getGesuchsteller1().getGesuchstellerJA().getNachname());

	}

	@Test
	public void testPaginationEdgeCases() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getPagination().setStart(0);
		filterDTO.getPagination().setNumber(10);
		//max 10 resultate davon 3 gefunden
		Pair<Long, List<Gesuch>> resultpair = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(3), resultpair.getLeft());
		Assert.assertEquals(3, resultpair.getRight().size());

		//max 0 resultate -> leere liste
		filterDTO.getPagination().setStart(0);
		filterDTO.getPagination().setNumber(0);
		Pair<Long, List<Gesuch>> noresult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(3), noresult.getLeft()); //wir erwarten 0 Resultate aber count 3
		Assert.assertEquals(0, noresult.getRight().size());

		filterDTO.getPagination().setStart(0);
		filterDTO.getPagination().setNumber(2);
		Pair<Long, List<Gesuch>> twopages = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(3), twopages.getLeft());
		Assert.assertEquals(2, twopages.getRight().size());

	}

	@Test
	public void testSearchByGesuchsperiode() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		Gesuchsperiode periode = TestDataUtil.createCustomGesuchsperiode(TestDataUtil.PERIODE_JAHR_1, TestDataUtil.PERIODE_JAHR_2);

		Gesuchsperiode nextPeriode = TestDataUtil.createCustomGesuchsperiode(TestDataUtil.PERIODE_JAHR_1 + 1, TestDataUtil.PERIODE_JAHR_2 + 1);
		nextPeriode = persistence.merge(nextPeriode);
		gesuch.setGesuchsperiode(nextPeriode);
		gesuch = persistence.merge(gesuch);
		Assert.assertEquals("gesuch fuer naechste Periode muss vorhanden sein", gesuch.getGesuchsperiode(), nextPeriode);
		gesuchService.findGesuch(gesuch.getId());

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Assert.assertEquals(TestDataUtil.PERIODE_JAHR_1 + "/" + TestDataUtil.PERIODE_JAHR_2, periode.getGesuchsperiodeString());
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString(periode.getGesuchsperiodeString());

		Pair<Long, List<Gesuch>> firstResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(2), firstResult.getLeft());

		Assert.assertEquals((TestDataUtil.PERIODE_JAHR_1 + 1) + "/" + (TestDataUtil.PERIODE_JAHR_2 + 1), nextPeriode.getGesuchsperiodeString());
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString(nextPeriode.getGesuchsperiodeString());

		Pair<Long, List<Gesuch>> result = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(1), result.getLeft());
		Assert.assertEquals(gesuch.getId(), result.getRight().get(0).getId());

		//search nach kurzem string
		filterDTO.getSearch().getPredicateObject().setGesuchsperiodeString((TestDataUtil.PERIODE_JAHR_1 + 1) + "/" + (TestDataUtil.PERIODE_JAHR_2 - 2000 + 1));
		Pair<Long, List<Gesuch>> thirdResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(1), thirdResult.getLeft());
		Assert.assertEquals(gesuch.getId(), thirdResult.getRight().get(0).getId());

	}

	@Test
	public void testSearchWithRoleGesuchsteller() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> firstResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(2), firstResult.getLeft());

		loginAsGesuchsteller("gesuchst");
		Pair<Long, List<Gesuch>> secondResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(0), secondResult.getLeft());
		Assert.assertEquals(0, secondResult.getRight().size());
	}

	@Test
	public void testSearchWithRoleSachbearbeiterInst() {
		loginAsAdmin();
		Gesuch gesDagmar = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25), AntragStatus.IN_BEARBEITUNG_GS);
		Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
		gesuch = persistence.merge(gesuch);

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> firstResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(1), firstResult.getLeft()); //Admin sieht Gesuch im Status IN_BEARBEITUNG_GS nicht, soll anscheinend so sein

		//Die Gesuche sollten im Status IN_BEARBEITUNG_GS sein und zu keinem oder einem Traegerschafts Sachbearbeiter gehoeren, trotzdem sollten wir sie finden
		//		Benutzer user = TestDataUtil.createDummySuperAdmin(persistence);
		//kita Weissenstein
		Institution institutionToSet = gesuch.extractAllBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution();
		loginAsSachbearbeiterInst("sainst", institutionToSet);
		Pair<Long, List<Gesuch>> secondResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(2), secondResult.getLeft());
		Assert.assertEquals(2, secondResult.getRight().size());

		gesuch.getFall().setVerantwortlicher(null);
		persistence.merge(gesuch);
		//traegerschaftbenutzer setzten
		Traegerschaft traegerschaft = institutionToSet.getTraegerschaft();
		Assert.assertNotNull("Unser testaufbau sieht vor, dass die institution zu einer traegerschaft gehoert", traegerschaft);
		Benutzer verantwortlicherUser = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "anonymous", traegerschaft, null, TestDataUtil.createDefaultMandant());
		gesDagmar.getFall().setVerantwortlicher(verantwortlicherUser);
		persistence.merge(gesDagmar);
		//es muessen immer noch beide gefunden werden da die betreuungen immer noch zu inst des users gehoeren
		Pair<Long, List<Gesuch>> thirdResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(2), thirdResult.getLeft());
		Assert.assertEquals(2, thirdResult.getRight().size());

		//aendere user zu einer anderen institution  -> darf nichts mehr finden
		Institution otherInst = TestDataUtil.createAndPersistDefaultInstitution(persistence);
		loginAsSachbearbeiterInst("sainst2", otherInst);

		Pair<Long, List<Gesuch>> fourthResult = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(0), fourthResult.getLeft());
		Assert.assertEquals(0, fourthResult.getRight().size());

	}

	@Test
	public void testGetPendenzenForSteueramtUser() {
		final Fall fall = fallService.saveFall(TestDataUtil.createDefaultFall());
		final Gesuchsperiode gesuchsperiode1516 = TestDataUtil.createCustomGesuchsperiode(2015, 2016);
		final Gesuchsperiode periodeToUpdate = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode1516);

		Gesuch gesuchVerfuegt = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.VERFUEGT);
		persistence.persist(gesuchVerfuegt);

		Gesuch gesuchSTV = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.PRUEFUNG_STV);
		persistence.persist(gesuchSTV);

		Gesuch gesuchGeprueftSTV = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.GEPRUEFT_STV);
		persistence.persist(gesuchGeprueftSTV);

		Gesuch gesuchBearbeitungSTV = TestDataUtil.createGesuch(fall, periodeToUpdate, AntragStatus.IN_BEARBEITUNG_STV);
		persistence.persist(gesuchBearbeitungSTV);

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		filterDTO.getSort().setPredicate("fallNummer");
		filterDTO.getSort().setReverse(true);

		loginAsSteueramt();
		final Pair<Long, List<Gesuch>> pendenzenSTV = searchService.searchAllAntraege(filterDTO);

		Assert.assertNotNull(pendenzenSTV);
		Assert.assertEquals(2, pendenzenSTV.getKey().intValue());
		for (Gesuch pendenz : pendenzenSTV.getValue()) {
			Assert.assertTrue(pendenz.getStatus() == AntragStatus.IN_BEARBEITUNG_STV
				|| pendenz.getStatus() == AntragStatus.PRUEFUNG_STV);
		}
	}

	@Test
	public void testSearchPendenzenJA() {
		TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		final Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		TestDataUtil.gesuchVerfuegen(gesuch, gesuchService);

		// es muss 2 Faelle geben
		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();
		Pair<Long, List<Gesuch>> allantraege = searchService.searchAllAntraege(filterDTO);
		Assert.assertEquals(Long.valueOf(2), allantraege.getLeft());

		// davon nur einer ist eine Pendenz
		Pair<Long, List<Gesuch>> pendenzen = searchService.searchPendenzen(filterDTO);
		Assert.assertEquals(Long.valueOf(1), pendenzen.getLeft());
		Assert.assertSame(AntragStatus.IN_BEARBEITUNG_JA, pendenzen.getValue().get(0).getStatus());
	}

	/**
	 * Ein Mischgesuch. Es muss mit Flag null eine Pendez fuers SCH sein und nachdem man dieses Flag setzt ist es keine Pendenz mehr
	 */
	@Test
	public void testSearchPendenzenMischgesuchFlagFinSit() {
		final Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		addVerantwortlicherSCHToFall(gesuch.getFall());

		loginAsAdminSchulamt();

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();

		Pair<Long, List<Gesuch>> pendenzen = searchService.searchPendenzen(filterDTO);
		Assert.assertEquals(Long.valueOf(1), pendenzen.getLeft());
		Assert.assertSame(AntragStatus.IN_BEARBEITUNG_JA, pendenzen.getValue().get(0).getStatus());

		gesuch.setFinSitStatus(FinSitStatus.ABGELEHNT);
		persistence.merge(gesuch);

		Pair<Long, List<Gesuch>> pendenzenSCH = searchService.searchPendenzen(filterDTO);
		Assert.assertEquals(Long.valueOf(0), pendenzenSCH.getLeft());

		loginAsSachbearbeiterJA();
		Pair<Long, List<Gesuch>> pendenzenJA = searchService.searchPendenzen(filterDTO);
		Assert.assertEquals(Long.valueOf(1), pendenzenJA.getLeft());
	}

	/**
	 * Ein SCHgesuch. Es muss eine Pendenz sein, bis es ABGESCHLOSSEN (NUR_SCHULAMT) ist.
	 */
	@Test
	public void testSearchPendenzenSCH() {
		final Gesuch gesuch = TestDataUtil.createAndPersistBeckerNoraGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		convertToSCHGesuch(gesuch);

		loginAsAdminSchulamt();

		AntragTableFilterDTO filterDTO = TestDataUtil.createAntragTableFilterDTO();

		Pair<Long, List<Gesuch>> pendenzen = searchService.searchPendenzen(filterDTO);
		Assert.assertEquals(Long.valueOf(1), pendenzen.getLeft());
		Assert.assertSame(AntragStatus.IN_BEARBEITUNG_JA, pendenzen.getValue().get(0).getStatus());

		gesuch.setFinSitStatus(FinSitStatus.AKZEPTIERT);
		gesuch.setStatus(AntragStatus.NUR_SCHULAMT);
		persistence.merge(gesuch);

		Pair<Long, List<Gesuch>> pendenzenEmpty = searchService.searchPendenzen(filterDTO);
		Assert.assertEquals(Long.valueOf(0), pendenzenEmpty.getLeft());
	}

	private void convertToSCHGesuch(Gesuch gesuch) {
		final Fall fall = addVerantwortlicherSCHToFall(gesuch.getFall());
		fall.setVerantwortlicher(null);
		persistence.merge(fall);
	}

	private Fall addVerantwortlicherSCHToFall(@Nonnull Fall fall) {
		// mit 2 Verantwortlichen wird zu Mischgesuch
		Benutzer verantSCH = TestDataUtil.createBenutzerSCH();
		persistence.persist(verantSCH.getMandant());
		persistence.persist(verantSCH);
		fall.setVerantwortlicherSCH(verantSCH);
		return persistence.merge(fall);
	}

}
