package ch.dvbern.ebegu.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.KindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer Kinder
 */
@Path("betreuungen")
@Stateless
@Api
public class BetreuungResource {

	@Inject
	private BetreuungService betreuungService;
	@Inject
	private KindService kindService;
	@Inject
	private FallService fallService;
	@Inject
	private JaxBConverter converter;
	@Inject
	private ResourceHelper resourceHelper;
	@Inject
	private GesuchService gesuchService;



	//TODO (hefr) Dieser Service wird immer nur fuer Betreuungen verwendet, nie fuer Abwesenheiten
	@Nonnull
	@PUT
	@Path("/betreuung/{kindId}/{abwesenheit}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung saveBetreuung(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Nonnull @NotNull @PathParam ("abwesenheit") Boolean abwesenheit,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			resourceHelper.assertGesuchStatusForBenutzerRole(kind.get().getGesuch());
			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			convertedBetreuung.setKind(kind.get());
			Betreuung persistedBetreuung = this.betreuungService.saveBetreuung(convertedBetreuung, abwesenheit);

			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

	//TODO (hefr) dieser service wird immer nur fuer Abwesenheiten verwendet
	@Nonnull
	@PUT
	@Path("/all/{abwesenheit}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxBetreuung> saveBetreuungen(
		@Nonnull @NotNull @Valid List<JaxBetreuung> betreuungenJAXP,
		@Nonnull @NotNull @PathParam ("abwesenheit") Boolean abwesenheit,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		if (!betreuungenJAXP.isEmpty()) {
			if (betreuungenJAXP.get(0).getGesuchId() != null) {
				final Optional<Gesuch> gesuch = gesuchService.findGesuch(betreuungenJAXP.get(0).getGesuchId());
				gesuch.ifPresent(gesuch1 -> resourceHelper.assertGesuchStatusForBenutzerRole(gesuch1));
			}
		}

		List<JaxBetreuung> resultBetreuungen = new ArrayList<>();
		betreuungenJAXP.forEach(betreuungJAXP -> {
			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			Betreuung persistedBetreuung = this.betreuungService.saveBetreuung(convertedBetreuung, abwesenheit);

			resultBetreuungen.add(converter.betreuungToJAX(persistedBetreuung));
		});
		return resultBetreuungen;
	}

	@Nonnull
	@PUT
	@Path("/abweisen/{kindId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung betreuungPlatzAbweisen(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(betreuungJAXP.getId());
		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertBetreuungStatusEqual(betreuungJAXP.getId(), Betreuungsstatus.WARTEN);

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
			resourceHelper.assertGesuchStatusForBenutzerRole(kind.get().getGesuch());

			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			convertedBetreuung.setKind(kind.get());
			Betreuung persistedBetreuung = this.betreuungService.betreuungPlatzAbweisen(convertedBetreuung);

			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

	@Nonnull
	@PUT
	@Path("/bestaetigen/{kindId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung betreuungPlatzBestaetigen(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(betreuungJAXP.getId());

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertBetreuungStatusEqual(betreuungJAXP.getId(), Betreuungsstatus.WARTEN);

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
			resourceHelper.assertGesuchStatusForBenutzerRole(kind.get().getGesuch());

			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			convertedBetreuung.setKind(kind.get());
			Betreuung persistedBetreuung = this.betreuungService.betreuungPlatzBestaetigen(convertedBetreuung);

			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

	@Nullable
	@GET
	@Path("/{betreuungId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung findBetreuung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId betreuungJAXPId) throws EbeguException {
		Validate.notNull(betreuungJAXPId.getId());
		String id = converter.toEntityId(betreuungJAXPId);
		Optional<Betreuung> fallOptional = betreuungService.findBetreuung(id);

		if (!fallOptional.isPresent()) {
			return null;
		}
		Betreuung betreuungToReturn = fallOptional.get();
		return converter.betreuungToJAX(betreuungToReturn);
	}

	@Nullable
	@DELETE
	@Path("/{betreuungId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeBetreuung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId betreuungJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(betreuungJAXPId.getId());
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(betreuungJAXPId.getId());

		if (betreuung.isPresent()) {
			// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
			resourceHelper.assertGesuchStatusForBenutzerRole(betreuung.get().extractGesuch());
			betreuungService.removeBetreuung(converter.toEntityId(betreuungJAXPId));
			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("removeBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "BetreuungID invalid: " + betreuungJAXPId.getId());
	}

	@ApiOperation(value = "gets all Betreuungen for a Fall across all Gesuchperioden")
	@Nullable
	@GET
	@Path("/alleBetreuungen/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAllBetreuungenWithVerfuegungFromFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Fall> fallOptional = fallService.findFall(converter.toEntityId(fallId));


		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fall = fallOptional.get();

		Collection<Betreuung> betreuungCollection = betreuungService.findAllBetreuungenWithVerfuegungFromFall(fall);
		Collection<JaxBetreuung> jaxBetreuungList = converter.betreuungListToJax(betreuungCollection);


		return Response.ok(jaxBetreuungList).build();

	}


}
