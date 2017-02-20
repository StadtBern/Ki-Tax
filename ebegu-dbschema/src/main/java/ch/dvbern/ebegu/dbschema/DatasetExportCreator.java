package ch.dvbern.ebegu.dbschema;

import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

/**
 * Generiert ein XML-File als Dataset für DBUnit-Tests.
 * Die Files werden aktuell im BIN-Ordner des JBoss generiert, beim Starten des JBoss
 * full.xml: Enthaelt einen kompletten Dump der Datenbank
 * partial.xml: Enthaelt einen Dump *ohne*:
 * - _aud-Tabellen
 * -
 */
@Startup
@Singleton
@SuppressWarnings("PMD")
public class DatasetExportCreator {

	private static final String DATASOURCE_NAME = "jdbc/ebegu";

	private static final String[] EXCEPTIONS = new String[]{"_aud", "revinfo", "schema_version"};

	@PersistenceContext(unitName = "ebeguPersistenceUnit")
	EntityManager em;

	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	@PostConstruct
	public void exportForDBUnit() {
		try {
			final DataSource dataSource = InitialContext.doLookup(DATASOURCE_NAME);
			Connection jdbcConnection = dataSource.getConnection();

			IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

			// write DTD file
			FileOutputStream fileOutputStreamDTD = new FileOutputStream("ebegu-testdata.dtd");
			FlatDtdDataSet.write(connection.createDataSet(), fileOutputStreamDTD);

			// partial database export
			QueryDataSet partialDataSet = new QueryDataSet(connection);
			Query query = em.createNativeQuery("show tables;");
			List<String> tableNames = query.getResultList();
			for (String tableName : tableNames) {
				if (!(StringUtils.indexOfAny(tableName, EXCEPTIONS) > 0)) {
					partialDataSet.addTable(tableName);
				}
			}
			FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

			// full database export
			IDataSet fullDataSet = connection.createDataSet();
			FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

			// dependent tables database export: export table X and all tables that
			// have a PK which is a FK on X, in the right order for insertion
	/*        String[] depTableNames =
			  TablesDependencyHelper.getAllDependentTables( connection, "X" );
			IDataSet depDataset = connection.createDataSet( depTableNames );
			FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml"));*/

		} catch (NamingException e) {
			final String msg = ("flyway db migration error (missing datasource '" + DATASOURCE_NAME) + "')";
			throw new RuntimeException(msg, e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
