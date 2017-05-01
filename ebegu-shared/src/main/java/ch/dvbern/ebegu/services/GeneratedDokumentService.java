package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;

import javax.activation.MimeTypeParseException;
import java.io.IOException;

/**
 * Service zum Verwalten von GeneratedDokumenten
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public interface GeneratedDokumentService {


	GeneratedDokument getFinSitDokumentAccessTokenGeneratedDokument(Gesuch gesuch,
																	Boolean forceCreation) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getBegleitschreibenDokument(Gesuch gesuch,
												  Boolean forceCreation) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getFreigabequittungAccessTokenGeneratedDokument(Gesuch gesuch,
																	  Boolean forceCreation, Zustelladresse zustelladresse) throws MimeTypeParseException, MergeDocException;

	GeneratedDokument getVerfuegungDokumentAccessTokenGeneratedDokument(Gesuch gesuch, Betreuung betreuung, String manuelleBemerkungen,
																		Boolean forceCreation) throws MimeTypeParseException, MergeDocException, IOException;

	GeneratedDokument getMahnungDokumentAccessTokenGeneratedDokument(Mahnung mahnung,
																	 Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException;

	GeneratedDokument getNichteintretenDokumentAccessTokenGeneratedDokument(Betreuung betreuung,
																			Boolean forceCreation) throws MimeTypeParseException, IOException, MergeDocException;

	void removeAllGeneratedDokumenteFromGesuch(Gesuch gesuch);

}
