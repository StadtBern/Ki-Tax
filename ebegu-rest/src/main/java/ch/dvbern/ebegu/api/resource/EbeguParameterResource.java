package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEbeguParameter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Resource fuer E-BEGU Parameter
 */
@Path("parameter")
@Stateless
@Api
public class EbeguParameterResource {

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Create a new or update an existing E-BEGU parameter with the given key and value",
		response = JaxEbeguParameter.class,
		consumes = MediaType.APPLICATION_JSON)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEbeguParameter (
		@Nonnull @NotNull @Valid JaxEbeguParameter jaxEbeguParameter,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		EbeguParameter ebeguParameter;
		if (jaxEbeguParameter.getId() != null) {
			Optional<EbeguParameter> optional = ebeguParameterService.findEbeguParameter(jaxEbeguParameter.getId());
			ebeguParameter = optional.orElse(new EbeguParameter());
		} else {
			ebeguParameter = new EbeguParameter();
		}
		EbeguParameter convertedEbeguParameter = converter.ebeguParameterToEntity(jaxEbeguParameter, ebeguParameter);
		EbeguParameter persistedEbeguParameter = ebeguParameterService.saveEbeguParameter(convertedEbeguParameter);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EbeguParameterResource.class)
			.path("/" + persistedEbeguParameter.getName())
			.build();
		return Response.created(uri).entity(converter.ebeguParameterToJAX(persistedEbeguParameter)).build();
	}


	@ApiOperation(value = "Find a E-BEGU parameter by its unique name",
		response = JaxEbeguParameter.class)
	@Nullable
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public JaxEbeguParameter findEbeguParameter (
		@Nonnull @NotNull @PathParam("id") JaxId id) throws EbeguException {

		Validate.notNull(id.getId());
		String parameterId = converter.toEntityId(id);
		Optional<EbeguParameter> optional = ebeguParameterService.findEbeguParameter(parameterId);
		if (!optional.isPresent()) {
			return null;
		}
		return converter.ebeguParameterToJAX(optional.get());
	}

	@ApiOperation(value = "Get all E-BEGU parameter")
	@Nonnull
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxEbeguParameter> getAllEbeguParameter() {
		return ebeguParameterService.getAllEbeguParameter().stream()
			.map(param -> converter.ebeguParameterToJAX(param))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Get all E-BEGU parameter that are valid at a certain date")
	@Nonnull
	@GET
	@Path("/date")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxEbeguParameter> getAllEbeguParameterByDate(
		@Nullable @QueryParam("date") String stringDate) {

		LocalDate date = LocalDate.now();
		if (stringDate != null && !stringDate.isEmpty()) {
			date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		return ebeguParameterService.getAllEbeguParameterByDate(date).stream()
			.map(ebeguParameter -> converter.ebeguParameterToJAX(ebeguParameter))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Get all E-BEGU parameter for a specific Gesuchsperiode. The id of the gesuchsperiode is passed  as a pathParam")
	@Nonnull
	@GET
	@Path("/gesuchsperiode/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxEbeguParameter> getEbeguParameterByGesuchsperiode (
		@Nonnull @NotNull @PathParam("id") JaxId id) {

		Validate.notNull(id.getId());
		String gesuchsperiodeId = converter.toEntityId(id);
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId);
		if (gesuchsperiode.isPresent()) {
			return ebeguParameterService.getEbeguParameterByGesuchsperiode(gesuchsperiode.get()).stream()
				.map(ebeguParameter -> converter.ebeguParameterToJAX(ebeguParameter))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@ApiOperation(value = "Get all E-BEGU for a year")
	@Nonnull
	@GET
	@Path("/year/{year}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxEbeguParameter> getEbeguParameterByJahr (
		@Nonnull @NotNull @PathParam("year") Integer jahr) {

		return ebeguParameterService.getEbeguParameterByJahr(jahr).stream()
			.map(ebeguParameter -> converter.ebeguParameterToJAX(ebeguParameter))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Get all E-BEGU parameter by key and date")
	@Nullable
	@GET
	@Path("/name/{name}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxEbeguParameter getEbeguParameterByKeyAndDate (
		@Nonnull @PathParam("name") String name,
		@Nullable @QueryParam("date") String stringDate) {

		LocalDate date = LocalDate.now();
		if (stringDate != null && !stringDate.isEmpty()) {
			date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		EbeguParameterKey ebeguParameterKey = EbeguParameterKey.valueOf(name);
		Optional<EbeguParameter> optional  = ebeguParameterService.getEbeguParameterByKeyAndDate(ebeguParameterKey, date);
		if (optional.isPresent()) {
			return converter.ebeguParameterToJAX(optional.get());
		}
		return null;
	}
}
