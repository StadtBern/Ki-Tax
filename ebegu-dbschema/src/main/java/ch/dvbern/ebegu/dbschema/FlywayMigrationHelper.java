package ch.dvbern.ebegu.dbschema;

import ch.dvbern.ebegu.enums.UserRoleName;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.CDI;
import java.util.function.Consumer;

/**
 * Unterstuetz die Java basierte Migration von Datenbestaenden.
 *
 * Es ist wichtig pro FlyWay Migration nur einen Call von {@link #migrate} zu verwenden,
 * da die Migration sonst gegebenenfalls nicht ein komplettes Rollback unterstuetzt.
 */
@Stateless
@RunAs(UserRoleName.SUPER_ADMIN)
@PermitAll
public class FlywayMigrationHelper {

	@Resource
	private SessionContext ctx;

	/**
	 * Wenn diese Methode ausserhalb eines EJB Contexts ausgefuehrt wird, was bei der Migration von FlyWay der Fall ist,
	 * so wird die @RunAs(SUPER_ADMIN) Annotation ignoriert. Diese Methode holt sich deshalb via SessionContext eine
	 * Instanz des FlywayMigrationHelper, welcher dann die korrekte Rolle erhaelt.
	 *
	 * Mit CDI<Object>.select(MyService.class) koennen Services, welche fuer die Migration benoetigt werden injected werden.
	 *
	 * @param consumer die Funktion, welche die Migrierung durchfuert
	 */
	public void migrate(@Nonnull Consumer<CDI<Object>> consumer) {
		ctx.getBusinessObject(FlywayMigrationHelper.class).migrateInternal(consumer);
	}

	// Muss leider public sein, sollte aber nicht verwendet werden
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void migrateInternal(@Nonnull Consumer<CDI<Object>> consumer) {
		consumer.accept(CDI.current());
	}
}
