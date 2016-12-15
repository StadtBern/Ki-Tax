package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresseContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.persistence.PersistenceService;
import ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil;
import ch.dvbern.ebegu.services.AdresseService;
import ch.dvbern.ebegu.services.AdresseServiceBean;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

@Ignore //im moment haben wir glaube ich noch keinen schlauen use case fuer needle tests. Kitadmin benutzt das so,
// dass die businessmethoden innerhalb eines services damit gestestet werden
public class NeedleTest {


    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @ObjectUnderTest
	private JaxBConverter converter;

	@InjectIntoMany
	@ObjectUnderTest
	private final Persistence<Adresse> persistence = new PersistenceService<>();

	@InjectIntoMany
	@ObjectUnderTest
	private final CriteriaQueryHelper criteriaQueryHelper = new CriteriaQueryHelper();

	@InjectIntoMany
	@ObjectUnderTest
	private final AdresseService leistungsrechnungService = new AdresseServiceBean();

    @Test
    public void testConverter() throws Exception {
		JaxAdresseContainer adr = TestJaxDataUtil.createTestJaxAdr(null);
				adr.getAdresseJA().setGueltigAb(null);
				adr.getAdresseJA().setGueltigBis(null);
				GesuchstellerAdresseContainer adrEntity = converter.adresseContainerToEntity(adr, new GesuchstellerAdresseContainer());
				Assert.assertEquals(Constants.START_OF_TIME, adrEntity.extractGueltigkeit().getGueltigAb());
				Assert.assertEquals(Constants.END_OF_TIME,adrEntity.extractGueltigkeit().getGueltigBis());
    }

	/**
	 * Testet das Umzugadresse konvertiert wird
	 */
	@Test
	public void convertJaxGesuchstellerWithUmzgTest(){
		JaxGesuchstellerContainer gesuchstellerWith3Adr = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		GesuchstellerContainer gesuchsteller = converter.gesuchstellerContainerToEntity(gesuchstellerWith3Adr, new GesuchstellerContainer());
		Assert.assertEquals(gesuchstellerWith3Adr.getGesuchstellerJA().getGeburtsdatum(), gesuchsteller.getGesuchstellerJA().getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWith3Adr.getGesuchstellerJA().getVorname(), gesuchsteller.getGesuchstellerJA().getVorname());
		Assert.assertEquals(gesuchstellerWith3Adr.getGesuchstellerJA().getNachname(), gesuchsteller.getGesuchstellerJA().getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWith3Adr.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(3, gesuchsteller.getAdressen().size());
		ImmutableListMultimap<AdresseTyp, GesuchstellerAdresseContainer> adrByTyp = Multimaps
			.index(gesuchsteller.getAdressen(), GesuchstellerAdresseContainer::extractAdresseTyp);
		GesuchstellerAdresseContainer altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
		Assert.assertTrue(altAdr.getGesuchstellerAdresseJA().isSame(converter
			.adresseContainerToEntity(gesuchstellerWith3Adr.getAlternativeAdresse(), new GesuchstellerAdresseContainer()).getGesuchstellerAdresseJA()));
	}
}
