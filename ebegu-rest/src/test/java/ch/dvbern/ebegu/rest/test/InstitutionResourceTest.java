package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.client.JaxOpenIdmResponse;
import ch.dvbern.ebegu.api.client.JaxOpenIdmResult;
import ch.dvbern.ebegu.api.client.OpenIdmRestService;
import ch.dvbern.ebegu.api.resource.InstitutionResource;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.InstitutionServiceBean;
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

import static ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil.creatOpenIdmInst;
import static ch.dvbern.ebegu.rest.test.util.TestJaxDataUtil.creatOpenIdmTraegerschaft;

/**
 * Testet InstitutionResource mit Needle
 */

public class InstitutionResourceTest {

	private static final UriInfo RESTEASY_URI_INFO = new ResteasyUriInfo("test", "test", "test");

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	private InstitutionResource institutionResource;

	@Inject
	private InstitutionService institutionService = new InstitutionServiceBean();

	@Inject
	private OpenIdmRestService openIdmRestService;

	@Inject
	private EasyMockProvider mockProvider;

	@Before
	public void mockup() {

	}

	@Test
	public void synchronizeInstitutionEmptyTest() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		List<Institution> institutions = new ArrayList<>();

		EasyMock.expect(openIdmRestService.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(institutionService.getAllActiveInstitutionen()).andReturn((institutions));

		EasyMock.expect(openIdmRestService.deleteInstitution(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestService.createInstitution(EasyMock.anyObject(Institution.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();


		mockProvider.replayAll();

		institutionResource.synchronizeInstitutions(true);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeInstitutionNullTest1() {

		List<Institution> institutions = new ArrayList<>();

		EasyMock.expect(openIdmRestService.getAll()).andReturn(Optional.empty());
		EasyMock.expect(institutionService.getAllActiveInstitutionen()).andReturn((institutions));

		EasyMock.expect(openIdmRestService.deleteInstitution(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestService.createInstitution(EasyMock.anyObject(Institution.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		institutionResource.synchronizeInstitutions(true);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeInstitutionNullTest2() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		List<Institution> institutions = null;

		EasyMock.expect(openIdmRestService.getAll()).andReturn(Optional.empty());
		EasyMock.expect(institutionService.getAllActiveInstitutionen()).andReturn((institutions));

		EasyMock.expect(openIdmRestService.deleteInstitution(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestService.createInstitution(EasyMock.anyObject(Institution.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		institutionResource.synchronizeInstitutions(true);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeInstitution_TwoExist_TwoCreate_Test() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		final JaxOpenIdmResult jaxOpenIdmResult = creatOpenIdmInst("I-1");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResult);
		final JaxOpenIdmResult jaxOpenIdmResul2 = creatOpenIdmInst("I-2");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul2);
		final JaxOpenIdmResult jaxOpenIdmResul3 = creatOpenIdmTraegerschaft("T-3"); // Traegerschaft
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul3);
		final JaxOpenIdmResult jaxOpenIdmResul4 = creatOpenIdmTraegerschaft("T-4"); // Traegerschaft
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul4);


		List<Institution> institutions = new ArrayList<>();
		final Institution institution1 = creatInst("1");
		institutions.add(institution1);
		final Institution institution2 = creatInst("2");
		institutions.add(institution2);
		final Institution institution3 = creatInst("3");
		institutions.add(institution3);
		final Institution institution4 = creatInst("4");
		institutions.add(institution4);

		EasyMock.expect(openIdmRestService.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(institutionService.getAllActiveInstitutionen()).andReturn((institutions));

		EasyMock.expect(openIdmRestService.createInstitution(institution3)).andReturn(Optional.of(creatOpenIdmInst("3")));
		EasyMock.expect(openIdmRestService.createInstitution(institution4)).andReturn(Optional.of(creatOpenIdmInst("4")));

		EasyMock.expect(openIdmRestService.deleteInstitution(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();

		mockProvider.replayAll();

		final StringBuilder stringBuilder = institutionResource.synchronizeInstitutions(true);
		System.out.println(stringBuilder);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeInstitution_AllExist_Test() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		final JaxOpenIdmResult jaxOpenIdmResult = creatOpenIdmInst("I-1");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResult);
		final JaxOpenIdmResult jaxOpenIdmResul2 = creatOpenIdmInst("I-2");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul2);


		List<Institution> institutions = new ArrayList<>();
		final Institution institution1 = creatInst("1");
		institutions.add(institution1);
		final Institution institution2 = creatInst("2");
		institutions.add(institution2);

		EasyMock.expect(openIdmRestService.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(institutionService.getAllActiveInstitutionen()).andReturn((institutions));

		EasyMock.expect(openIdmRestService.deleteInstitution(EasyMock.anyObject(String.class))).andThrow(new AssertionFailedError("Nothing to delete!")).anyTimes();
		EasyMock.expect(openIdmRestService.createInstitution(EasyMock.anyObject(Institution.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		final StringBuilder stringBuilder = institutionResource.synchronizeInstitutions(true);
		System.out.println(stringBuilder);

		mockProvider.verifyAll();
	}

	@Test
	public void synchronizeInstitution_TwoExist_TwoDelete_Test() {

		JaxOpenIdmResponse jaxOpenIdmResponse = new JaxOpenIdmResponse();
		final JaxOpenIdmResult jaxOpenIdmResult = creatOpenIdmInst("I-1");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResult);
		final JaxOpenIdmResult jaxOpenIdmResul2 = creatOpenIdmInst("I-2");
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul2);
		final JaxOpenIdmResult jaxOpenIdmResul3 = creatOpenIdmInst("T-3"); // Traegerschaft
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul3);
		final JaxOpenIdmResult jaxOpenIdmResul4 = creatOpenIdmInst("T-4"); // Traegerschaft
		jaxOpenIdmResponse.getResult().add(jaxOpenIdmResul4);


		List<Institution> institutions = new ArrayList<>();
		final Institution institution1 = creatInst("1");
		institutions.add(institution1);
		final Institution institution2 = creatInst("2");
		institutions.add(institution2);

		EasyMock.expect(openIdmRestService.getAll()).andReturn(Optional.of(jaxOpenIdmResponse));
		EasyMock.expect(institutionService.getAllActiveInstitutionen()).andReturn((institutions));

		EasyMock.expect(openIdmRestService.deleteInstitution("3")).andReturn(true);
		EasyMock.expect(openIdmRestService.deleteInstitution("4")).andReturn(true);

		EasyMock.expect(openIdmRestService.createInstitution(EasyMock.anyObject(Institution.class))).andThrow(new AssertionFailedError("Nothing to Create!")).anyTimes();

		mockProvider.replayAll();

		final StringBuilder stringBuilder = institutionResource.synchronizeInstitutions(true);
		System.out.println(stringBuilder);

		mockProvider.verifyAll();
	}

	private Institution creatInst(String name) {
		Institution institution = new Institution();
		institution.setId(name);
		institution.setName(name);
		return institution;
	}

}
