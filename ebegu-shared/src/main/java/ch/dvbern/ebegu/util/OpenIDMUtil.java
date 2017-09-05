package ch.dvbern.ebegu.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
			return "I-" + institutionId;
		}

	@Nonnull
	public static String convertToOpenIdmTraegerschaftUID(@Nonnull String traegerschaftId) {
		return "T-" + traegerschaftId;
	}
}
