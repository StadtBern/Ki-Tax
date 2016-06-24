package ch.dvbern.ebegu.api.util;

import javax.annotation.Nonnull;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * Allgemeine Utils fuer Rest Funktionalitaeten
 */
public final class RestUtil {

	public static boolean isFileDownloadRequest(@Nonnull ContainerRequestContext requestContext) {
		return requestContext.getUriInfo().getPath().startsWith("/blobs/temp");
	}

}
