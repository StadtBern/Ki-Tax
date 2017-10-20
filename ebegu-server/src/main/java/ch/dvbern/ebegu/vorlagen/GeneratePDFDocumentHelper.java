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

package ch.dvbern.ebegu.vorlagen;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Objects;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;
import com.google.common.io.ByteStreams;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Helper Klasse um einen DocX zu einem PDF zu konvertieren
 */
public class GeneratePDFDocumentHelper {

	private static final String PDFENCODING = "ISO-8859-1";
	private static final String NUMOFPAGE = "#PAGE";
	private static final String NUMOFPAGES = "#MAX";
	private static final String PROP_STANDARD_ANZAHL_SEITEN = "expectedNumberOfPages";

	/**
	 * Konvertiert ein docx zu einem PDF
	 *
	 * @return das PDF Dokument als Byte
	 */
	@Nonnull
	private byte[] generatePDFDocument(@Nonnull byte[] generateFrom) throws MergeDocException {
		try {
			Objects.requireNonNull(generateFrom, "generateFrom muss gesetzt sein");
			final XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(generateFrom));

			final PdfOptions options = PdfOptions.create();
			options.setConfiguration(this::setXDocReportPDFWriterOptions);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfConverter.getInstance().convert(document, out, options);

			return manipulatePdf(out.toByteArray());
		} catch (IOException | InvocationTargetException | DocumentException | IllegalAccessException | NoSuchMethodException e) {
			throw new MergeDocException("generatePDFDocument()", "Bei der Generierung der Verfuegungsmustervorlage ist einen Fehler aufgetretten", e, new Objects[] {});
		}
	}

	/**
	 * Konvertiert ein docx zu einem PDF
	 *
	 * @return das PDF Dokument als Byte
	 */
	@Nonnull
	public byte[] generatePDFDocument(@Nonnull byte[] docxTemplate, @Nonnull EBEGUMergeSource mergeSource, boolean writeProtected) throws MergeDocException {
		try {
			Objects.requireNonNull(docxTemplate, "generateFrom muss gesetzt sein");
			Objects.requireNonNull(docxTemplate, "mergeSource muss gesetzt sein");

			DOCXMergeEngine docxme = new DOCXMergeEngine(mergeSource.getClass().getName());

			byte[] mergedDocx = docxme.getDocument(new ByteArrayInputStream(docxTemplate), mergeSource);
			//			save(mergedDocx);
			byte[] mergedPdf = generatePDFDocument(mergedDocx);
			PdfReader reader = new PdfReader(mergedPdf);
			int numOfPDFPages = reader.getNumberOfPages();
			reader.close();

			int expectedNumOfDOCXPages = 0;
			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(mergedDocx));
			if (document.getProperties().getCustomProperties().contains(PROP_STANDARD_ANZAHL_SEITEN)) {
				expectedNumOfDOCXPages = document.getProperties().getCustomProperties()
					.getProperty(PROP_STANDARD_ANZAHL_SEITEN).getI4();
			}

			if (expectedNumOfDOCXPages > 0 && expectedNumOfDOCXPages != numOfPDFPages) {
				mergeSource.setPDFLongerThanExpected(true);
				mergedDocx = docxme.getDocument(new ByteArrayInputStream(docxTemplate), mergeSource);
				mergedPdf = generatePDFDocument(mergedDocx);
			}

			if (!writeProtected) {
				mergedPdf = addDraftWatermark(mergedPdf);
			}

			return mergedPdf;
		} catch (IOException | DocTemplateException | DocumentException e) {
			throw new MergeDocException("generatePDFDocument()", "Bei der Generierung der Verfuegungsmustervorlage ist einen Fehler aufgetretten", e, new Objects[] {});
		}
	}

	/**
	 * Speichert das Zwischenresultat der PDF Generierung (Word mit ersetzten Tags)
	 * im Temp-Folder. Zum Debuggen.
	 */
	@SuppressWarnings(value = { "PMD.UnusedPrivateMethod", "UPM_UNCALLED_PRIVATE_METHOD", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE" })
	//	private boolean save(byte[] content) {
	//		UUID uuid = UUID.randomUUID();
	//		String tempDir = System.getProperty("java.io.tmpdir");
	//		final String absoluteFilePath = tempDir + "/" + uuid + ".docx";
	//		Path file = Paths.get(absoluteFilePath);
	//		try {
	//			if (!Files.exists(file.getParent())) {
	//				Files.createDirectories(file.getParent());
	//				LOGGER.info("Save Word-file in FileSystem: " + absoluteFilePath);
	//			}
	//			Files.write(file, content);
	//		} catch (IOException e) {
	//			LOGGER.info("Can't save file in FileSystem: ");
	//			return false;
	//		}
	//		return true;
	//	}

	private void setXDocReportPDFWriterOptions(@Nonnull PdfWriter pdfWriter) {
		pdfWriter.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
		pdfWriter.setTagged();
		PDFFontUtil.embedStandardFonts();
	}

	/**
	 * PDF has to be manipulated in order to set the right page number.
	 */
	@Nonnull
	private byte[] manipulatePdf(@Nonnull byte[] doc) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, DocumentException {

		PDFFontUtil.embedStandardFonts();

		ByteArrayOutputStream manipulated = new ByteArrayOutputStream();
		PdfStamper stamper = null;
		PdfReader reader = new PdfReader(doc);
		int pages = reader.getNumberOfPages();
		try {
			for (int i = 1; i <= pages; i++) {
				PdfDictionary dict = reader.getPageN(i);
				PdfObject object = dict.getDirectObject(PdfName.CONTENTS);
				if (object instanceof PRStream) {
					PRStream stream = (PRStream) object;
					byte[] data = PdfReader.getStreamBytes(stream);
					// Man muss hier doppelt die Kodierung angeben und es funktioniert nicht mit UTF-8. Scheint nicht
					// auf unseren Windows-Rechner zu funktionieren!!!
					String correctedStr = new String(data, Charset.forName(PDFENCODING)).replace(NUMOFPAGE, String.valueOf(i)).replace(NUMOFPAGES, String.valueOf(pages));
					stream.setData(correctedStr.getBytes(PDFENCODING));
				}
			}
			stamper = new PdfStamper(reader, manipulated);

			setWriterPDFA(stamper);

		} finally {
			if (stamper != null) {
				stamper.close();
			}
			reader.close();
			manipulated.close();
		}
		return manipulated.toByteArray();
	}

	private byte[] addDraftWatermark(byte[] orginalPDF) throws IOException, DocumentException {

		PdfReader pdfReader = null;
		ByteArrayOutputStream outputStream = null;
		PdfStamper pdfStamper = null;

		pdfReader = new PdfReader(orginalPDF);
		outputStream = new ByteArrayOutputStream();
		pdfStamper = new PdfStamper(pdfReader, outputStream);

		PdfLayer layer = new PdfLayer("watermark", pdfStamper.getWriter());

		for (int pageIndex = 1; pageIndex <= pdfReader.getNumberOfPages(); pageIndex++) {
			pdfStamper.setFormFlattening(false);
			Rectangle pageRectangle = pdfReader.getPageSizeWithRotation(pageIndex);
			PdfContentByte pdfData = pdfStamper.getOverContent(pageIndex);

			pdfData.beginLayer(layer);

			PdfGState graphicsState = new PdfGState();
			pdfData.setGState(graphicsState);
			pdfData.beginText();

			Image watermarkImage = Image.getInstance(ByteStreams.toByteArray(
				GeneratePDFDocumentHelper.class.getResourceAsStream("/vorlagen/entwurfWasserzeichen.png")));

			float width = pageRectangle.getWidth();
			float height = pageRectangle.getHeight();

			watermarkImage.setAbsolutePosition(width / 2 - watermarkImage.getWidth() / 2, height / 2 - watermarkImage.getHeight() / 2);

			pdfData.addImage(watermarkImage);
			pdfData.endText();
			pdfData.endLayer();
		}

		pdfStamper.close();
		outputStream.close();
		pdfReader.close();

		return outputStream.toByteArray();
	}

	private void setWriterPDFA(@Nonnull PdfStamper stamper) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		PdfWriter writer = stamper.getWriter();

		writer.setPDFXConformance(PdfWriter.PDFA1B);
		writer.createXmpMetadata();

		setColorProfile(writer);
	}

	/**
	 * Zusätlich zu die Fonts, soll auch das Color Profil im PDF.Dictionnary addiert werden.
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE")
	@SuppressFBWarnings(value = "UI_INHERITANCE_UNSAFE_GETRESOURCE")
	private void setColorProfile(@Nonnull PdfWriter pdfWriter) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		pdfWriter.setDefaultColorspace(PdfName.DEFAULTRGB, null);

		// Hack: Bevor pdfWriter.setOutputIntents zu aufrufen, soll das Document geöffnet werden (das Document selbst,
		// nicht den Writer)
		try {
			Method m = PdfWriter.class.getDeclaredMethod("getPdfDocument");
			m.setAccessible(true);
			((PdfDocument) m.invoke(pdfWriter)).open();
		} catch (NullPointerException eN) {
			// Invoking führt zur NullPointer bei initPage aber Rest den Job ist gemacht, was genug ist hier.
			// Open wird später wieder bei xdocreport mit success durchgeführt.
		}

		final ICC_Profile icc = ICC_Profile.getInstance(this.getClass().getResourceAsStream("/font/sRGB.profile"));
		pdfWriter.setOutputIntents("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", icc);
	}
}
