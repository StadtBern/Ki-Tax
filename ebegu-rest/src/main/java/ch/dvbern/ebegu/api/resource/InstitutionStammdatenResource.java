package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxInstitutionStammdaten;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.util.DateUtil;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Resource fuer InstitutionStammdaten
 */
@Path("institutionstammdaten")
@Stateless
@Api
public class InstitutionStammdatenResource {

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitutionStammdaten saveInstitutionStammdaten(
		@Nonnull @NotNull @Valid JaxInstitutionStammdaten institutionStammdatenJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		InstitutionStammdaten instDaten;
		if (institutionStammdatenJAXP.getId() != null) {
			Optional<InstitutionStammdaten> optional = institutionStammdatenService.findInstitutionStammdaten(institutionStammdatenJAXP.getId());
			instDaten = optional.orElse(new InstitutionStammdaten());
		} else {
			instDaten = new InstitutionStammdaten();
			Adresse adresse = new Adresse();
			instDaten.setAdresse(adresse);
		}
		InstitutionStammdaten convertedInstData = converter.institutionStammdatenToEntity(institutionStammdatenJAXP, instDaten);
		InstitutionStammdaten persistedInstData = institutionStammdatenService.saveInstitutionStammdaten(convertedInstData);

		return converter.institutionStammdatenToJAX(persistedInstData);

	}

	@Nullable
	@GET
	@Path("/{institutionStammdatenId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitutionStammdaten findInstitutionStammdaten(
		@Nonnull @NotNull @PathParam("institutionStammdatenId") JaxId institutionStammdatenJAXPId) throws EbeguException {

		Validate.notNull(institutionStammdatenJAXPId.getId());
		String institutionStammdatenID = converter.toEntityId(institutionStammdatenJAXPId);
		Optional<InstitutionStammdaten> optional = institutionStammdatenService.findInstitutionStammdaten(institutionStammdatenID);

		if (!optional.isPresent()) {
			return null;
		}
		return converter.institutionStammdatenToJAX(optional.get());
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitutionStammdaten> getAllInstitutionStammdaten() {
		return institutionStammdatenService.getAllInstitutionStammdaten().stream()
			.map(instStammdaten -> converter.institutionStammdatenToJAX(instStammdaten))
			.collect(Collectors.toList());
	}

	@Nullable
	@DELETE
	@Path("/{institutionStammdatenId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeInstitutionStammdaten(
		@Nonnull @NotNull @PathParam("institutionStammdatenId") JaxId institutionStammdatenJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(institutionStammdatenJAXPId.getId());
		institutionStammdatenService.removeInstitutionStammdaten(converter.toEntityId(institutionStammdatenJAXPId));
		return Response.ok().build();
	}

	/**
	 * Sucht in der DB alle InstitutionStammdaten, bei welchen das gegebene Datum zwischen DatumVon und DatumBis liegt
	 * Wenn das Datum null ist, wird dieses automatisch als heutiges Datum gesetzt.
	 *
	 * @param stringDate Date als String mit Format "yyyy-MM-dd". Wenn null, heutiges Datum gesetzt
	 * @return Liste mit allen InstitutionStammdaten die den Bedingungen folgen
     */
	@Nonnull
	@GET
	@Path("/date")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitutionStammdaten> getAllInstitutionStammdatenByDate(
		@Nullable @QueryParam("date") String stringDate) {

		LocalDate date = DateUtil.parseStringToDateOrReturnNow(stringDate);
		return institutionStammdatenService.getAllInstitutionStammdatenByDate(date).stream()
			.map(institutionStammdaten -> converter.institutionStammdatenToJAX(institutionStammdaten))
			.collect(Collectors.toList());
	}

	/**
	 * Sucht in der DB alle InstitutionStammdaten, bei welchen die Institutions-id dem übergabeparameter entspricht
	 *
	 * @param institutionJAXPId ID der gesuchten Institution
	 * @return Liste mit allen InstitutionStammdaten die der Bedingung folgen
	 */
	@Nonnull
	@GET
	@Path("/institution/{institutionId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitutionStammdaten> getAllInstitutionStammdatenByInstitution(
		@Nonnull @NotNull @PathParam("institutionId") JaxId institutionJAXPId) {

		Validate.notNull(institutionJAXPId.getId());
		String institutionID = converter.toEntityId(institutionJAXPId);
		return institutionStammdatenService.getAllInstitutionStammdatenByInstitution(institutionID).stream()
			.map(instStammdaten -> converter.institutionStammdatenToJAX(instStammdaten))
			.collect(Collectors.toList());
	}

	/**
	 * Gibt alle BetreuungsangebotsTypen zurueck, welche die Institutionen des eingeloggten Benutzers anbieten
	 */
	@Nonnull
	@GET
	@Path("/currentuser")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<BetreuungsangebotTyp> getBetreuungsangeboteForInstitutionenOfCurrentBenutzer() {
		List<BetreuungsangebotTyp> result = new ArrayList<>();
		result.addAll(institutionStammdatenService.getBetreuungsangeboteForInstitutionenOfCurrentBenutzer());
		return result;
	}
}
