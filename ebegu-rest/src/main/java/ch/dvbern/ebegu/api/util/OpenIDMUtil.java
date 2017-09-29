package ch.dvbern.ebegu.api.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.api.AuthConstants;

/**
 * Helper to convert IDs from IAM to our id format and vice versa
 */
public final class OpenIDMUtil {

	private OpenIDMUtil() {
	}

	@Nonnull
	public static  String convertToEBEGUID(@Nullable String openIdmUid) {
		if (openIdmUid != null && openIdmUid.length() > 2) {
			return openIdmUid.substring(2);
		} else {
			return "";
		}
	}

	@Nonnull
	public static String convertToOpenIdmInstitutionsUID(@Nonnull String institutionId) {
			return AuthConstants.OPENIDM_INST_PREFIX +  institutionId;
		}

	@Nonnull
	public static String convertToOpenIdmTraegerschaftUID(@Nonnull String traegerschaftId) {
		return AuthConstants.OENIDM_TRAEGERSCHAFT_PREFIX + traegerschaftId;
	}
}
