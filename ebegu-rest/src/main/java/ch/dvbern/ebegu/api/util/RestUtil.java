package ch.dvbern.ebegu.api.util;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Allgemeine Utils fuer Rest Funktionalitaeten
 */
public final class RestUtil {

	private static final Pattern MATCH_QUOTE = Pattern.compile("\"");

	/**
	 * Parst den Content-Disposition Header
	 * @param part aus einem {@link MultipartFormDataInput}. Bei keinem Filename oder einem leeren Filename wird dieser auf null reduziert.
	 */
	@Nonnull
	public static UploadFileInfo parseUploadFile(@Nonnull InputPart part) throws MimeTypeParseException {
		Objects.requireNonNull(part);

		MultivaluedMap<String, String> headers = part.getHeaders();
		String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
		String filename = null;
		String contentType = headers.getFirst("Content-Type");
		for (String name : contentDispositionHeader) {
			if (name.toLowerCase(Locale.US).trim().startsWith("filename")) {
				String[] tmp = name.split("=");
				filename = MATCH_QUOTE.matcher(tmp[1].trim()).replaceAll("");
			}
		}
		return new UploadFileInfo(StringUtils.defaultIfBlank(filename, null), new MimeType(contentType));
	}

	public static boolean isFileDownloadRequest(@Nonnull ContainerRequestContext requestContext) {
		return requestContext.getUriInfo().getPath().startsWith("/blobs/temp");
	}

}
