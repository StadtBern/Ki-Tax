/*
 * Copyright (C) 2018 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.reporting.massenversand;

import java.util.List;

/**
 * DTO für eine Familie eines Massenversands
 */
public class MassenversandDataRow {

	private String gesuchsperiode;
	private String fall;

	private String gs1Name;
	private String gs1Vorname;
	private String gs1PersonId;
	private String gs1Mail;

	private String gs2Name;
	private String gs2Vorname;
	private String gs2PersonId;
	private String gs2Mail;

	private String adresse;

	private String einreichungsart;
	private String status;
	private String typ;

	private List<MassenversandRepeatKindDataCol> kinderCols;


	public String getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(String gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public String getFall() {
		return fall;
	}

	public void setFall(String fall) {
		this.fall = fall;
	}

	public String getGs1Name() {
		return gs1Name;
	}

	public void setGs1Name(String gs1Name) {
		this.gs1Name = gs1Name;
	}

	public String getGs1Vorname() {
		return gs1Vorname;
	}

	public void setGs1Vorname(String gs1Vorname) {
		this.gs1Vorname = gs1Vorname;
	}

	public String getGs1PersonId() {
		return gs1PersonId;
	}

	public void setGs1PersonId(String gs1PersonId) {
		this.gs1PersonId = gs1PersonId;
	}

	public String getGs1Mail() {
		return gs1Mail;
	}

	public void setGs1Mail(String gs1Mail) {
		this.gs1Mail = gs1Mail;
	}

	public String getGs2Name() {
		return gs2Name;
	}

	public void setGs2Name(String gs2Name) {
		this.gs2Name = gs2Name;
	}

	public String getGs2Vorname() {
		return gs2Vorname;
	}

	public void setGs2Vorname(String gs2Vorname) {
		this.gs2Vorname = gs2Vorname;
	}

	public String getGs2PersonId() {
		return gs2PersonId;
	}

	public void setGs2PersonId(String gs2PersonId) {
		this.gs2PersonId = gs2PersonId;
	}

	public String getGs2Mail() {
		return gs2Mail;
	}

	public void setGs2Mail(String gs2Mail) {
		this.gs2Mail = gs2Mail;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getEinreichungsart() {
		return einreichungsart;
	}

	public void setEinreichungsart(String einreichungsart) {
		this.einreichungsart = einreichungsart;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public List<MassenversandRepeatKindDataCol> getKinderCols() {
		return kinderCols;
	}

	public void setKinderCols(List<MassenversandRepeatKindDataCol> kinderCols) {
		this.kinderCols = kinderCols;
	}
}
