package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
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

@Ignore
public class NeedleTest {



    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @ObjectUnderTest
	private JaxBConverter converter;

	@InjectIntoMany
	@ObjectUnderTest
	private final Persistence<Adresse> persistence = new PersistenceService<Adresse>();

	@InjectIntoMany
	@ObjectUnderTest
	private final CriteriaQueryHelper criteriaQueryHelper = new CriteriaQueryHelper();

	@InjectIntoMany
	@ObjectUnderTest
	private final AdresseService leistungsrechnungService = new AdresseServiceBean();

    @Test
    public void testConverter() throws Exception {
		JaxAdresse adr = TestJaxDataUtil.createTestJaxAdr(null);
				adr.setGueltigAb(null);
				adr.setGueltigBis(null);
				Adresse adrEntity = converter.adresseToEntity(adr, new GesuchstellerAdresse());
				Assert.assertEquals(Constants.START_OF_TIME, adrEntity.getGueltigkeit().getGueltigAb());
				Assert.assertEquals(Constants.END_OF_TIME,adrEntity.getGueltigkeit().getGueltigBis());
    }


	/**
	 * Testet das Umzugadresse konvertiert wird
	 */
	@Test
	public void convertJaxGesuchstellerWithUmzgTest(){
		JaxGesuchsteller gesuchstellerWith3Adr = TestJaxDataUtil.createTestJaxGesuchstellerWithUmzug();
		Gesuchsteller gesuchsteller = converter.gesuchstellerToEntity(gesuchstellerWith3Adr, new Gesuchsteller());
		Assert.assertEquals(gesuchstellerWith3Adr.getGeburtsdatum(), gesuchsteller.getGeburtsdatum());
		Assert.assertEquals(gesuchstellerWith3Adr.getVorname(), gesuchsteller.getVorname());
		Assert.assertEquals(gesuchstellerWith3Adr.getNachname(), gesuchsteller.getNachname());
		//id wird serverseitig gesetzt
		Assert.assertNull(gesuchstellerWith3Adr.getId());
		Assert.assertNotNull(gesuchsteller.getId());
		Assert.assertEquals(3, gesuchsteller.getAdressen().size());
		ImmutableListMultimap<AdresseTyp, GesuchstellerAdresse> adrByTyp = Multimaps.index(gesuchsteller.getAdressen(), GesuchstellerAdresse::getAdresseTyp);
		Adresse altAdr = adrByTyp.get(AdresseTyp.KORRESPONDENZADRESSE).get(0);
		Assert.assertTrue(altAdr.isSame(converter.adresseToEntity(gesuchstellerWith3Adr.getAlternativeAdresse(), new GesuchstellerAdresse())));

	}

}
