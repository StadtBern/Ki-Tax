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

package ch.dvbern.ebegu.batch.jobs.report;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.services.WorkjobService;

/**
 * Wenn ein Job Fehlgeschlagen in der Tabelle steht beim Startup so kann dieser nie mehr erfolgreich finnished
 * werden da wir den JBoss die internen Jobs nicht ueber einen Neustart speichern lassen. Damit diese den
 * Benutzer nicht blockieren beim generieren von Statistiken, loeschen wir die mal alle
 * siehe auch EBEGU-1775
 */
@Startup
@Singleton
@RunAs(UserRoleName.SUPER_ADMIN)
public class WorkjobStartupCleaner {

	@Inject
	private Instance<WorkjobService> workjobService;

	@Resource
	private TimerService timerService;

	/**
	 * damit wir injection benutzten koennen, machen wir hier einen Timer der die Timeout Methode ausfuehrt
	 */
	@PostConstruct
	public void startControlBeans() {
		timerService.createTimer(7 * 1000, "Wir muessen warten bis alle Services verfuegbar sind");
	}


	@Timeout
	public void startCleanupOfWorkjobs() {
		final List<Workjob> unfinishedWorkjobs = workjobService.get().findUnfinishedWorkjobs();
		unfinishedWorkjobs.forEach(workjob ->  workjobService.get().removeWorkjob(workjob));
	}


}
