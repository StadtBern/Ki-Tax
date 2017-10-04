/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
	public static String convertToEBEGUID(@Nullable String openIdmUid) {
		if (openIdmUid != null && openIdmUid.length() > 2) {
			return openIdmUid.substring(2);
		} else {
			return "";
		}
	}

	@Nonnull
	public static String convertToOpenIdmInstitutionsUID(@Nonnull String institutionId) {
		return AuthConstants.OPENIDM_INST_PREFIX + institutionId;
	}

	@Nonnull
	public static String convertToOpenIdmTraegerschaftUID(@Nonnull String traegerschaftId) {
		return AuthConstants.OENIDM_TRAEGERSCHAFT_PREFIX + traegerschaftId;
	}
}
