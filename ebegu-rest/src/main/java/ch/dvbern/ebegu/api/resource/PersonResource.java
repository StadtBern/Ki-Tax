package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxPerson;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AdresseService;
import ch.dvbern.ebegu.services.PersonService;
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
import java.util.Optional;

/**
 * REST Resource fuer Personen
 */
@Path("personen")
@Stateless
@Api
public class PersonResource {

	@Inject
	private PersonService personService;

	@Inject
	private AdresseService adresseService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Create a new Person in the database. The transfer object also has a relation to adressen " +
		"(wohnadresse, umzugadresse, korrespondenzadresse) these are stored in the database as well. Note that wohnadresse and" +
		"umzugadresse are both stored as consecutive wohnadressen in the database")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxPerson create(
		@Nonnull @NotNull @Valid JaxPerson personJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Person convertedPerson = converter.personToEntity(personJAXP, new Person());
		Person persistedPerson = this.personService.createPerson(convertedPerson);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(PersonResource.class)
			.path("/" + persistedPerson.getId())
			.build();

		JaxPerson jaxPerson = converter.personToJAX(persistedPerson);

		//Korrespondenzadr wird atm immer updated
		if (personJAXP.getAlternativeAdresse() != null) {

			Adresse korrespondenzAdresseToPersist = converter.adresseToEntity(personJAXP.getAlternativeAdresse(), new Adresse());
			korrespondenzAdresseToPersist.setPerson(persistedPerson);
			Adresse persistedKorrespondenzadr = adresseService.createAdresse(korrespondenzAdresseToPersist);
			jaxPerson.setAlternativeAdresse(converter.adresseToJAX(persistedKorrespondenzadr));
		}

		//Umzug
		if (personJAXP.getUmzugAdresse() != null) {
			Adresse umzugAdrToPersist = converter.adresseToEntity(personJAXP.getUmzugAdresse(), new Adresse());
			umzugAdrToPersist.setPerson(persistedPerson);
			Adresse persistedUmzugAdr = adresseService.createAdresse(umzugAdrToPersist);
			jaxPerson.setUmzugAdresse(converter.adresseToJAX(persistedUmzugAdr));
			Adresse wohnAdrToPersist = converter.adresseToEntity(personJAXP.getWohnAdresse(), new Adresse());
			wohnAdrToPersist.setGueltigBis(persistedUmzugAdr.getGueltigAb().minusDays(1));
			wohnAdrToPersist.setPerson(persistedPerson);
			jaxPerson.setWohnAdresse(converter.adresseToJAX(adresseService.createAdresse(wohnAdrToPersist)));

		} else {
			//Wohnadresse konvertieren und speichern
			Adresse wohnAdrToPersist = converter.adresseToEntity(personJAXP.getWohnAdresse(), new Adresse());
			wohnAdrToPersist.setPerson(persistedPerson);
			Adresse persistedwohnAdr = adresseService.createAdresse(wohnAdrToPersist);
			jaxPerson.setWohnAdresse(converter.adresseToJAX(persistedwohnAdr));
		}

//		return Response.created(uri).entity(jaxPerson).build();
		return jaxPerson;

	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxPerson update(
		@Nonnull @NotNull @Valid JaxPerson personJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(personJAXP.getId());
		String personID = converter.toEntityId(personJAXP);
		Optional<Person> optional = personService.findPerson(converter.toEntityId(personJAXP));
		Person personFromDB = optional.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, personJAXP.getId().toString()));
		Person personToMerge = converter.personToEntity(personJAXP, personFromDB);


		Person modifiedPerson = this.personService.updatePerson(personToMerge);
		JaxPerson jaxPerson = converter.personToJAX(modifiedPerson);

		//Korrespondenzadr wird atm immer updated
		if (personJAXP.getAlternativeAdresse() != null) {
			Adresse currentKorrespAdrToUpdate = adresseService.getKorrespondenzAdr(modifiedPerson.getId()).orElse(new Adresse());
			currentKorrespAdrToUpdate = converter.adresseToEntity(personJAXP.getAlternativeAdresse(), currentKorrespAdrToUpdate);
			jaxPerson.setAlternativeAdresse(converter.adresseToJAX(adresseService.updateAdresse(currentKorrespAdrToUpdate)));
		}

		Adresse currentWohnadresseFromDB = adresseService.getCurrentWohnadresse(modifiedPerson.getId());
		Adresse wohnadresseToMerge = converter.adresseToEntity(personJAXP.getWohnAdresse(), currentWohnadresseFromDB);

		//Wenn ein Umzug angegeben wurde
		if (personJAXP.getUmzugAdresse() != null) {
			Adresse umzugAdresseFromDB = adresseService.getNewestWohnadresse(personID).orElse(new Adresse());
			Adresse updateAdresseToMerge = converter.adresseToEntity(personJAXP.getUmzugAdresse(), umzugAdresseFromDB);
			Validate.notNull(updateAdresseToMerge.getGueltigAb(), "gueltigAb muss fuer Umzugadresse gesetzt sein");
			wohnadresseToMerge.setGueltigBis(updateAdresseToMerge.getGueltigAb().minusDays(1));
			jaxPerson.setUmzugAdresse(converter.adresseToJAX(adresseService.updateAdresse(updateAdresseToMerge)));
		}

		jaxPerson.setWohnAdresse(converter.adresseToJAX(adresseService.updateAdresse(wohnadresseToMerge)));

		return jaxPerson;
	}


	@Nullable
	@GET
	@Path("/{personId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxPerson findPerson(
		@Nonnull @NotNull JaxId personJAXPId) throws EbeguException {

		Validate.notNull(personJAXPId.getId());
		String personID = converter.toEntityId(personJAXPId);
		Optional<Person> optional = personService.findPerson(personID);

		if (!optional.isPresent()) {
			return null;
		}
		Person personToReturn = optional.get();

		JaxPerson jaxPerson = converter.personToJAX(personToReturn);
		//adressen anhaengen
		Optional<Adresse> korrespondenzAdr = adresseService.getKorrespondenzAdr(personID);

		if (korrespondenzAdr.isPresent()) {
			jaxPerson.setAlternativeAdresse(converter.adresseToJAX(korrespondenzAdr.get()));
		}

		Adresse currentWohnadresse = adresseService.getCurrentWohnadresse(personID);
		Adresse umzugAdresse = adresseService.getNewestWohnadresse(personID).orElse(null);

		//wenn beide gleich sind gibt es keine Umzugadresse
		if (currentWohnadresse.equals(umzugAdresse)) {
		    jaxPerson.setWohnAdresse(converter.adresseToJAX(currentWohnadresse));
			jaxPerson.setUmzugAdresse(null);

		} else {
			jaxPerson.setWohnAdresse(converter.adresseToJAX(currentWohnadresse));
			jaxPerson.setUmzugAdresse(converter.adresseToJAX(umzugAdresse));
		}
		return jaxPerson;
	}

}
