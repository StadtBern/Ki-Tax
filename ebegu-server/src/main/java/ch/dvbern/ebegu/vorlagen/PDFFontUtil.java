/*
 * Copyright © 2010 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 *
 * $Id$
 */
package ch.dvbern.ebegu.vorlagen;

import com.lowagie.text.FontFactory;

public class PDFFontUtil {

    /*
    public static final String FONT_FACE_ARIAL = "arial";
    public static final String TTF_ARIAL_RESOURCE = "/font/arial.ttf";

    private static final String FONT_FACE_CALIBRI = "calibri";
    private static final String TTF_CALIBRI_RESOURCE = "/font/calibri.ttf";
    private static final String FONT_FACE_CALIBRIB = "calibri";
    private static final String TTF_CALIBRIB_RESOURCE = "/font/calibri.ttf";
    private static final String FONT_FACE_CALIBRII = "calibri";
    private static final String TTF_CALIBRII_RESOURCE = "/font/calibri.ttf";
    private static final String FONT_FACE_CALIBRIL = "calibri";
    private static final String TTF_CALIBRIL_RESOURCE = "/font/calibri.ttf";
    private static final String FONT_FACE_CALIBRILI = "calibri";
    private static final String TTF_CALIBRILI_RESOURCE = "/font/calibri.ttf";
    private static final String FONT_FACE_CALIBRIZ = "calibri";
    private static final String TTF_CALIBRIZ_RESOURCE = "/font/calibri.ttf";
    */

    public static void embedStandardFonts() {
        FontFactory.defaultEmbedding = true;

        /*
        // Auskommentiert, Seitenummer Problem, DABU 07.06.2015
        // Dazu PDFBox im Kom. Modul < 2.0 kann nicht Dokumente mit embedded Fonts korrekt drucken ("Fett Problem")
        FontFactory.register(TTF_ARIAL_RESOURCE, FONT_FACE_ARIAL);
        FontFactory.register(TTF_CALIBRI_RESOURCE, FONT_FACE_CALIBRI);
        FontFactory.register(TTF_CALIBRIB_RESOURCE, FONT_FACE_CALIBRIB);
        FontFactory.register(TTF_CALIBRII_RESOURCE, FONT_FACE_CALIBRII);
        FontFactory.register(TTF_CALIBRIL_RESOURCE, FONT_FACE_CALIBRIL);
        FontFactory.register(TTF_CALIBRILI_RESOURCE, FONT_FACE_CALIBRILI);
        FontFactory.register(TTF_CALIBRIZ_RESOURCE, FONT_FACE_CALIBRIZ);
        */
    }
}
