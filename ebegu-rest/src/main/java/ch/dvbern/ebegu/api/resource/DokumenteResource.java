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

package ch.dvbern.ebegu.api.resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDokument;
import ch.dvbern.ebegu.api.dtos.JaxDokumentGrund;
import ch.dvbern.ebegu.api.dtos.JaxDokumente;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.util.DokumenteUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer Dokumente
 */
@Path("dokumente")
@Stateless
@Api(description = "Resource für die Verwaltung von Dokumenten")
public class DokumenteResource {

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;

	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private FileSaverService fileSaverService;

	@ApiOperation(value = "Gibt alle Dokumente zurück, welche zum übergebenen Gesuch vorhanden sind.",
		response = JaxDokumente.class)
	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumente getDokumente(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {
			final Set<DokumentGrund> dokumentGrundsNeeded = dokumentenverzeichnisEvaluator.calculate(gesuch.get());
			//TODO reviewer addSonstige und addPapiergesuch sollte moeglichwerweise hier nicht zu den needed hinzugefuegt werden... weil sie ja eigentlich nicht needed sind und nur auf dem GUI angezeigt werden sollen
			dokumentenverzeichnisEvaluator.addSonstige(dokumentGrundsNeeded);
			dokumentenverzeichnisEvaluator.addPapiergesuch(dokumentGrundsNeeded, gesuch.get());
			final Collection<DokumentGrund> persistedDokumentGrund = dokumentGrundService.findAllDokumentGrundByGesuch(gesuch.get());
			final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil.mergeNeededAndPersisted(dokumentGrundsNeeded, persistedDokumentGrund, gesuch.get());
			return converter.dokumentGruendeToJAX(dokumentGrundsMerged);
		}
		throw new EbeguEntityNotFoundException("getDokumente", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}

	@ApiOperation(value = "Gibt alle Dokumente eines bestimmten Typs zurück, die zu einem Gesuch vorhanden sind",
		response = JaxDokumente.class)
	@Nullable
	@GET
	@Path("/byTyp/{gesuchId}/{dokumentGrundTyp}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumente getDokumente(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId,
		@Nonnull @NotNull @PathParam("dokumentGrundTyp") DokumentGrundTyp dokumentGrundTyp) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {
			final Set<DokumentGrund> dokumentGrundsNeeded = new HashSet<>();
			dokumentenverzeichnisEvaluator.addPapiergesuch(dokumentGrundsNeeded, gesuch.get());
			final Collection<DokumentGrund> persistedDokumentGrund = dokumentGrundService.findAllDokumentGrundByGesuchAndDokumentType(gesuch.get(), dokumentGrundTyp);
			final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil.mergeNeededAndPersisted(dokumentGrundsNeeded, persistedDokumentGrund, gesuch.get());
			return converter.dokumentGruendeToJAX(dokumentGrundsMerged);
		}
		throw new EbeguEntityNotFoundException("getDokumente", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}

	@ApiOperation(value = "Aktualisiert ein Dokument in der Datenbank", response = JaxDokumentGrund.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumentGrund updateDokumentGrund(
		@Nonnull @NotNull @Valid JaxDokumentGrund dokumentGrundJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(dokumentGrundJAXP.getId());
		Optional<DokumentGrund> dokumentGrundOptional = dokumentGrundService.findDokumentGrund(dokumentGrundJAXP.getId());
		DokumentGrund dokumentGrundFromDB = dokumentGrundOptional.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, dokumentGrundJAXP.getId()));
		// beim DokumentGrund mit dem DokumentGrundTyp SONSTIGE_NACHWEISE oder PAPIERGESUCH  soll das needed-Flag (transient)
		// per default auf false sein. sonst stimmt der Wizardstep-Status nicht
		if(dokumentGrundFromDB.getDokumentGrundTyp().equals(DokumentGrundTyp.SONSTIGE_NACHWEISE) || dokumentGrundFromDB.getDokumentGrundTyp().equals(DokumentGrundTyp.PAPIERGESUCH)){
			dokumentGrundFromDB.setNeeded(false);
		}
		// Files where not in the list anymore, should be deleted on Filesystem!
		Set<Dokument> dokumentsToRemove = findDokumentToRemove(dokumentGrundJAXP, dokumentGrundFromDB);
		for (Dokument dokument : dokumentsToRemove) {
			// Es duerfen nur dokumente ohne VorgaengerId geloescht werden. D.h. es koennen nur Files auf dem FS einer
			// Mutation geloescht werden, welche neu hinzugefuegt worden sind. Kopierte Dokumente des EG duerfen nicht geloescht werden!
			if (dokument.getVorgaengerId() == null) {
				fileSaverService.remove(dokument.getFilepfad());
			}
		}

		DokumentGrund dokumentGrundToMerge = converter.dokumentGrundToEntity(dokumentGrundJAXP, dokumentGrundFromDB);
		DokumentGrund modifiedDokumentGrund = this.dokumentGrundService.updateDokumentGrund(dokumentGrundToMerge);

		if (modifiedDokumentGrund == null) {
			return null;
		}
		return converter.dokumentGrundToJax(modifiedDokumentGrund);
	}

	/**
	 * Gibt Liste von Dokumenten zurück, welche auf der DB vorhanden sind auf dem jaxB Objekt jedoch nicht mehr.
	 */
	private Set<Dokument> findDokumentToRemove(JaxDokumentGrund dokumentGrundJAXP, DokumentGrund dokumentGrundFromDB) {
		Validate.notNull(dokumentGrundFromDB.getDokumente());
		Validate.notNull(dokumentGrundJAXP.getDokumente());

		Set<Dokument> dokumentsToRemove = new HashSet<>();
		for (Dokument dokument : dokumentGrundFromDB.getDokumente()) {
			boolean found = false;
			for (JaxDokument jaxDokument : dokumentGrundJAXP.getDokumente()) {
				if (dokument.getId().equals(jaxDokument.getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				dokumentsToRemove.add(dokument);
			}
		}
		return dokumentsToRemove;
	}
}
