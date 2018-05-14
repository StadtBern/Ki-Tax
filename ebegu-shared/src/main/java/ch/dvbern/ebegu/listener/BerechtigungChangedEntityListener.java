/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

package ch.dvbern.ebegu.listener;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.entities.BerechtigungHistory;
import ch.dvbern.ebegu.services.BenutzerService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class BerechtigungChangedEntityListener {

	private static BenutzerService benutzerService = null;

	@SuppressFBWarnings(value = "LI_LAZY_INIT_STATIC", justification = "Auch wenn das vlt. mehrfach initialisiert wird... das macht nix, solange am Ende was Richtiges drinsteht")
	private static BenutzerService getBenutzerService() {
		if (benutzerService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection (mal wieder) buggy ist.
			//noinspection NonThreadSafeLazyInitialization
			benutzerService = CDI.current().select(BenutzerService.class).get();
		}
		return benutzerService;
	}

	@PrePersist
	@PreUpdate
	protected void prePersist(@Nonnull Berechtigung berechtigung) {
		save(berechtigung, false);
	}

	@PreRemove
	protected void preDelete(@Nonnull Berechtigung berechtigung) {
		save(berechtigung, true);
	}

	private void save(@Nonnull Berechtigung berechtigung, boolean deleted) {
		BerechtigungHistory newBerechtigungsHistory = new BerechtigungHistory(berechtigung, deleted);
		newBerechtigungsHistory.setTimestampErstellt(LocalDateTime.now());
		String userMutiert = berechtigung.getUserMutiert() != null ? berechtigung.getUserMutiert() : "anonymous";
		newBerechtigungsHistory.setUserErstellt(userMutiert);
		getBenutzerService().saveBerechtigungHistory(newBerechtigungsHistory);
	}
}
