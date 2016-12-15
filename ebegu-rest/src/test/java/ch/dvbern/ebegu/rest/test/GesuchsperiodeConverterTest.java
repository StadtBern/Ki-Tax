package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * Tests der die Konvertierung von Gesuchsperiode prueft
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeConverterTest extends AbstractEbeguRestLoginTest {


	@Inject
	private Persistence<Gesuchsperiode> persistence;

	@Inject
	private JaxBConverter converter;


	/**
	 * transformiert eine gespeicherte Gesuchsperiode nach jax und wieder zurueck. wir erwarten dass Daten gleich bleiben
	 */
	@Test
	public void convertPersistedTestEntityToJax(){
		LocalDate date = LocalDate.now();
		Gesuchsperiode gesuchsperiode = createNewEntity(date, date);
		JaxGesuchsperiode jaxGesuchsperiode = this.converter.gesuchsperiodeToJAX(gesuchsperiode);
		Gesuchsperiode gesuchsperiodeToEntity = this.converter.gesuchsperiodeToEntity(jaxGesuchsperiode, new Gesuchsperiode());
		Assert.assertTrue(gesuchsperiode.isSame(gesuchsperiodeToEntity));
	}

	// HELP METHODS

	private Gesuchsperiode createNewEntity(LocalDate ab, LocalDate bis) {
		Gesuchsperiode gesuchsperiode = new Gesuchsperiode();
		gesuchsperiode.setGueltigkeit(new DateRange(ab, bis));
		gesuchsperiode.setActive(true);
		return gesuchsperiode;
	}

}
