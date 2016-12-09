package ch.dvbern.ebegu.dbschema;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.util.MathUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Liest die Liste der Institutionen (Excel) ein
 */
@SuppressWarnings({"CallToPrintStackTrace", "IOResourceOpenedButNotSafelyClosed", "UseOfSystemOutOrSystemErr"})
public class InstitutionenInsertCreator {

	private static final String MANDANT_ID_BERN = "e3736eb8-6eef-40ef-9e52-96ab48d8f220";

	private Map<String, String> traegerschaftenMap = new HashMap<>();
	private Map<String, String> institutionenMap = new HashMap<>();

	private PrintWriter printWriter;


	public static void main(String[] args) {
		InstitutionenInsertCreator creator = new InstitutionenInsertCreator();
		try {
			creator.readExcel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readExcel() throws IOException {
		InputStream resourceAsStream = InstitutionenInsertCreator.class.getResourceAsStream("/institutionen/Institutionen_2016.12.08.xlsx");
		XSSFWorkbook myWorkBook = new XSSFWorkbook(resourceAsStream);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet.iterator();
		rowIterator.next(); // Titelzeile
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			readRow(row);
		}
	}

	private void readRow(Row row) {
		// Traegerschaften
		String traegerschaftKey = readString(row, 0);
		String traegerschaftsId = null;
		if (StringUtils.isNotEmpty(traegerschaftKey)) {
			if (traegerschaftenMap.containsKey(traegerschaftKey)) {
				traegerschaftsId = traegerschaftenMap.get(traegerschaftKey);
			} else {
				traegerschaftsId = writeTraegerschaft(row);
				traegerschaftenMap.put(traegerschaftKey, traegerschaftsId);
			}
		}
		// Institutionen
		String institutionsKey = readString(row, 3);
		String institutionsId = null;
		if (StringUtils.isNotEmpty(institutionsKey)) {
			if (institutionenMap.containsKey(institutionsKey)) {
				institutionsId = institutionenMap.get(institutionsKey);
			} else {
				institutionsId = writeInstitution(row, traegerschaftsId);
				institutionenMap.put(institutionsKey, institutionsId);
			}
		}
		// Adressen
		String adresseId = writeAdresse(row);
		// Institutionsstammdaten
		String angebot = readString(row, 11);
		BetreuungsangebotTyp betreuungsangebotTyp = null;
		try {
			betreuungsangebotTyp = BetreuungsangebotTyp.valueOf(angebot);
			writeInstitutionStammdaten(row, institutionsId, adresseId, betreuungsangebotTyp);
		} catch (IllegalArgumentException iae) {
			if ("TAGESELTERN".equalsIgnoreCase(angebot)) {
				// Tageseltern muessen fuer Schulkinder und Kleinkinder erstellt werden!
				writeInstitutionStammdaten(row, institutionsId, adresseId, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
				writeInstitutionStammdaten(row, institutionsId, adresseId, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);
			} else {
				System.out.println("Unbekannter Betreuungsangebot-Typ: " + angebot);
			}
		}
	}

	private String readString(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell != null) {
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					return cell.getStringCellValue();
				case Cell.CELL_TYPE_NUMERIC:
					return Double.toString(cell.getNumericCellValue());
				default:
					System.out.println("Typ nicht definiert: " + cell.getCellType() + " " + row.getRowNum() + "/" + columnIndex);
					return null;
			}
		} else {
			return null;
		}
	}

	private Integer readInt(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell != null) {
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					return Integer.parseInt(cell.getStringCellValue());
				case Cell.CELL_TYPE_NUMERIC:
					return Double.valueOf(cell.getNumericCellValue()).intValue();
				default:
					throw new IllegalArgumentException("Typ nicht definiert");
			}
		} else {
			return null;
		}
	}

	private double readDouble(Row row, int columnIndex) {
		return row.getCell(columnIndex).getNumericCellValue();
	}

	private String writeAdresse(Row row) {
		String id = UUID.randomUUID().toString();
		String strasse = readString(row, 5);
		String hausnummer = readString(row, 6);
		String plz = readString(row, 7);
		String ort = readString(row, 8);
		String zusatzzeile = readString(row, 9);

		if (strasse == null) {
			System.out.println("strasse is null: " + row.getRowNum());
		}
		if (plz == null) {
			System.out.println("plz is null: " + row.getRowNum());
		}
		if (ort == null) {
			System.out.println("ort is null: " + row.getRowNum());
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
		println(sb.toString());

		return id;
	}

	private String writeTraegerschaft(Row row) {
		String id = UUID.randomUUID().toString();
		String traegerschaftsname = readString(row, 1);
		String traegerschaftEmail = readString(row, 2);

		if (traegerschaftsname == null) {
			System.out.println("institutionsname is null: " + row.getRowNum());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO traegerschaft ");
		sb.append("(id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, name, active) ");
		sb.append("VALUES (");
		sb.append("'").append(id).append("', ");	// id
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_erstellt
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_mutiert
		sb.append("'flyway', "); 					// user_erstellt
		sb.append("'flyway', "); 					// user_mutiert
		sb.append("0, ");							// version,
		sb.append(toStringOrNull(traegerschaftsname)).append(", "); // name
		sb.append("0");				 				// active
		sb.append(");");
		println(sb.toString());

		return id;
	}

	private String writeInstitution(Row row, String traegerschaftId) {
		String id = UUID.randomUUID().toString();
		String institutionsname = readString(row, 4);

		if (institutionsname == null) {
			System.out.println("institutionsname is null: " + row.getRowNum());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO institution ");
		sb.append("(id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, name, mandant_id, traegerschaft_id, active) ");
		sb.append("VALUES (");
		sb.append("'").append(id).append("', ");	// id
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_erstellt
		sb.append("'2016-01-01 00:00:00', "); 		// timestamp_mutiert
		sb.append("'flyway', "); 					// user_erstellt
		sb.append("'flyway', "); 					// user_mutiert
		sb.append("0, ");					// version,
		sb.append(toStringOrNull(institutionsname)).append(", "); // name
		sb.append("'").append(MANDANT_ID_BERN).append("', "); // mandant_id,
		sb.append(toStringOrNull(traegerschaftId)).append(", "); // name
		sb.append("true"); // active
		sb.append(");");
		println(sb.toString());

		return id;
	}

	private String writeInstitutionStammdaten(Row row, String institutionsId, String adresseId, BetreuungsangebotTyp typ) {
		// INSERT INTO institution_stammdaten (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, betreuungsangebot_typ, iban, oeffnungsstunden, oeffnungstage, institution_id, adresse_id) VALUES ('11111111-1111-1111-1111-111111111101', '2016-07-26 00:00:00', '2016-07-26 00:00:00', 'flyway', 'flyway', 0, '1000-01-01', '9999-12-31', 'KITA', null, 11.50, 240.00, '11111111-1111-1111-1111-111111111101', '11111111-1111-1111-1111-111111111101');
		String id = UUID.randomUUID().toString();
		String iban = readString(row, 12);
		String stunden = readString(row, 13);
		String tage = readString(row, 14);
		String email = readString(row, 10);

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
		println(sb.toString());

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
				FileOutputStream fos = new FileOutputStream("/media/M/hefr/output.txt");
				printWriter = new PrintWriter(fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		return printWriter;
	}

	private void println(String s)  {
		getPrintWriter().println(s);
	}
}
