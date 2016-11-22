package ch.dvbern.ebegu.rest.test.client;

import ch.dvbern.ebegu.api.client.OpenIdmService;
import ch.dvbern.ebegu.rest.test.AbstractEbeguRestTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Testet FallResource
 */

@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class OpenIdmServiceTest extends AbstractEbeguRestTest {


	@Inject
	private OpenIdmService openIdmService;


	@Test
	public void testLogin() {
		final Response response = openIdmService.login();
		System.out.println(response);


	}
}
