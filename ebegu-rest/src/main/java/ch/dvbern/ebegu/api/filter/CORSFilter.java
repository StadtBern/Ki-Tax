package ch.dvbern.ebegu.api.filter;

import ch.dvbern.ebegu.config.config.EbeguConfiguration;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Dieser Filter erlaubt cross origin requests. Dies ist natuerlich ein Sicherheitsrisiko und sollte in Produktion
 * entsprechend eingeschraenkt werden
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Inject
	private EbeguConfiguration configuration;

   @Override
   public void filter(final ContainerRequestContext requestContext,
					  final ContainerResponseContext cres) throws IOException {
	   if (configuration.getIsDevmode()) {
		   cres.getHeaders().add("Access-Control-Allow-Origin", "*");
	      cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
	      cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
	      cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	      cres.getHeaders().add("Access-Control-Max-Age", "1209600");

	   }

   }

}
