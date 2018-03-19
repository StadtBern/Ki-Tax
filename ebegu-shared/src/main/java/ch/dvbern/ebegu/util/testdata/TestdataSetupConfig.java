/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

package ch.dvbern.ebegu.util.testdata;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mandant;

/**
 * Grund- Konfiguration fuer Testfaelle
 */
public class TestdataSetupConfig {

	private Mandant mandant;
	private InstitutionStammdaten kitaBruennen;
	private InstitutionStammdaten kitaWeissenstein;
	private InstitutionStammdaten tagiWeissenstein;
	private InstitutionStammdaten tagesschuleBruennen;
	private InstitutionStammdaten ferieninselBruennen;

	private Gesuchsperiode gesuchsperiode;

	private TestdataSetupConfig() {
	}

	public TestdataSetupConfig(Mandant mandant, InstitutionStammdaten kitaBruennen, InstitutionStammdaten kitaWeissenstein,
			InstitutionStammdaten tagiWeissenstein) {
		this.mandant = mandant;
		this.kitaBruennen = kitaBruennen;
		this.kitaWeissenstein = kitaWeissenstein;
		this.tagiWeissenstein = tagiWeissenstein;
	}

	public TestdataSetupConfig(Mandant mandant, InstitutionStammdaten kitaBruennen, InstitutionStammdaten kitaWeissenstein,
			InstitutionStammdaten tagiWeissenstein, Gesuchsperiode gesuchsperiode) {
		this.mandant = mandant;
		this.kitaBruennen = kitaBruennen;
		this.kitaWeissenstein = kitaWeissenstein;
		this.tagiWeissenstein = tagiWeissenstein;
		this.gesuchsperiode = gesuchsperiode;
	}

	public TestdataSetupConfig(Mandant mandant, InstitutionStammdaten kitaBruennen, InstitutionStammdaten kitaWeissenstein,
		InstitutionStammdaten tagiWeissenstein, InstitutionStammdaten tagesschuleBruennen, InstitutionStammdaten ferieninselBruennen,
		Gesuchsperiode gesuchsperiode) {
		this.mandant = mandant;
		this.kitaBruennen = kitaBruennen;
		this.kitaWeissenstein = kitaWeissenstein;
		this.tagiWeissenstein = tagiWeissenstein;
		this.tagesschuleBruennen = tagesschuleBruennen;
		this.ferieninselBruennen = ferieninselBruennen;
		this.gesuchsperiode = gesuchsperiode;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}

	public InstitutionStammdaten getKitaBruennen() {
		return kitaBruennen;
	}

	public void setKitaBruennen(InstitutionStammdaten kitaBruennen) {
		this.kitaBruennen = kitaBruennen;
	}

	public InstitutionStammdaten getKitaWeissenstein() {
		return kitaWeissenstein;
	}

	public void setKitaWeissenstein(InstitutionStammdaten kitaWeissenstein) {
		this.kitaWeissenstein = kitaWeissenstein;
	}

	public InstitutionStammdaten getTagiWeissenstein() {
		return tagiWeissenstein;
	}

	public void setTagiWeissenstein(InstitutionStammdaten tagiWeissenstein) {
		this.tagiWeissenstein = tagiWeissenstein;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public InstitutionStammdaten getTagesschuleBruennen() {
		return tagesschuleBruennen;
	}

	public void setTagesschuleBruennen(InstitutionStammdaten tagesschuleBruennen) {
		this.tagesschuleBruennen = tagesschuleBruennen;
	}

	public InstitutionStammdaten getFerieninselBruennen() {
		return ferieninselBruennen;
	}

	public void setFerieninselBruennen(InstitutionStammdaten ferieninselBruennen) {
		this.ferieninselBruennen = ferieninselBruennen;
	}
}
