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
        // Dazu PDFBox im Kom. ModulTagesschule < 2.0 kann nicht Dokumente mit embedded Fonts korrekt drucken ("Fett Problem")
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
