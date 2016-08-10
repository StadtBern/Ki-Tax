package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.resource.*;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Testet BetreuungResource
 */

public class DokumenteResourceTest extends AbstractEbeguRestTest {

	private DokumenteResource dokumenteResource;

	@Before
	public void init(){
		dokumenteResource = new DokumenteResource();
	}

	@Test
	public void testAllPersistedInNeeded()  {
		Set<DokumentGrund> dokumentGrundsNeeded = new HashSet<DokumentGrund>();

		Collection<DokumentGrund> persistedDokumentGrunds = new HashSet<DokumentGrund>();

		createGrundNeeded(dokumentGrundsNeeded, DokumentGrundTyp.FAMILIENSITUATION, DokumentTyp.JAHRESLOHNAUSWEISE);
		createGrundNeeded(dokumentGrundsNeeded, DokumentGrundTyp.FAMILIENSITUATION, DokumentTyp.STEUERERKLAERUNG);
		createGrundNeeded(dokumentGrundsNeeded, DokumentGrundTyp.ERWERBSPENSUM, DokumentTyp.NACHWEIS_AUSBILDUNG);

		createGrundPersisted(persistedDokumentGrunds, DokumentGrundTyp.FAMILIENSITUATION, DokumentTyp.JAHRESLOHNAUSWEISE, 3);

		final Set<DokumentGrund> mergeNeededAndPersisted = dokumenteResource.mergeNeededAndPersisted(dokumentGrundsNeeded, persistedDokumentGrunds);

		Set<DokumentGrund> mergedFamsit = getByGrundTyp(mergeNeededAndPersisted, DokumentGrundTyp.FAMILIENSITUATION);

		Assert.assertNotNull(mergedFamsit);
		Assert.assertEquals(mergedFamsit.size(), 2);

		Assert.assertEquals(3,getByDokumentType(mergedFamsit, DokumentTyp.JAHRESLOHNAUSWEISE).size());
		Assert.assertEquals(1,getByDokumentType(mergedFamsit, DokumentTyp.STEUERERKLAERUNG).size());


		Set<DokumentGrund> mergedERWERBSPENSUM = getByGrundTyp(mergeNeededAndPersisted, DokumentGrundTyp.ERWERBSPENSUM);
		Assert.assertNotNull(mergedERWERBSPENSUM);
		Assert.assertEquals(mergedERWERBSPENSUM.size(), 1);
		Assert.assertEquals(1,getByDokumentType(mergedERWERBSPENSUM, DokumentTyp.NACHWEIS_AUSBILDUNG).size());


	}

	private Set<Dokument> getByDokumentType(Set<DokumentGrund> dokumentGrunds, DokumentTyp dokumentTyp) {
		Set<Dokument> dokumente = new HashSet<Dokument>();

		for (DokumentGrund dokumentGrund : dokumentGrunds) {
			for (Dokument dokument : dokumentGrund.getDokumente()) {
				if(dokument.getDokumentTyp().equals(dokumentTyp)){
					dokumente.add(dokument);
				}

			}
		}
		return dokumente;

	}

	private Set<DokumentGrund> getByGrundTyp(Set<DokumentGrund> dokumentGrundsNeeded, DokumentGrundTyp dokumentGrundTyp) {

		Set<DokumentGrund> dokumentGrundsNeededMerged = new HashSet<DokumentGrund>();
		for (DokumentGrund dokumentGrund : dokumentGrundsNeeded) {
			if(dokumentGrund.getDokumentGrundTyp().equals(dokumentGrundTyp)){
				dokumentGrundsNeededMerged.add(dokumentGrund);
			}
		}
		return dokumentGrundsNeededMerged;
	}


	private void createGrundNeeded(Set<DokumentGrund> dokumentGrundsNeeded, DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp) {

		DokumentGrund dokumentGrund = new DokumentGrund();
		dokumentGrund.setDokumentGrundTyp(dokumentGrundTyp);

		Dokument dokument = new Dokument();
		dokument.setDokumentTyp(dokumentTyp);
		dokumentGrund.getDokumente().add(dokument);

		dokumentGrundsNeeded.add(dokumentGrund);
	}

	private void createGrundPersisted(Collection<DokumentGrund> dokumentGrunds, DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp, int number) {

		DokumentGrund dokumentGrund = new DokumentGrund();
		dokumentGrund.setDokumentGrundTyp(dokumentGrundTyp);

		for (int i = 1; i < number; i++) {
			Dokument dokument = new Dokument();
			dokument.setDokumentTyp(dokumentTyp);
			dokument.setDokumentName(i + " ");
			dokumentGrund.getDokumente().add(dokument);
		}
		Dokument dokument = new Dokument();
		dokument.setDokumentTyp(dokumentTyp);
		dokumentGrund.getDokumente().add(dokument);

		dokumentGrunds.add(dokumentGrund);

	}

}
