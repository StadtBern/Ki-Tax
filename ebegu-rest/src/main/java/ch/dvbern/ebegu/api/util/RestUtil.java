package ch.dvbern.ebegu.api.util;

import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.UploadFileInfo;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
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
	 *
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

	public static Response buildDownloadResponse(Dokument dokument, boolean attachment) throws IOException {

		Path filePath = Paths.get(dokument.getDokumentPfad());
		//if no guess can be made assume application/octet-stream
		String contentType = Files.probeContentType(filePath);
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		final byte[] bytes = Files.readAllBytes(filePath);

		String disposition = (attachment ? "attachment; " : "inline;") + "filename=\"" + dokument.getDokumentName() + '"';

		return Response.ok(bytes)
			.header("Content-Disposition", disposition)
			.header("Content-Length", bytes.length)
			.type(MediaType.valueOf(contentType)).build();


	}

	/**
	 * Entfernt von der uebergebenen Collection von KindContainer die Kinder, die keine Betreuung mit einer der uebergebenen Institutionen hat.
	 *
	 * @param kindContainers    Alle KindContainers
	 * @param userInstitutionen Institutionen mit denen, die Kinder eine Beziehung haben muessen.
	 */
	public static void purgeKinderAndBetreuungenOfInstitutionen(Collection<JaxKindContainer> kindContainers, Collection<Institution> userInstitutionen) {
		final Iterator<JaxKindContainer> kindsIterator = kindContainers.iterator();
		while (kindsIterator.hasNext()) {
			final JaxKindContainer kind = kindsIterator.next();
			purgeSingleKindAndBetreuungenOfInstitutionen(kind, userInstitutionen);
			if (kind.getBetreuungen().size() == 0) {
				kindsIterator.remove();
			}
		}
	}

	public static void purgeSingleKindAndBetreuungenOfInstitutionen(JaxKindContainer kind, Collection<Institution> userInstitutionen) {
		final Iterator<JaxBetreuung> betreuungIterator = kind.getBetreuungen().iterator();
		while (betreuungIterator.hasNext()) {
			final JaxBetreuung betreuung = betreuungIterator.next();
			if (!RestUtil.isInstitutionInList(userInstitutionen, betreuung.getInstitutionStammdaten().getInstitution())
				|| !(Betreuungsstatus.WARTEN.equals(betreuung.getBetreuungsstatus())
				|| Betreuungsstatus.BESTAETIGT.equals(betreuung.getBetreuungsstatus())
				|| Betreuungsstatus.ABGEWIESEN.equals(betreuung.getBetreuungsstatus()))) {
				betreuungIterator.remove();
			}
		}
	}

	private static boolean isInstitutionInList(Collection<Institution> userInstitutionen, JaxInstitution institutionToLookFor) {
		for (final Institution institutionInList : userInstitutionen) {
			if (institutionInList.getId().equals(institutionToLookFor.getId())) {
				return true;
			}
		}
		return false;
	}
}
