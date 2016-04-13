package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxPerson;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

/**
 * Resource fuer Gesuch
 */
@Path("gesuche")
@Stateless
@Api
public class GesuchResource {

	@Inject
	private GesuchService gesuchService;
	@Inject
	private PersonService personService;
	@Inject
	private PersonResource personResource;

	@Inject
	private FamiliensituationService familiensituationService;
	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Creates a new Gesuch in the database. The transfer object also has a relation to Familiensituation " +
		"which is stored in the database as well.")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Gesuch convertedGesuch = converter.gesuchToEntity(gesuchJAXP, new Gesuch());
		Gesuch persistedGesuch = this.gesuchService.createGesuch(convertedGesuch);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(GesuchResource.class)
			.path("/" + persistedGesuch.getId())
			.build();

		JaxGesuch jaxGesuch = converter.gesuchToJAX(persistedGesuch);

		return Response.created(uri).entity(jaxGesuch).build();
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch update(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(gesuchJAXP.getId());
		String gesuchsID = converter.toEntityId(gesuchJAXP);
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(gesuchsID);
		Gesuch gesuchFromDB = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXP.getId().toString()));

		JaxPerson gesuchsteller1 = null;
		if(gesuchJAXP.getGesuchsteller1() != null) {
			if (gesuchJAXP.getGesuchsteller1().getTimestampErstellt() != null) {
				gesuchsteller1 = personResource.update(gesuchJAXP.getGesuchsteller1(), uriInfo, response);
			} else {
				gesuchsteller1 = personResource.create(gesuchJAXP.getGesuchsteller1(), uriInfo, response);
			}
		}

		JaxPerson gesuchsteller2 = null;
		if(gesuchJAXP.getGesuchsteller2() != null) {
			if (gesuchJAXP.getGesuchsteller2().getTimestampErstellt() != null) {
				gesuchsteller2 = personResource.update(gesuchJAXP.getGesuchsteller2(), uriInfo, response);
			} else {
				gesuchsteller2 = personResource.create(gesuchJAXP.getGesuchsteller2(), uriInfo, response);
			}
		}

		gesuchJAXP.setGesuchsteller1(gesuchsteller1);
		gesuchJAXP.setGesuchsteller2(gesuchsteller2);
		Gesuch gesuchToMerge = converter.gesuchToEntity(gesuchJAXP, gesuchFromDB);
		Gesuch modifiedGesuch = this.gesuchService.updateGesuch(gesuchToMerge);

		// todo team entscheiden ob wir Gesuch updaten und dann autmoatisch die abhaengige
		// datensaetze oder jeder Datensatz soll getrennt gespeichert werden (Gesuch->Gesuchsteller)

		JaxGesuch jaxGesuch = converter.gesuchToJAX(modifiedGesuch);
		if(gesuchsteller1 != null) {
			jaxGesuch.setGesuchsteller1(gesuchsteller1);
		}
		if(gesuchsteller2 != null) {
			jaxGesuch.setGesuchsteller1(gesuchsteller2);
		}
		return jaxGesuch;

	}

	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch findGesuch(
		@Nonnull @NotNull JaxId gesuchJAXPId) throws EbeguException {

		return null;
	}

}
