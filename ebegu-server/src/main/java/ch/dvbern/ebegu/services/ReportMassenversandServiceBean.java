/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ch.dvbern.ebegu.dto.KindDubletteDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Massenversand;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.GesuchTypFromAngebotTyp;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.reporting.ReportMassenversandService;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandDataRow;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandExcelConverter;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandRepeatKindDataCol;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;
import ch.dvbern.oss.lib.excelmerger.ExcelMerger;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.ejb3.annotation.TransactionTimeout;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

@SuppressWarnings("unchecked")
@Stateless
@Local(ReportMassenversandService.class)
@PermitAll
public class ReportMassenversandServiceBean extends AbstractReportServiceBean implements ReportMassenversandService {

	private static final char SEPARATOR = ';';

	private MassenversandExcelConverter massenversandExcelConverter = new MassenversandExcelConverter();

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private KindService  kindService;


	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT })
	public List<MassenversandDataRow> getReportMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nonnull String gesuchPeriodeID,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nullable String text
	) {

		List<Gesuch> ermittelteGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			datumVon,
			datumBis,
			gesuchPeriodeID
		);

		// Filter Gesuche by AngebotTyp
		List<Gesuch> gesucheFilteredByAngebotTyp =
			filterGesucheByAngebotTyp(inklBgGesuche, inklMischGesuche, inklTsGesuche, ermittelteGesuche);

		List<Gesuch> resultGesuchFinalList =
			filterGesucheByFolgegesuch(ohneErneuerungsgesuch, gesucheFilteredByAngebotTyp);

		// Wenn ein Text eingegeben wurde, wird der Massenversand gespeichert
		if (StringUtils.isNotEmpty(text) && !resultGesuchFinalList.isEmpty()) {
			saveMassenversand(
				datumVon,
				datumBis,
				gesuchPeriodeID,
				inklBgGesuche,
				inklMischGesuche,
				inklTsGesuche,
				ohneErneuerungsgesuch,
				text,
				resultGesuchFinalList);
		}

		final List<MassenversandDataRow> reportDataMassenversand = createReportDataMassenversand(resultGesuchFinalList);
		return reportDataMassenversand;
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT })
	@TransactionTimeout(value = Constants.STATISTIK_TIMEOUT_MINUTES, unit = TimeUnit.MINUTES)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UploadFileInfo generateExcelReportMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nonnull String gesuchPeriodeId,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nullable String text
	) throws ExcelMergeException {

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_MASSENVERSAND;

		InputStream is = ReportMassenversandServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		List<MassenversandDataRow> reportData = getReportMassenversand(
			datumVon, datumBis, gesuchPeriodeId, inklBgGesuche, inklMischGesuche, inklTsGesuche,
			ohneErneuerungsgesuch, text);

		Optional<Gesuchsperiode> gesuchsperiodeOptional = gesuchsperiodeService.findGesuchsperiode(gesuchPeriodeId);
		Gesuchsperiode gesuchsperiode = gesuchsperiodeOptional.orElseThrow(() ->
			new EbeguEntityNotFoundException("findGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchPeriodeId)
		);

		ExcelMergerDTO excelMergerDTO = massenversandExcelConverter.toExcelMergerDTO(
			reportData,
			Locale.getDefault(),
			datumVon,
			datumBis,
			gesuchsperiode,
			inklBgGesuche,
			inklMischGesuche,
			inklTsGesuche,
			ohneErneuerungsgesuch,
			text);

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		massenversandExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
			Constants.TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}

	private List<MassenversandDataRow> createReportDataMassenversand(@Nonnull List<Gesuch> resultGesuchList) {
		final List<MassenversandDataRow> rows = resultGesuchList.stream()
			.map(gesuch -> {
				MassenversandDataRow row = new MassenversandDataRow();

				row.setGesuchsperiode(gesuch.getGesuchsperiode().getGesuchsperiodeString());
				row.setFall(gesuch.getFall().getPaddedFallnummer());

				setGS1Data(gesuch, row);
				setGS2Data(gesuch, row);

				setKinderData(gesuch, row);

				row.setEinreichungsart(ServerMessageUtil.translateEnumValue(getEingangsartFromFallBesitzer(gesuch)));
				row.setStatus(ServerMessageUtil.translateEnumValue(gesuch.getStatus()));
				row.setTyp(ServerMessageUtil.translateEnumValue(gesuch.getTyp()));

				return row;
			})
			.collect(Collectors.toList());

		return rows;
	}

	/**
	 * If we are only interested in the Eingangsart of the Erstgesuch, i.e:
	 * Papier_erstgesuch + Papier_mutation --> Eingangsart = PAPIER
	 * Online_erstgesuch + Papier_mutation --> Eingangsart = ONLINE
	 * we need to take the Eingangsart of the Erstgesuch and not from the mutation. For this case there is a much
	 * more performant way than to looking for the Erstgesuch.
	 * If the fall has a Besitzer, it is an online Gesuch.
	 */
	private Eingangsart getEingangsartFromFallBesitzer(@Nonnull Gesuch gesuch) {
		if (gesuch.getFall().getBesitzer() != null) {
			return Eingangsart.ONLINE;
		}
		return Eingangsart.PAPIER;
	}

	private void setGS2Data(Gesuch gesuch, MassenversandDataRow row) {
		if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2().getGesuchstellerJA() != null) {
			final Gesuchsteller gesuchstellerJA = gesuch.getGesuchsteller2().getGesuchstellerJA();
			row.setGs2Name(gesuchstellerJA.getNachname());
			row.setGs2Vorname(gesuchstellerJA.getVorname());
			row.setGs2PersonId(gesuchstellerJA.getEwkPersonId());
			row.setGs2Mail(gesuchstellerJA.getMail());
		}
	}

	private void setGS1Data(Gesuch gesuch, MassenversandDataRow row) {
		if (gesuch.getGesuchsteller1() != null && gesuch.getGesuchsteller1().getGesuchstellerJA() != null) {
			final Gesuchsteller gesuchstellerJA = gesuch.getGesuchsteller1().getGesuchstellerJA();
			row.setGs1Name(gesuchstellerJA.getNachname());
			row.setGs1Vorname(gesuchstellerJA.getVorname());
			row.setGs1PersonId(gesuchstellerJA.getEwkPersonId());
			row.setGs1Mail(gesuchstellerJA.getMail());
			final GesuchstellerAdresse currentKorrespondezAdresse =
				gesuch.getGesuchsteller1().extractEffektiveKorrespondezAdresse(LocalDate.now());
			if (currentKorrespondezAdresse != null) {
				row.setAdresse(currentKorrespondezAdresse.getAddressAsString());
			}
		}
	}

	private void setKinderData(Gesuch gesuch, MassenversandDataRow row) {
		final Set<KindDubletteDTO> kindDubletten = kindService.getKindDubletten(gesuch.getId());
		row.setKinderCols(
			gesuch.getKindContainers().stream()
				.filter(kindContainer -> kindContainer.getKindJA() != null
					&& kindContainer.getBetreuungen() != null && !kindContainer.getBetreuungen().isEmpty())
				.map(kindContainer -> {
					final MassenversandRepeatKindDataCol kindCol = new MassenversandRepeatKindDataCol();
					final Kind kind = kindContainer.getKindJA();
					kindCol.setKindName(kind.getNachname());
					kindCol.setKindVorname(kind.getVorname());
					kindCol.setKindGeburtsdatum(kind.getGeburtsdatum());
					setDubletten(kindCol, kindContainer, kindDubletten);

					kindContainer.getBetreuungen().stream()
						.map(Betreuung::getInstitutionStammdaten)
						.forEach(instStammdaten -> setInstitutionName(kindCol, instStammdaten));

					return kindCol;
				})
				.collect(Collectors.toList())
		);
	}

	private void setDubletten(
		@Nonnull MassenversandRepeatKindDataCol kindCol,
		@Nonnull KindContainer kindContainer,
		@Nonnull Set<KindDubletteDTO> kindDubletten
	) {
		kindDubletten.stream()
			.filter(kindDubletteDTO -> kindDubletteDTO.getKindNummerOriginal().equals(kindContainer.getKindNummer()))
			.forEach(kindDubletteDTO ->
				kindCol.addKindDubletten(PrintUtil.getPaddedFallnummer(kindDubletteDTO.getFallNummer())
			));
	}

	private void setInstitutionName(
		@Nonnull MassenversandRepeatKindDataCol kindCol,
		@Nonnull InstitutionStammdaten instStammdaten
	) {
		final String instName = instStammdaten.getInstitution().getName();
		switch (instStammdaten.getBetreuungsangebotTyp()) {
		case KITA:
			kindCol.setKindInstitutionKitaOrWeitere(instName);
			break;
		case TAGI:
			kindCol.setKindInstitutionTagiOrWeitere(instName);
			break;
		case TAGESELTERN_KLEINKIND:
			kindCol.setKindInstitutionTeKleinkindOrWeitere(instName);
			break;
		case TAGESELTERN_SCHULKIND:
			kindCol.setKindInstitutionTeSchulkindOrWeitere(instName);
			break;
		case TAGESSCHULE:
			kindCol.setKindInstitutionTagesschuleOrWeitere(instName);
			break;
		case FERIENINSEL:
			kindCol.setKindInstitutionFerieninselOrWeitere(instName);
			break;
		}
	}

	private List<Gesuch> filterGesucheByFolgegesuch(boolean ohneErneuerungsgesuch, List<Gesuch> gesucheFilteredByAngebotTyp) {
		if (ohneErneuerungsgesuch) {
			return gesucheFilteredByAngebotTyp.stream()
				.filter(gesuch -> !gesuchService.hasFolgegesuchForAmt(gesuch.getId()))
				.collect(Collectors.toList());
		}
		return gesucheFilteredByAngebotTyp;
	}

	private List<Gesuch> filterGesucheByAngebotTyp(
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		List<Gesuch> ermittelteGesuche
	) {
		return ermittelteGesuche.stream()
			.filter(
				gesuch -> {
					final GesuchTypFromAngebotTyp gesuchTyp = gesuch.calculateGesuchTypFromAngebotTyp();
					return (inklTsGesuche && gesuchTyp == GesuchTypFromAngebotTyp.TS_GESUCH)
						|| (inklBgGesuche && gesuchTyp == GesuchTypFromAngebotTyp.BG_GESUCH)
						|| (inklMischGesuche && gesuchTyp == GesuchTypFromAngebotTyp.MISCH_GESUCH);
				}
			).collect(Collectors.toList());
	}

	private void saveMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nullable String gesuchPeriodeID,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nonnull String text,
		@Nonnull List<Gesuch> gesuche
	) {
		Massenversand massenversand = new Massenversand();
		massenversand.setText(text);
		@SuppressWarnings("StringConcatenationMissingWhitespace")
		String einstellungen = Constants.DATE_FORMATTER.format(datumVon) + SEPARATOR
			+ Constants.DATE_FORMATTER.format(datumBis) + SEPARATOR
			+ gesuchPeriodeID + SEPARATOR
			+ inklBgGesuche + SEPARATOR
			+ inklMischGesuche + SEPARATOR
			+ inklTsGesuche + SEPARATOR
			+ ohneErneuerungsgesuch + SEPARATOR;
		massenversand.setEinstellungen(einstellungen);
		massenversand.setGesuche(gesuche);
		gesuchService.createMassenversand(massenversand);
	}
}
