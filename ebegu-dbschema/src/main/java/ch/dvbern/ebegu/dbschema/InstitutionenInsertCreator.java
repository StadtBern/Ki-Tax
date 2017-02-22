package ch.dvbern.ebegu.dbschema;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.util.MathUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Liest die Liste der Institutionen (Excel) ein
 * Info:
 * Es gibt Institutionen mit mehreren Angeboten. Teilweise sollen sich diese nicht sehen können; In diesen Faellen machen
 * wir zwei Institutionen daraus. Im Excel muss dazu die Spalte Institutions-Id leer bleiben, bzw. dort wo für mehrere
 * Angebote die gleiche InstitutionsId drinn steht, werden die Angebote als InstitutiosStammdaten importiert.
 */
@SuppressWarnings({"CallToPrintStackTrace", "IOResourceOpenedButNotSafelyClosed", "UseOfSystemOutOrSystemErr", "TooBroadScope", "PMD.AvoidDuplicateLiterals", "StringBufferReplaceableByString"})
public class InstitutionenInsertCreator {

	private static final Logger LOG = LoggerFactory.getLogger(InstitutionenInsertCreator.class);

	public static final String MANDANT_ID_BERN = "e3736eb8-6eef-40ef-9e52-96ab48d8f220";

	public static final int COL_TRAEGERSCHAFT_ID = 0;
	public static final int COL_TRAEGERSCHAFT_NAME = 1;
	public static final int COL_TRAEGERSCHAFT_MAIL = 2;
	public static final int COL_INSTITUTION_ID = 3;
	public static final int COL_INSTITUTION_NAME = 4;
	public static final int COL_STRASSE = 5;
	public static final int COL_HAUSNUMMER = 6;
	public static final int COL_PLZ = 7;
	public static final int COL_ORT = 8;
	public static final int COL_ZUSATZZEILE = 9;
	public static final int COL_INSTITUTION_MAIL = 10;
	public static final int COL_ANGEBOT = 11;
	public static final int COL_IBAN = 12;
	public static final int COL_OEFFNUNGSSTUNDEN = 13;
	public static final int COL_OEFFNUNGSTAGE = 14;

	private Map<String, String> traegerschaftenMap = new HashMap<>();
	private Map<String, String> institutionenMap = new HashMap<>();

	private List<String> insertTraegerschaften = new LinkedList<>();
	private List<String> insertAdressen = new LinkedList<>();
	private List<String> insertInstitutionen = new LinkedList<>();
	private List<String> insertInstitutionsStammdaten = new LinkedList<>();

	private PrintWriter printWriter;
	private String inputFile = "/institutionen/Institutionen_2017.01.13.xlsx";
	private int anzahlZeilen = 87;
	private String outoutFile = "insertInstitutionen.sql";


	public static void main(String[] args) {
		InstitutionenInsertCreator creator = new InstitutionenInsertCreator();
		try {
			creator.readExcel();
		} catch (IOException e) {
			LOG.error("Fehler beim Einlesen", e);
		}
	}

	private void readExcel() throws IOException {
		InputStream resourceAsStream = InstitutionenInsertCreator.class.getResourceAsStream(inputFile);
		XSSFWorkbook myWorkBook = new XSSFWorkbook(resourceAsStream);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet.iterator();
		rowIterator.next(); // Titelzeile
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			readRow(row);
		}
		for (String s : insertTraegerschaften) {
			println(s);
		}
		for (String s : insertAdressen) {
			println(s);
		}
		for (String s : insertInstitutionen) {
			println(s);
		}
		for (String s : insertInstitutionsStammdaten) {
			println(s);
		}
		printWriter.flush();
		printWriter.close();
	}

	@SuppressWarnings("OverlyComplexMethod")
	private void readRow(Row row) {
		if (row.getRowNum() > anzahlZeilen) {
			return;
		}
		// Traegerschaften
		String traegerschaftKey = readString(row, COL_TRAEGERSCHAFT_ID);
		String traegerschaftsId = null;
		if (StringUtils.isNotEmpty(traegerschaftKey)) {
			if (traegerschaftenMap.containsKey(traegerschaftKey)) {
				traegerschaftsId = traegerschaftenMap.get(traegerschaftKey);
			} else {
				traegerschaftsId = writeTraegerschaft(row);
				traegerschaftenMap.put(traegerschaftKey, traegerschaftsId);
			}

			if (traegerschaftsId == null) {
				LOG.error("TraegerschaftsId ist null, breche ab. " + row.getRowNum());
				return;
			}
		}

		// Institutionen
		String institutionsKey = readString(row, COL_INSTITUTION_ID);
		String institutionsId;
		if (StringUtils.isNotEmpty(institutionsKey) && institutionenMap.containsKey(institutionsKey)) {
			institutionsId = institutionenMap.get(institutionsKey);
		} else {
			institutionsId = writeInstitution(row, traegerschaftsId);
			institutionenMap.put(institutionsKey, institutionsId);
		}
		if (institutionsId == null) {
			LOG.error("institutionsId ist null, breche ab. " + row.getRowNum());
			return;
		}
		// Adressen
		String adresseId = writeAdresse(row);
		if (adresseId == null) {
			LOG.error("adresseId ist null, breche ab. " + row.getRowNum());
			return;
		}
		// Institutionsstammdaten
		String angebot = readString(row, COL_ANGEBOT);
		if (angebot == null) {
			LOG.error("angebot is null: " + row.getRowNum());
			return;
		}
		try {
			BetreuungsangebotTyp betreuungsangebotTyp = BetreuungsangebotTyp.valueOf(angebot);
			writeInstitutionStammdaten(row, institutionsId, adresseId, betreuungsangebotTyp);
		} catch (IllegalArgumentException iae) {
			if ("TAGESELTERN".equalsIgnoreCase(angebot)) {
				// Tageseltern muessen fuer Schulkinder und Kleinkinder erstellt werden!
				writeInstitutionStammdaten(row, institutionsId, adresseId, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
				writeInstitutionStammdaten(row, institutionsId, adresseId, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);
			} else {
				LOG.warn("Unbekannter Betreuungsangebot-Typ: " + angebot);
			}
		}
	}

	private String readString(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell != null) {
			cell.setCellType(CellType.STRING);
			return cell.getStringCellValue();
		} else {
			return null;
		}
	}

	private String readDouble(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell != null) {
			cell.setCellType(CellType.NUMERIC);
			return Double.toString(cell.getNumericCellValue());
		} else {
			return null;
		}
	}

	private String writeAdresse(Row row) {
		String id = UUID.randomUUID().toString();
		String strasse = readString(row, COL_STRASSE);
		String hausnummer = readString(row, COL_HAUSNUMMER);
		String plz = readString(row, COL_PLZ);
		String ort = readString(row, COL_ORT);
		String zusatzzeile = readString(row, COL_ZUSATZZEILE);

		if (strasse == null) {
			LOG.error("strasse is null: " + row.getRowNum());
			return null;
		}
		if (plz == null) {
			LOG.error("plz is null: " + row.getRowNum());
			return null;
		}
		if (ort == null) {
			LOG.error("ort is null: " + row.getRowNum());
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO adresse ");
		sb.append("(id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile) ");
		sb.append("VALUES (");
		sb.append("'").append(id).append("', ");	// id
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_erstellt
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_mutiert
		sb.append("'flyway', "); 					// user_erstellt
		sb.append("'flyway', "); 					// user_mutiert
		sb.append("0, ");							// version,
		sb.append("null, ");						// gemeinde,
		sb.append("'1000-01-01', "); 				// gueltig_ab,
		sb.append("'9999-12-31', "); 				// gueltig_bis,
		sb.append(toStringOrNull(hausnummer)).append(", "); // hausnummer
		sb.append("'CH', ");						// land,
		sb.append(toStringOrNull(ort)).append(", "); // ort
		sb.append(toStringOrNull(plz)).append(", "); // plz
		sb.append(toStringOrNull(strasse)).append(", "); // strasse
		sb.append(toStringOrNull(zusatzzeile)); 	// zusatzzeile
		sb.append(");");
		insertAdressen.add(sb.toString());

		return id;
	}

	private String writeTraegerschaft(Row row) {
		String id = UUID.randomUUID().toString();
		String traegerschaftsname = readString(row, COL_TRAEGERSCHAFT_NAME);
		String traegerschaftEmail = readString(row, COL_TRAEGERSCHAFT_MAIL);

		if (traegerschaftsname == null) {
			LOG.error("institutionsname is null: " + row.getRowNum());
			return null;
		}
		if (traegerschaftEmail == null) {
			LOG.error("traegerschaftEmail is null: " + row.getRowNum());
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO traegerschaft ");
		sb.append("(id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, name, active, mail) ");
		sb.append("VALUES (");
		sb.append("'").append(id).append("', ");	// id
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_erstellt
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_mutiert
		sb.append("'flyway', "); 					// user_erstellt
		sb.append("'flyway', "); 					// user_mutiert
		sb.append("0, ");							// version,
		sb.append(toStringOrNull(traegerschaftsname)).append(", "); // name
		sb.append("true, ");				 				// active
		sb.append(toStringOrNull(traegerschaftEmail));  // mail
		sb.append(");");
		insertTraegerschaften.add(sb.toString());

		return id;
	}

	private String writeInstitution(Row row, String traegerschaftId) {
		String id = UUID.randomUUID().toString();
		String institutionsname = readString(row, COL_INSTITUTION_NAME);
		String institutionsEmail = readString(row, COL_INSTITUTION_MAIL);

		if (institutionsname == null) {
			LOG.error("institutionsname is null: " + row.getRowNum());
			return null;
		}
		if (institutionsEmail == null) {
			LOG.error("institutionsEmail is null: " + row.getRowNum());
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO institution ");
		sb.append("(id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, name, mandant_id, traegerschaft_id, active, mail) ");
		sb.append("VALUES (");
		sb.append("'").append(id).append("', ");	// id
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_erstellt
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_mutiert
		sb.append("'flyway', "); 					// user_erstellt
		sb.append("'flyway', "); 					// user_mutiert
		sb.append("0, ");					        // version,
		sb.append(toStringOrNull(institutionsname)).append(", "); // name
		sb.append("'").append(MANDANT_ID_BERN).append("', ");    // mandant_id,
		sb.append(toStringOrNull(traegerschaftId)).append(", "); // name
		sb.append("true, "); // active
		sb.append(toStringOrNull(institutionsEmail)); // mail
		sb.append(");");
		insertInstitutionen.add(sb.toString());

		return id;
	}

	private String writeInstitutionStammdaten(Row row, String institutionsId, String adresseId, BetreuungsangebotTyp typ) {
		if (institutionsId == null) {
			LOG.error("institutionsId is null: " + row.getRowNum());
			return null;
		}
		if (adresseId == null) {
			LOG.error("adresseId is null: " + row.getRowNum());
			return null;
		}

		// INSERT INTO institution_stammdaten (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, betreuungsangebot_typ, iban, oeffnungsstunden, oeffnungstage, institution_id, adresse_id) VALUES ('11111111-1111-1111-1111-111111111101', '2016-07-26 00:00:00', '2016-07-26 00:00:00', 'flyway', 'flyway', 0, '1000-01-01', '9999-12-31', 'KITA', null, 11.50, 240.00, '11111111-1111-1111-1111-111111111101', '11111111-1111-1111-1111-111111111101');
		String id = UUID.randomUUID().toString();
		String iban = readString(row, COL_IBAN);
		String stunden = readDouble(row, COL_OEFFNUNGSSTUNDEN);
		String tage = readDouble(row, COL_OEFFNUNGSTAGE);

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO institution_stammdaten ");
		sb.append("(id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, betreuungsangebot_typ, iban, oeffnungsstunden, oeffnungstage, institution_id, adresse_id) ");
		sb.append("VALUES (");
		sb.append("'").append(id).append("', ");	// id
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_erstellt
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_mutiert
		sb.append("'flyway', "); 					// user_erstellt
		sb.append("'flyway', "); 					// user_mutiert
		sb.append("0, ");					// version,
		sb.append("'1000-01-01', "); 				// gueltig_ab,
		sb.append("'9999-12-31', "); 				// gueltig_bis,
		sb.append("'").append(typ.name()).append("', "); // betreuungsangebot_typ,
		sb.append(toStringOrNull(iban)).append(", "); // iban
		sb.append(toBigDecimalOrNull(stunden)).append(", "); // oeffnungsstunden,
		sb.append(toBigDecimalOrNull(tage)).append(", "); // oeffnungstage,
		sb.append(toStringOrNull(institutionsId)).append(", "); // institution_id
		sb.append(toStringOrNull(adresseId)); // adresse_id
		sb.append(");");
		insertInstitutionsStammdaten.add(sb.toString());

		return id;
	}

	private String toStringOrNull(String aStringOrNull) {
		if (aStringOrNull == null) {
			return "null";
		} else {
			return "'" + aStringOrNull + "'";
		}
	}

	private String toBigDecimalOrNull(String aStringOrNull) {
		if (aStringOrNull == null) {
			return "null";
		} else {
			// Mit 2 Nachkommastellen
			BigDecimal from = MathUtil.DEFAULT.from(new BigDecimal(aStringOrNull));
			if (from != null) {
				return from.toString();
			}
			return "null";
		}
	}

	private PrintWriter getPrintWriter() {
		if (printWriter == null) {
			try {
				File output = new File(outoutFile);
				FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
				printWriter = new PrintWriter(fos);
				LOG.info("File generiert: " + output.getAbsolutePath());
			} catch (FileNotFoundException e) {
				LOG.error("Konnte Outputfile nicht erstellen", e);
			}
		}
		return printWriter;
	}

	private void println(String s)  {
		getPrintWriter().println(s);
	}
}
