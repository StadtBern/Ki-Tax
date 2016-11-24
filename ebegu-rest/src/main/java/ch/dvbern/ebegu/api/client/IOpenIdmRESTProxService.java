/*
 * Copyright (c) 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.api.client;

import io.swagger.jaxrs.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dies das proxy interface fuer den HTTP Endpunkt des sms gateways
 */
public interface IOpenIdmRESTProxService {


	@GET
	@Path("/openidm/info/login")
	@Consumes(MediaType.APPLICATION_JSON)
	Response login(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession);

	@GET
	@Path("/openidm/managed/institution")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getAllInstitutions(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession,
		@QueryParam("_queryFilter") boolean queryFilter);

	@GET
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getInstitutionbyUid(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession,
		@PathParam("uid") String uid);

	@PUT
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response create(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@PathParam("uid") String uid,
		JaxInstitutionOpenIdm jaxInstitutionOpenIdm);

	@PATCH
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_FORM_URLENCODED)
	Response update(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@PathParam("uid") String uid,
		JaxUpdateOpenIdm jaxUpdateOpenIdm);

	@DELETE
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response delete(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@PathParam("uid") String uid);
}

