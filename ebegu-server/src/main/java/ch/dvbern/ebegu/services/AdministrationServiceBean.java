package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.MathUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service fuer diverse Admin-Aufgaben.
 * Im Moment nur fuer internen Gebrauch, d.h. die Methoden werden nirgends im Code aufgerufen, koennen aber bei Bedarf
 * schnell irgendwo angehaengt werden.
 */
@Stateless
@Local(AdministrationService.class)
@RolesAllowed(value = {UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN})
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdministrationServiceBean extends AbstractBaseService implements AdministrationService {


	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	private static final Logger LOG = LoggerFactory.getLogger(AdministrationServiceBean.class);

	private PrintWriter printWriter;
	private String inputFile = "/institutionen/Institutionen_2017.03.01.xlsx";
	private String outputFile = "insertInstitutionen.sql";
	private int anzahlZeilen = 87;

	private List<String> traegerschaftenMap = new LinkedList<>();
	private List<String> institutionenMap = new LinkedList<>();

	private List<String> listTraegerschaften = new LinkedList<>();
	private List<String> listAdressen = new LinkedList<>();
	private List<String> listInstitutionen = new LinkedList<>();
	private List<String> listInstitutionsStammdaten = new LinkedList<>();


	@Override
	public void createSQLSkriptInstutionsstammdaten() {
		try {
			InputStream resourceAsStream = AdministrationServiceBean.class.getResourceAsStream(inputFile);
			XSSFWorkbook myWorkBook = new XSSFWorkbook(resourceAsStream);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			rowIterator.next(); // Titelzeile
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				readRow(row);
			}
			for (String s : listTraegerschaften) {
				println(s);
			}
			for (String s : listAdressen) {
				println(s);
			}
			for (String s : listInstitutionen) {
				println(s);
			}
			for (String s : listInstitutionsStammdaten) {
				println(s);
			}
			printWriter.flush();
			printWriter.close();
		} catch (IOException ioe) {
			LOG.error("Error beim Importieren", ioe);
		}
	}

	@SuppressWarnings("OverlyComplexMethod")
	private void readRow(Row row) {
		if (row.getRowNum() > anzahlZeilen) {
			return;
		}
		// Traegerschaften
		String traegerschaftId = readString(row, AdministrationService.COL_TRAEGERSCHAFT_ID);
		if (StringUtils.isNotEmpty(traegerschaftId) && !traegerschaftenMap.contains(traegerschaftId)) {
			writeTraegerschaft(row, traegerschaftId);
			traegerschaftenMap.add(traegerschaftId);
		}
		// Institutionen
		String institutionsId = readString(row, AdministrationService.COL_INSTITUTION_ID);
		if (StringUtils.isEmpty(institutionsId) || !institutionenMap.contains(institutionsId)) {
			institutionsId = writeInstitution(row, institutionsId, traegerschaftId);
			institutionenMap.add(institutionsId);
		}
		// Stammdaten und Adressen
		String stammdatenId = readString(row, AdministrationService.COL_STAMMDATEN_ID);
		writeInstitutionStammdaten(row, stammdatenId, institutionsId);
	}

	private String writeTraegerschaft(Row row, String traegerschaftId) {
		String traegerschaftsname = readString(row, AdministrationService.COL_TRAEGERSCHAFT_NAME);
		String traegerschaftEmail = readString(row, AdministrationService.COL_TRAEGERSCHAFT_MAIL);
		if (StringUtils.isNotEmpty(traegerschaftsname) && StringUtils.isNotEmpty(traegerschaftEmail)) {
			// Es gibt eine Traegerschaft
			if (StringUtils.isNotEmpty(traegerschaftId)) {
				Optional<Traegerschaft> traegerschaftOptional = traegerschaftService.findTraegerschaft(traegerschaftId);
				if (traegerschaftOptional.isPresent()) {
					// Traegerschaft ist schon bekannt -> updaten
					listTraegerschaften.add(updateTraegerschaft(traegerschaftId, traegerschaftsname, traegerschaftEmail));

				} else {
					throw new IllegalStateException("Traegerschaft nicht gefunden!");
				}
			} else {
				// dies ist unmoeglich <- wenn ein traegerschaft ohne ID kommt wird einfach nichts gemacht
				// Traegerschaft ist neu
				traegerschaftId = UUID.randomUUID().toString();
				listTraegerschaften.add(insertTraegerschaft(traegerschaftId, traegerschaftsname, traegerschaftEmail));
			}
		} else {
			throw new IllegalStateException("Traegerschaftdaten fehlen");
		}
		return traegerschaftId;
	}

	private String insertTraegerschaft(String id, String traegerschaftsname, String traegerschaftEmail) {
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
		return sb.toString();
	}

	private String updateTraegerschaft(String id, String traegerschaftsname, String traegerschaftEmail) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE traegerschaft set");
		sb.append(" name = ");
		sb.append(toStringOrNull(traegerschaftsname)); // name
		sb.append(", mail = ");
		sb.append(toStringOrNull(traegerschaftEmail));  // mail
		sb.append(" where id = '");
		sb.append(id);	// id
		sb.append("';");
		return sb.toString();
	}

	private String writeInstitution(Row row,  String institutionId, String traegerschaftId) {
		String institutionsname = readString(row, AdministrationService.COL_INSTITUTION_NAME);
		String institutionsEmail = readString(row, AdministrationService.COL_INSTITUTION_MAIL);
		if (StringUtils.isEmpty(institutionsname) || StringUtils.isEmpty(institutionsEmail)) {
			throw new IllegalStateException("Institutionsangaben fehlen");
		}
		// Es gibt eine Institution
		if (StringUtils.isNotEmpty(institutionId)) {
			Optional<Institution> institutionOptional = institutionService.findInstitution(institutionId);
			if (institutionOptional.isPresent()) {
				// Institution ist schon bekannt -> updaten
				listInstitutionen.add(updateInstitution(institutionId, traegerschaftId, institutionsname, institutionsEmail));
			} else {
				throw new IllegalStateException("Institution nicht gefunden!");
			}
		} else {
			// Institution ist neu
			institutionId = UUID.randomUUID().toString();
			listInstitutionen.add(insertInstitution(institutionId, traegerschaftId, institutionsname, institutionsEmail));
		}
		return institutionId;
	}

	private String insertInstitution(String id, String traegerschaftId, String institutionsname, String institutionsEmail) {
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
		return sb.toString();
	}

	private String updateInstitution(String id, String traegerschaftId, String institutionsname, String institutionsEmail) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE institution set");
		sb.append(" name = ");
		sb.append(toStringOrNull(institutionsname)); // name
		sb.append(", mail = ");
		sb.append(toStringOrNull(institutionsEmail));  // mail
		sb.append(", traegerschaft_id = ");
		sb.append(toStringOrNull(traegerschaftId));  // traegerschaft_id
		sb.append(" where id = '");
		sb.append(id);	// id
		sb.append("';");
		return sb.toString();
	}

	private String writeInstitutionStammdaten(Row row, String stammdatenId, String institutionsId) {
		String angebot = readString(row, AdministrationService.COL_ANGEBOT);
		if (StringUtils.isEmpty(angebot)) {
			throw new IllegalStateException("Angebotstyp fehlen");
		}
		BetreuungsangebotTyp typ = BetreuungsangebotTyp.valueOf(angebot);
		String iban = readString(row, AdministrationService.COL_IBAN);
		String stunden = readDouble(row, AdministrationService.COL_OEFFNUNGSSTUNDEN);
		String tage = readDouble(row, AdministrationService.COL_OEFFNUNGSTAGE);

		String strasse = readString(row, AdministrationService.COL_STRASSE);
		String hausnummer = readString(row, AdministrationService.COL_HAUSNUMMER);
		String plz = readString(row, AdministrationService.COL_PLZ);
		String ort = readString(row, AdministrationService.COL_ORT);
		String zusatzzeile = readString(row, AdministrationService.COL_ZUSATZZEILE);
		if (StringUtils.isEmpty(strasse) || StringUtils.isEmpty(plz) || StringUtils.isEmpty(ort)) {
			throw new IllegalStateException("Adressangaben fehlen");
		}

		if (StringUtils.isEmpty(institutionsId)) {
			throw new IllegalStateException("institutionsId is null: " + row.getRowNum());
		}

		if (StringUtils.isNotEmpty(stammdatenId)) {
			Optional<InstitutionStammdaten> stammdatenOptional = institutionStammdatenService.findInstitutionStammdaten(stammdatenId);
			if (stammdatenOptional.isPresent()) {
				// Institution ist schon bekannt -> updaten
				String adresseId = stammdatenOptional.get().getAdresse().getId();
				listAdressen.add(updateAdresse(adresseId, hausnummer, ort, plz, strasse, zusatzzeile));
				listInstitutionsStammdaten.add(updateInstitutionsStammdaten(institutionsId, typ, iban, stunden, tage));
			} else {
				throw new IllegalStateException("InstitutionStammdaten nicht gefunden!");
			}
		} else {
			// Institution ist neu
			String adresseId = UUID.randomUUID().toString();
			listAdressen.add(insertAdresse(adresseId, hausnummer, ort, plz, strasse, zusatzzeile));
			stammdatenId = UUID.randomUUID().toString();
			listInstitutionsStammdaten.add(insertInstitutionsStammdaten(stammdatenId, institutionsId, adresseId, typ, iban, stunden, tage));
		}
		return stammdatenId;
	}

	private String insertInstitutionsStammdaten(String id, String institutionsId, String adresseId, BetreuungsangebotTyp typ, String iban, String stunden, String tage) {
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
		return sb.toString();
	}

	private String updateInstitutionsStammdaten(String id, BetreuungsangebotTyp typ, String iban, String stunden, String tage) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE institution_stammdaten set");
		sb.append(" betreuungsangebot_typ = ");
		sb.append("'").append(typ.name()).append("'"); // typ
		sb.append(", iban = ");
		sb.append(toStringOrNull(iban));  // ort
		sb.append(", oeffnungsstunden = ");
		sb.append(toBigDecimalOrNull(stunden));  // stunden
		sb.append(", oeffnungstage = ");
		sb.append(toBigDecimalOrNull(tage));  // tage
		sb.append(" where id = '");
		sb.append(id);	// id
		sb.append("';");
		return sb.toString();
	}

	private String insertAdresse(String id, String hausnummer, String ort, String plz, String strasse, String zusatzzeile) {
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
		return sb.toString();
	}

	private String updateAdresse(String id, String hausnummer, String ort, String plz, String strasse, String zusatzzeile) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE adresse set");
		sb.append(" hausnummer = ");
		sb.append(toStringOrNull(hausnummer)); // hausnummer
		sb.append(", ort = ");
		sb.append(toStringOrNull(ort));  // ort
		sb.append(", plz = ");
		sb.append(toStringOrNull(plz));  // plz
		sb.append(", strasse = ");
		sb.append(toStringOrNull(strasse));  // strasse
		sb.append(", zusatzzeile = ");
		sb.append(toStringOrNull(zusatzzeile));  // zusatzzeile
		sb.append(" where id = '");
		sb.append(id);	// id
		sb.append("';");
		return sb.toString();
	}

	private String readString(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			return cell.getStringCellValue();
		} else {
			return null;
		}
	}

	private String readDouble(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			return Double.toString(cell.getNumericCellValue());
		} else {
			return null;
		}
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
				File output = new File(outputFile);
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

	@Override
	public void exportInstitutionsstammdaten() {

		DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("YYYY.MM.dd");

		try {
			File fos = new File("Institutionen_" + formatter.format(LocalDate.now()) + ".csv");
			PrintWriter pw = new PrintWriter(fos);
			LOG.info("Writing File to: " + fos.getAbsolutePath());

			pw.println("TrägerschaftId,Trägerschaft,Trägerschaft E-Mail,InstitutionId,Name,Strasse,Hausnummer,Plz,Ort,Zusatzzeile,E-Mail,StammdatenId,Angebot,IBAN,Öffnungsstunden,Öffnungstage");

			Collection<InstitutionStammdaten> stammdatenList = criteriaQueryHelper.getAll(InstitutionStammdaten.class);
			for (InstitutionStammdaten stammdaten: stammdatenList) {
				Institution institution = stammdaten.getInstitution();
				Traegerschaft traegerschaft = institution.getTraegerschaft();
				Adresse adresse = stammdaten.getAdresse();

				if (institution.getActive()) {

					StringBuilder sb = new StringBuilder();

					if (traegerschaft != null) {
						append(sb, traegerschaft.getId());
						append(sb, traegerschaft.getName());
						append(sb, traegerschaft.getMail());
					} else {
						append(sb, "");
						append(sb, "");
						append(sb, "");
					}

					append(sb, institution.getId());
					append(sb, institution.getName());
					append(sb, adresse.getStrasse());
					append(sb, adresse.getHausnummer());
					append(sb, adresse.getPlz());
					append(sb, adresse.getOrt());
					append(sb, adresse.getZusatzzeile());
					append(sb, institution.getMail());

					append(sb, stammdaten.getId());
					append(sb, stammdaten.getBetreuungsangebotTyp().name());
					String iban = stammdaten.getIban() != null ? stammdaten.getIban().getIban() : "";
					append(sb, iban);
					append(sb, stammdaten.getOeffnungsstunden());
					append(sb, stammdaten.getOeffnungstage());

					pw.println(sb);
				}
			}
			pw.flush();
			pw.close();

		} catch (IOException e) {
			LOG.debug(e.getMessage());
		}
	}

	private void append(@Nonnull StringBuilder sb, @Nullable String s) {
		if (StringUtils.isNotEmpty(s)) {
			sb.append(s);
		}
		sb.append(",");
	}

	private void append(@Nonnull StringBuilder sb, @Nullable BigDecimal s) {
		if (s != null) {
			sb.append(s);
		}
		sb.append(",");
	}
}
