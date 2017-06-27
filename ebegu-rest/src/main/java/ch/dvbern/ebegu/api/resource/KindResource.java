package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.dto.KindDubletteDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.KindService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * REST Resource fuer Kinder
 */
@Path("kinder")
@Stateless
@Api(description = "Resource zum verwalten von Kindern eines Gesuchstellers")
public class KindResource {

	@Inject
	private KindService kindService;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private JaxBConverter converter;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private BenutzerService benutzerService;

	@Nullable
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxKindContainer saveKind(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchId,
		@Nonnull @NotNull @Valid JaxKindContainer kindContainerJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {
			KindContainer kindToMerge = new KindContainer();
			if (kindContainerJAXP.getId() != null) {
				Optional<KindContainer> optional = kindService.findKind(kindContainerJAXP.getId());
				kindToMerge = optional.orElse(new KindContainer());
			}
			KindContainer convertedKind = converter.kindContainerToEntity(kindContainerJAXP, kindToMerge);
			convertedKind.setGesuch(gesuch.get());
			KindContainer persistedKind = this.kindService.saveKind(convertedKind);

			return converter.kindContainerToJAX(persistedKind);
		}
		throw new EbeguEntityNotFoundException("saveKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}


	@Nullable
	@GET
	@Path("/find/{kindContainerId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxKindContainer findKind(
		@Nonnull @NotNull @PathParam ("kindContainerId") JaxId kindJAXPId) throws EbeguException {

		Validate.notNull(kindJAXPId.getId());
		String kindID = converter.toEntityId(kindJAXPId);
		Optional<KindContainer> optional = kindService.findKind(kindID);

		if (!optional.isPresent()) {
			return null;
		}
		JaxKindContainer jaxKindContainer = converter.kindContainerToJAX(optional.get());

		Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
		if (currentBenutzer.isPresent()) {
			UserRole currentUserRole = currentBenutzer.get().getRole();
			// Es wird gecheckt ob der Benutzer zu einer Institution/Traegerschaft gehoert. Wenn ja, werden die Kinder gefilter
			// damit nur die relevanten Kinder geschickt werden
			if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT.equals(currentUserRole) || UserRole.SACHBEARBEITER_INSTITUTION.equals(currentUserRole)) {
				Collection<Institution> instForCurrBenutzer = institutionService.getAllowedInstitutionenForCurrentBenutzer();
				RestUtil.purgeSingleKindAndBetreuungenOfInstitutionen(jaxKindContainer, instForCurrBenutzer);
			}
		}
		return jaxKindContainer;
	}

	@Nullable
	@DELETE
	@Path("/{kindContainerId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeKind(
		@Nonnull @NotNull @PathParam("kindContainerId") JaxId kindJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(kindJAXPId.getId());
		Optional<KindContainer> kind = kindService.findKind(kindJAXPId.getId());
		if (kind.isPresent()) {
			kindService.removeKind(kind.get());
			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("removeKind", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindID invalid: " + kindJAXPId.getId());

	}

	@Nullable
	@GET
	@Path("/dubletten/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<KindDubletteDTO> getKindDubletten(@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJaxId) throws
		EbeguException {
		Validate.notNull(gesuchJaxId.getId());
		String gesuchId = converter.toEntityId(gesuchJaxId);
		Set<KindDubletteDTO> kindDubletten = kindService.getKindDubletten(gesuchId);
		return kindDubletten;
	}
}
