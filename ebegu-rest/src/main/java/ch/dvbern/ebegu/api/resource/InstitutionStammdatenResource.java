package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxInstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
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
import javax.ws.rs.core.UriInfo;
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
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitutionStammdaten create(
		@Nonnull @NotNull @Valid JaxInstitutionStammdaten institutionStammdatenJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		InstitutionStammdaten convertedInstitutionStammdaten = converter.institutionStammdatenToEntity(institutionStammdatenJAXP, new InstitutionStammdaten());
		InstitutionStammdaten persistedInstitutionStammdaten = this.institutionStammdatenService.saveInstitutionStammdaten(convertedInstitutionStammdaten);

		return converter.institutionStammdatenToJAX(persistedInstitutionStammdaten);
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitutionStammdaten update(
		@Nonnull @NotNull @Valid JaxInstitutionStammdaten institutionStammdatenJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(institutionStammdatenJAXP.getId());
		String institutionStammdatenID = converter.toEntityId(institutionStammdatenJAXP);
		Optional<InstitutionStammdaten> optional = institutionStammdatenService.findInstitutionStammdaten(institutionStammdatenID);
		InstitutionStammdaten institutionStammdatenFromDB = optional.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionStammdatenID));
		InstitutionStammdaten institutionStammdatenToMerge = converter.institutionStammdatenToEntity(institutionStammdatenJAXP, institutionStammdatenFromDB);


		InstitutionStammdaten modifiedInstitutionStammdaten = this.institutionStammdatenService.saveInstitutionStammdaten(institutionStammdatenToMerge);
		return converter.institutionStammdatenToJAX(modifiedInstitutionStammdaten);
	}

	@Nullable
	@GET
	@Path("/{institutionStammdatenId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitutionStammdaten findInstitutionStammdaten(@Nonnull @NotNull JaxId institutionStammdatenJAXPId) throws EbeguException {
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

}
