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

package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Testet BetreuungResource
 */

public class DokumenteUtilTest {


	@Test
	public void testAllPersistedInNeeded() {
		Set<DokumentGrund> dokumentGrundsNeeded = new HashSet<>();
		final Gesuch gesuch = new Gesuch();

		Collection<DokumentGrund> persistedDokumentGrunds = new HashSet<>();

		createGrundNeeded(dokumentGrundsNeeded, DokumentGrundTyp.FAMILIENSITUATION, DokumentTyp.JAHRESLOHNAUSWEISE);
		createGrundNeeded(dokumentGrundsNeeded, DokumentGrundTyp.FAMILIENSITUATION, DokumentTyp.STEUERERKLAERUNG);
		createGrundNeeded(dokumentGrundsNeeded, DokumentGrundTyp.ERWERBSPENSUM, DokumentTyp.NACHWEIS_AUSBILDUNG);

		createGrundPersisted(persistedDokumentGrunds, DokumentGrundTyp.FAMILIENSITUATION, DokumentTyp.JAHRESLOHNAUSWEISE, 3);

		final Set<DokumentGrund> mergeNeededAndPersisted = DokumenteUtil.mergeNeededAndPersisted(dokumentGrundsNeeded, persistedDokumentGrunds, gesuch);

		Set<DokumentGrund> mergedFamsit = getByGrundTyp(mergeNeededAndPersisted, DokumentGrundTyp.FAMILIENSITUATION);

		Assert.assertNotNull(mergedFamsit);
		Assert.assertEquals(mergedFamsit.size(), 2);

		Assert.assertEquals(3, getByDokumentType(mergedFamsit, DokumentTyp.JAHRESLOHNAUSWEISE).size());
		Assert.assertEquals(1, getByDokumentType(mergedFamsit, DokumentTyp.STEUERERKLAERUNG).size());


		Set<DokumentGrund> mergedERWERBSPENSUM = getByGrundTyp(mergeNeededAndPersisted, DokumentGrundTyp.ERWERBSPENSUM);
		Assert.assertNotNull(mergedERWERBSPENSUM);
		Assert.assertEquals(mergedERWERBSPENSUM.size(), 1);
		Assert.assertEquals(1, getByDokumentType(mergedERWERBSPENSUM, DokumentTyp.NACHWEIS_AUSBILDUNG).size());
	}

	@Test
	public void testGetFileNameForGeneratedDokumentTypBEGLEITSCHREIBEN() {
		Assert.assertEquals("Deckblatt_16.000001.pdf", DokumenteUtil
			.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.BEGLEITSCHREIBEN, "16.000001"));
	}

	@Test
	public void testGetFileNameForGeneratedDokumentTypFINANZIELLE_SITUATION() {
		Assert.assertEquals("Finanzielle_Situation_16.000001.pdf", DokumenteUtil
			.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.FINANZIELLE_SITUATION, "16.000001"));
	}

	@Test
	public void testGetFileNameForGeneratedDokumentTypVERFUEGUNG_KITA() {
		Assert.assertEquals("Verfuegung_16.000001.1.1.pdf", DokumenteUtil
			.getFileNameForGeneratedDokumentTyp(GeneratedDokumentTyp.VERFUEGUNG, "16.000001.1.1"));
	}

	@Test
	public void testCompareDokumentGrundOldVersionDifferentNames() {
		DokumentGrund persistedDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.FREETEXT, null, DokumentTyp.BESTAETIGUNG_ARZT);
		persistedDok.setFullName("Fulgencio Gonzalez");
		DokumentGrund neededDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		Gesuch gesuch = mockGesuch();
		int result = DokumenteUtil.compareDokumentGrunds(persistedDok, neededDok, gesuch);
		Assert.assertNotEquals(0, result);
	}

	@Test
	public void testCompareDokumentGrundOldVersionSameDoks() {
		DokumentGrund persistedDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.FREETEXT, null, DokumentTyp.BESTAETIGUNG_ARZT);
		persistedDok.setFullName("Avioncito Rambert");
		DokumentGrund neededDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		Gesuch gesuch = mockGesuch();
		int result = DokumenteUtil.compareDokumentGrunds(persistedDok, neededDok, gesuch);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareDokumentGrundNewVersionSameDoks() {
		DokumentGrund persistedDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		DokumentGrund neededDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		int result = DokumenteUtil.compareDokumentGrunds(persistedDok, neededDok, null);
		Assert.assertEquals(0, result);
	}

	@Test
	public void testCompareDokumentGrundNewVersionDifferentPersonNumber() {
		DokumentGrund persistedDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		DokumentGrund neededDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 2, DokumentTyp.BESTAETIGUNG_ARZT);
		int result = DokumenteUtil.compareDokumentGrunds(persistedDok, neededDok, null);
		Assert.assertNotEquals(0, result);
	}

	@Test
	public void testCompareDokumentGrundNewVersionDifferentPersonTypes() {
		DokumentGrund persistedDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		DokumentGrund neededDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.KIND, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		int result = DokumenteUtil.compareDokumentGrunds(persistedDok, neededDok, null);
		Assert.assertNotEquals(0, result);
	}

	@Test
	public void testCompareDokumentGrundNewVersionDifferentDokumentGrundTypes() {
		DokumentGrund persistedDok = new DokumentGrund(DokumentGrundTyp.FINANZIELLESITUATION, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		DokumentGrund neededDok = new DokumentGrund(DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG, "tag",
			DokumentGrundPersonType.GESUCHSTELLER, 1, DokumentTyp.BESTAETIGUNG_ARZT);
		int result = DokumenteUtil.compareDokumentGrunds(persistedDok, neededDok, null);
		Assert.assertNotEquals(0, result);
	}


	@Nonnull
	private Gesuch mockGesuch() {
		Gesuch gesuch = new Gesuch();
		GesuchstellerContainer gsContainer = new GesuchstellerContainer();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setVorname("Avioncito");
		gesuchsteller.setNachname("Rambert");
		gsContainer.setGesuchstellerJA(gesuchsteller);
		gesuch.setGesuchsteller1(gsContainer);
		return gesuch;
	}

	private Set<Dokument> getByDokumentType(Set<DokumentGrund> dokumentGrunds, DokumentTyp dokumentTyp) {
		Set<Dokument> dokumente = new HashSet<>();

		for (DokumentGrund dokumentGrund : dokumentGrunds) {
			if (dokumentGrund.getDokumentTyp().equals(dokumentTyp)) {
				dokumente.addAll(dokumentGrund.getDokumente());
			}
		}
		return dokumente;

	}

	private Set<DokumentGrund> getByGrundTyp(Set<DokumentGrund> dokumentGrundsNeeded, DokumentGrundTyp dokumentGrundTyp) {

		Set<DokumentGrund> dokumentGrundsNeededMerged = new HashSet<>();
		for (DokumentGrund dokumentGrund : dokumentGrundsNeeded) {
			if (dokumentGrund.getDokumentGrundTyp().equals(dokumentGrundTyp)) {
				dokumentGrundsNeededMerged.add(dokumentGrund);
			}
		}
		return dokumentGrundsNeededMerged;
	}


	private void createGrundNeeded(Set<DokumentGrund> dokumentGrundsNeeded, DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp) {

		DokumentGrund dokumentGrund = new DokumentGrund();
		dokumentGrund.setDokumentGrundTyp(dokumentGrundTyp);
		dokumentGrund.setDokumentTyp(dokumentTyp);

		Dokument dokument = new Dokument();
		dokumentGrund.getDokumente().add(dokument);

		dokumentGrundsNeeded.add(dokumentGrund);
	}

	private void createGrundPersisted(Collection<DokumentGrund> dokumentGrunds, DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp, int number) {

		DokumentGrund dokumentGrund = new DokumentGrund();
		dokumentGrund.setDokumentGrundTyp(dokumentGrundTyp);
		dokumentGrund.setDokumentTyp(dokumentTyp);

		for (int i = 1; i < number; i++) {
			Dokument dokument = new Dokument();
			dokument.setFilename(i + " ");
			dokumentGrund.getDokumente().add(dokument);
		}
		Dokument dokument = new Dokument();
		dokumentGrund.getDokumente().add(dokument);

		dokumentGrunds.add(dokumentGrund);

	}

}
