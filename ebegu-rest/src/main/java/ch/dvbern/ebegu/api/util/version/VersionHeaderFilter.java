package ch.dvbern.ebegu.api.util.version;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * All responses that are sent to the client get a new header-param with the server version.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class VersionHeaderFilter implements ContainerResponseFilter {

	private static final String X_EBEGU_VERSION = "x-ebegu-version";
	private static final String X_EBEGU_BUILD_TIME = "x-ebegu-build-time";

	@Inject
	private VersionInfoBean versionInfoBean;

	@Override
	public void filter(@Nonnull ContainerRequestContext requestContext, @Nonnull ContainerResponseContext responseContext)
		throws IOException {
		versionInfoBean.getVersionInfo().ifPresent(versionInfo -> {
			responseContext.getHeaders().add(X_EBEGU_VERSION, versionInfo.getVersion());
			if (versionInfo.getBuildTimestamp() != null) {
				responseContext.getHeaders().add(X_EBEGU_BUILD_TIME, versionInfo.getBuildTimestamp());
			}
		});
	}
}
