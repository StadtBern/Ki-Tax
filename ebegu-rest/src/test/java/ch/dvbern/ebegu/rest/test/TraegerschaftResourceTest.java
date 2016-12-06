package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.client.JaxOpenIdmResponse;
import ch.dvbern.ebegu.api.client.JaxOpenIdmResult;
import ch.dvbern.ebegu.api.client.OpenIdmRestClient;
import ch.dvbern.ebegu.api.resource.TraegerschaftResource;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.services.TraegerschaftService;
import ch.dvbern.ebegu.services.TraegerschaftServiceBean;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import junit.framework.AssertionFailedError;
import org.easymock.EasyMock;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Testet TraegerschftResource
 */

public class TraegerschaftResourceTest {

	private static final UriInfo RESTEASY_URI_INFO = new ResteasyUriInfo("test", "test", "test");

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	private TraegerschaftResource traegerschaftResource;

	@Inject
	private TraegerschaftService traegerschaftService = new TraegerschaftServiceBean();

	@Inject
	private OpenIdmRestClient openIdmRestClient;

	@Inject
	private EasyMockProvider mockProvider;

	@Before
	public void mockup() {

	}

	@Test
	public void synchronizeTraegerschaftEmptyTest() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		List<Traegerschaft> traegerschafts = new ArrayList<>();

		EasyMock.expect(openIdmRestClient.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(traegerschaftService.getAllActiveTraegerschaften()).andReturn((traegerschafts));

		EasyMock.expect(openIdmRestClient.deleteTraegerschaft(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestClient.createTraegerschaft(EasyMock.anyObject(Traegerschaft.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();


		mockProvider.replayAll();

		traegerschaftResource.synchronizeTraegerschaft(true);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeTraegerschaftNullTest1() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		List<Traegerschaft> traegerschafts = new ArrayList<>();

		EasyMock.expect(openIdmRestClient.getAll()).andReturn(Optional.empty());
		EasyMock.expect(traegerschaftService.getAllActiveTraegerschaften()).andReturn((traegerschafts));

		EasyMock.expect(openIdmRestClient.deleteTraegerschaft(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestClient.createTraegerschaft(EasyMock.anyObject(Traegerschaft.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		traegerschaftResource.synchronizeTraegerschaft(true);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeTraegerschaftNullTest2() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		List<Traegerschaft> traegerschafts = null;

		EasyMock.expect(openIdmRestClient.getAll()).andReturn(Optional.empty());
		EasyMock.expect(traegerschaftService.getAllActiveTraegerschaften()).andReturn((traegerschafts));

		EasyMock.expect(openIdmRestClient.deleteTraegerschaft(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestClient.createTraegerschaft(EasyMock.anyObject(Traegerschaft.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		traegerschaftResource.synchronizeTraegerschaft(true);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeTraegerschaft_TwoExist_TwoCreate_Test() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		final JaxOpenIdmResult jaxOpenIdmResult = creatOpenIdmTraegerschaft("T-1");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResult);
		final JaxOpenIdmResult jaxOpenIdmResul2 = creatOpenIdmTraegerschaft("T-2");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul2);
		final JaxOpenIdmResult jaxOpenIdmResul3 = creatOpenIdmInst("I-3"); // Institution
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul3);
		final JaxOpenIdmResult jaxOpenIdmResul4 = creatOpenIdmInst("I-4"); // Institution
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul4);


		List<Traegerschaft> traegerschafts = new ArrayList<>();
		final Traegerschaft traegerschaft1 = creatInst("1");
		traegerschafts.add(traegerschaft1);
		final Traegerschaft traegerschaft2 = creatInst("2");
		traegerschafts.add(traegerschaft2);
		final Traegerschaft traegerschaft3 = creatInst("3");
		traegerschafts.add(traegerschaft3);
		final Traegerschaft traegerschaft4 = creatInst("4");
		traegerschafts.add(traegerschaft4);

		EasyMock.expect(openIdmRestClient.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(traegerschaftService.getAllActiveTraegerschaften()).andReturn((traegerschafts));

		EasyMock.expect(openIdmRestClient.createTraegerschaft(traegerschaft3)).andReturn(Optional.of(creatOpenIdmTraegerschaft("3")));
		EasyMock.expect(openIdmRestClient.createTraegerschaft(traegerschaft4)).andReturn(Optional.of(creatOpenIdmTraegerschaft("4")));

		EasyMock.expect(openIdmRestClient.deleteTraegerschaft(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();

		mockProvider.replayAll();

		final StringBuilder stringBuilder = traegerschaftResource.synchronizeTraegerschaft(true);
		System.out.println(stringBuilder);

		mockProvider.verifyAll();
	}


	@Test
	public void synchronizeTraegerschaft_AllExist_Test() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		final JaxOpenIdmResult jaxOpenIdmResult = creatOpenIdmTraegerschaft("T-1");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResult);
		final JaxOpenIdmResult jaxOpenIdmResul2 = creatOpenIdmTraegerschaft("T-2");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul2);


		List<Traegerschaft> traegerschafts = new ArrayList<>();
		final Traegerschaft traegerschaft1 = creatInst("1");
		traegerschafts.add(traegerschaft1);
		final Traegerschaft traegerschaft2 = creatInst("2");
		traegerschafts.add(traegerschaft2);

		EasyMock.expect(openIdmRestClient.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(traegerschaftService.getAllActiveTraegerschaften()).andReturn((traegerschafts));

		EasyMock.expect(openIdmRestClient.deleteTraegerschaft(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestClient.createTraegerschaft(EasyMock.anyObject(Traegerschaft.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		final StringBuilder stringBuilder = traegerschaftResource.synchronizeTraegerschaft(true);
		System.out.println(stringBuilder);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeTraegerschaft_TwoExist_TwoDelete_Test() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		final JaxOpenIdmResult jaxOpenIdmResult = creatOpenIdmTraegerschaft("T-1");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResult);
		final JaxOpenIdmResult jaxOpenIdmResul2 = creatOpenIdmTraegerschaft("T-2");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul2);
		final JaxOpenIdmResult jaxOpenIdmResul3 = creatOpenIdmTraegerschaft("T-3"); // Traegerschaft
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul3);
		final JaxOpenIdmResult jaxOpenIdmResul4 = creatOpenIdmTraegerschaft("T-4"); // Traegerschaft
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul4);


		List<Traegerschaft> traegerschafts = new ArrayList<>();
		final Traegerschaft traegerschaft1 = creatInst("1");
		traegerschafts.add(traegerschaft1);
		final Traegerschaft traegerschaft2 = creatInst("2");
		traegerschafts.add(traegerschaft2);

		EasyMock.expect(openIdmRestClient.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(traegerschaftService.getAllActiveTraegerschaften()).andReturn((traegerschafts));

		EasyMock.expect(openIdmRestClient.deleteTraegerschaft("3")).andReturn(true);
		EasyMock.expect(openIdmRestClient.deleteTraegerschaft("4")).andReturn(true);

		EasyMock.expect(openIdmRestClient.createTraegerschaft(EasyMock.anyObject(Traegerschaft.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		final StringBuilder stringBuilder = traegerschaftResource.synchronizeTraegerschaft(true);
		System.out.println(stringBuilder);

		mockProvider.verifyAll();
	}

	private JaxOpenIdmResult creatOpenIdmInst(String name) {
		JaxOpenIdmResult jaxOpenIdmResult = new JaxOpenIdmResult();
		jaxOpenIdmResult.set_id(name);
		jaxOpenIdmResult.setName(name);
		jaxOpenIdmResult.setMail(name + "@" + name + ".ch");
		jaxOpenIdmResult.setType("traegerschaft");
		return jaxOpenIdmResult;
	}

	private JaxOpenIdmResult creatOpenIdmTraegerschaft(String name) {
		JaxOpenIdmResult jaxOpenIdmResult = new JaxOpenIdmResult();
		jaxOpenIdmResult.set_id(name);
		jaxOpenIdmResult.setName(name);
		jaxOpenIdmResult.setMail(name + "@" + name + ".ch");
		jaxOpenIdmResult.setType("sponsor");
		return jaxOpenIdmResult;
	}

	private Traegerschaft creatInst(String name) {
		Traegerschaft traegerschaft = new Traegerschaft();
		traegerschaft.setId(name);
		traegerschaft.setName(name);
		return traegerschaft;
	}

	private Traegerschaft creatTrae(String name) {
		Traegerschaft traegerschaft = new Traegerschaft();
		traegerschaft.setId(name);
		traegerschaft.setName(name);
		return traegerschaft;
	}


}
