package ch.dvbern.ebegu.rest.test.client;

import ch.dvbern.ebegu.api.client.JaxOpenIdmResponse;
import ch.dvbern.ebegu.api.client.JaxOpenIdmResult;
import ch.dvbern.ebegu.api.client.OpenIdmRestClient;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.rest.test.AbstractEbeguRestTest;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


import javax.inject.Inject;
import java.util.Optional;

/**
 * Testet die Verbindung zum OpenIdm Server.
 * Tests sind default ignored, da wir zum Testen unserer Applikation nicht auf den OpenIdm Server angewiesen sind
 *
 * To run these test set getOpenIdmEnabled true in {@link ch.dvbern.ebegu.config.ch.dvbern.ebegu.config.EbeguConfigurationDummy}
 */

@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class OpenIdmRestClentTest extends AbstractEbeguRestTest {


	public static final String instId1 = "b4dc7d30-b63e-4176-a166-00deadbeef01";
	public static final String instId2 = "b4dc7d30-b63e-4176-a166-00deadbeef02";
	public static final String traegerschaftId1 = "b4dc7d30-b63e-4176-a166-00deadbeef01";

	@Inject
	private OpenIdmRestClient openIdmRestClient;

	@Test
	@Ignore
	public void testOpenIdmInterface() {
//		final Response response = openIdmRestClient.login();
//		Assert.assertEquals(response.getStatus(), 200);

		Institution institution1 = new Institution();
		institution1.setName("institution1");
		institution1.setId(instId1);
		final Optional<JaxOpenIdmResult> optInstitution1 = openIdmRestClient.createInstitution(institution1);
		Assert.assertTrue(optInstitution1.isPresent());

		Institution institution2 = new Institution();
		institution2.setName("institution2");
		institution2.setId(instId2);
		final Optional<JaxOpenIdmResult> optInstitution2 = openIdmRestClient.createInstitution(institution2);
		Assert.assertTrue(optInstitution2.isPresent());

		Traegerschaft traegerschaft = new Traegerschaft();
		traegerschaft.setName("traegerschaft1");
		traegerschaft.setId(traegerschaftId1);
		final Optional<JaxOpenIdmResult> optTaegerschaft = openIdmRestClient.createTraegerschaft(traegerschaft);
		Assert.assertTrue(optTaegerschaft.isPresent());

		final Optional<JaxOpenIdmResult> institutionByUid1 = openIdmRestClient.getInstitutionByUid(institution1.getId());
		Assert.assertTrue(institutionByUid1.isPresent());
		Assert.assertEquals(institution1.getId(), institutionByUid1.get().get_id());
		Assert.assertEquals(institution1.getName(), institutionByUid1.get().getName());

		final Optional<JaxOpenIdmResult> institutionByUid2 = openIdmRestClient.getInstitutionByUid(institution2.getId());
		Assert.assertTrue(institutionByUid2.isPresent());
		Assert.assertEquals(institution2.getId(), institutionByUid2.get().get_id());
		Assert.assertEquals(institution2.getName(), institutionByUid2.get().getName());

		final Optional<JaxOpenIdmResult> traegerschaftByUid = openIdmRestClient.getInstitutionByUid(traegerschaft.getId());
		Assert.assertTrue(traegerschaftByUid.isPresent());
		Assert.assertEquals(traegerschaft.getId(), traegerschaftByUid.get().get_id());
		Assert.assertEquals(traegerschaft.getName(), traegerschaftByUid.get().getName());


	}

	@Test
	@Ignore
	public void delete() {
		boolean sucess = openIdmRestClient.delete(instId1);
		Assert.assertTrue(sucess);

		sucess = openIdmRestClient.delete(instId2);
		Assert.assertTrue(sucess);


		sucess = openIdmRestClient.delete(traegerschaftId1);
		Assert.assertTrue(sucess);
	}

	@Test
	@Ignore
	public void getAll() {
		final Optional<JaxOpenIdmResponse> all = openIdmRestClient.getAll();
		Assert.assertTrue(all.isPresent());

		for (JaxOpenIdmResult jaxOpenIdmResult : all.get().getResult()) {
			System.out.println(jaxOpenIdmResult.get_id());
			System.out.println(jaxOpenIdmResult.getName());
			System.out.println(jaxOpenIdmResult.getType());
			System.out.println(jaxOpenIdmResult.getMail());
			System.out.println();
		}

	}

}
