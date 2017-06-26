package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchstellerService;
import ch.dvbern.ebegu.services.PersonenSucheService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.Optional;

/**
 * REST Resource fuer Gesuchsteller
 */
@Path("gesuchsteller")
@Stateless
@Api
public class GesuchstellerResource {

	@Inject
	private GesuchstellerService gesuchstellerService;

	@Inject
	private PersonenSucheService personenSucheService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Updates a Gesuchsteller or creates it if it doesn't exist in the database. The transfer object also has a relation to adressen " +
		"(wohnadresse, umzugadresse, korrespondenzadresse) these are stored in the database as well. Note that wohnadresse and" +
		"umzugadresse are both stored as consecutive wohnadressen in the database. Umzugs flag wird gebraucht, um WizardSteps" +
		"richtig zu setzen.")
	@Nullable
	@PUT
	@Path("/{gesuchId}/gsNumber/{gsNumber}/{umzug}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchstellerContainer saveGesuchsteller(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchContJAXPId,
		@Nonnull @NotNull @PathParam ("gsNumber") Integer gsNumber,
		@Nonnull @NotNull @PathParam ("umzug") Boolean umzug,
		@Nonnull @NotNull @Valid JaxGesuchstellerContainer gesuchstellerJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchContJAXPId.getId());
		if (gesuch.isPresent()) {
			GesuchstellerContainer gesuchstellerToMerge = new GesuchstellerContainer();
			if (gesuchstellerJAXP.getId() != null) {
				Optional<GesuchstellerContainer> optional = gesuchstellerService.findGesuchsteller(gesuchstellerJAXP.getId());
				gesuchstellerToMerge = optional.orElse(new GesuchstellerContainer());
			}

			GesuchstellerContainer convertedGesuchsteller = converter.gesuchstellerContainerToEntity(gesuchstellerJAXP, gesuchstellerToMerge);
			GesuchstellerContainer persistedGesuchsteller = this.gesuchstellerService.saveGesuchsteller(convertedGesuchsteller, gesuch.get(), gsNumber, umzug);

			return converter.gesuchstellerContainerToJAX(persistedGesuchsteller);
		}
		throw new EbeguEntityNotFoundException("createGesuchsteller", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchContJAXPId.getId());

	}

	@Nullable
	@GET
	@Path("/id/{gesuchstellerId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchstellerContainer findGesuchsteller(
		@Nonnull @NotNull @PathParam("gesuchstellerId") JaxId gesuchstellerJAXPId) throws EbeguException {

		Validate.notNull(gesuchstellerJAXPId.getId());
		String gesuchstellerID = converter.toEntityId(gesuchstellerJAXPId);
		Optional<GesuchstellerContainer> optional = gesuchstellerService.findGesuchsteller(gesuchstellerID);

		if (!optional.isPresent()) {
			return null;
		}
		GesuchstellerContainer gesuchstellerToReturn = optional.get();

		return converter.gesuchstellerContainerToJAX(gesuchstellerToReturn);
	}

	@Nullable
	@GET
	@Path("/ewk/search/attributes")
	@Produces(MediaType.APPLICATION_JSON)
	public EWKResultat suchePersonByAttributes(
            @QueryParam("nachname") String nachname,
            @QueryParam("vorname") String vorname,
            @QueryParam("geburtsdatum") String geburtsdatum,
            @QueryParam("geschlecht") String geschlecht,
            @Context HttpServletRequest request, @Context UriInfo uriInfo) throws EbeguException {
        Validate.notNull(nachname, "name must be set");
        Validate.notNull(vorname, "vorname must be set");
        Validate.notNull(geburtsdatum, "geburtsdatum must be set");
        Validate.notNull(geschlecht, "geschlecht must be set");
        LocalDate geburtsdatumDate = DateUtil.parseStringToDateOrReturnNow(geburtsdatum);
        return personenSucheService.suchePerson(nachname, geburtsdatumDate, Geschlecht.valueOf(geschlecht));
	}

	@Nullable
	@GET
	@Path("/ewk/search/id/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public EWKResultat suchePersonByPersonId(
		    @Nonnull @NotNull @PathParam("personId") String personId) throws EbeguException {
		return personenSucheService.suchePerson(personId);
	}

	@Nullable
	@PUT
	@Path("/ewk/{gesuchstellerId}/{ewkPersonId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Gesuchsteller selectPerson(
		@Nonnull @NotNull @PathParam("gesuchstellerId") JaxId gesuchstellerJAXPId,
		@Nonnull @NotNull @PathParam("ewkPersonId") JaxId ewkPersonJAXPId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(gesuchstellerJAXPId.getId());
		Validate.notNull(ewkPersonJAXPId.getId());
		String gesuchstellerID = converter.toEntityId(gesuchstellerJAXPId);
		String ewkPersonID = converter.toEntityId(ewkPersonJAXPId);
		Optional<GesuchstellerContainer> optional = gesuchstellerService.findGesuchsteller(gesuchstellerID);

		if (!optional.isPresent()) {
			return null;
		}
		GesuchstellerContainer gesuchstellerToReturn = optional.get();
		return optional.map(gesuchstellerContainer -> personenSucheService.selectPerson(gesuchstellerToReturn.getGesuchstellerJA(), ewkPersonID)).orElse(null);
	}
}
