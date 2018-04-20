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

package ch.dvbern.ebegu.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Service class, die von allen PrintServices erweitert werden muss. Sie enthaelt Methoden, um die Vorlagen zu laden
 */
public abstract class AbstractPrintService extends AbstractBaseService {

	private final Logger LOG = LoggerFactory.getLogger(AbstractPrintService.class.getSimpleName());

	@Inject
	private EbeguVorlageService ebeguVorlageService;

	// We need to pass to EbeguVorlageService a new EntityManager to avoid errors like ConcurrentModificatinoException. So we create it here
	// and pass it to the methods of EbeguVorlageService we need to call.
	//http://stackoverflow.com/questions/18267269/correct-way-to-do-an-entitymanager-query-during-hibernate-validation
	@PersistenceUnit(unitName = "ebeguPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Sucht nach der richtigen Vorlage in den Parameters. Wenn der Parameter existiert, wird die Vorlage geladen.
	 * Sollte der Parameter nicht existieren, wird das drfault-template geladen
	 */
	@Nonnull
	protected InputStream getVorlageStream(@Nonnull LocalDate dateAb, @Nonnull LocalDate dateBis, @Nonnull EbeguVorlageKey vorlageKey) {
		final Optional<EbeguVorlage> vorlage = ebeguVorlageService.getEbeguVorlageByDatesAndKey(dateAb, dateBis,
			vorlageKey, createEntityManager());
		if (vorlage.isPresent() && vorlage.get().getVorlage() != null) {
			try {
				return new FileInputStream(vorlage.get().getVorlage().getFilepfad());
			} catch (final FileNotFoundException e) {
				// Wenn die Datei nicht gefunden wird, die Exception Message wird gelogt und das default-template geladen
				LOG.error("Die Datei mit der Vorlage fuer " + vorlageKey + " wurde nicht gefunden. Die default Vorlage ("
					+ vorlageKey.getDefaultVorlagePath() + ") wird stattdessen benutzt");
			}
		}
		return AbstractPrintService.class.getResourceAsStream(vorlageKey.getDefaultVorlagePath());
	}

	@Nullable
	private EntityManager createEntityManager() {
		if (entityManagerFactory != null) {
			return entityManagerFactory.createEntityManager(); // creates a new EntityManager
		}
		return null;
	}

}
