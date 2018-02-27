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

import TSKindContainer from './TSKindContainer';
import TSAbstractAntragEntity from './TSAbstractAntragEntity';
import TSFamiliensituation from './TSFamiliensituation';
import TSEinkommensverschlechterungInfo from './TSEinkommensverschlechterungInfo';
import {TSAntragTyp} from './enums/TSAntragTyp';
import TSGesuchstellerContainer from './TSGesuchstellerContainer';
import TSEinkommensverschlechterungInfoContainer from './TSEinkommensverschlechterungInfoContainer';
import TSFamiliensituationContainer from './TSFamiliensituationContainer';
import {TSEingangsart} from './enums/TSEingangsart';
import {getSchulamtBetreuungsangebotTypValues, isOfAnyBetreuungsangebotTyp, TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSBetreuungsstatus} from './enums/TSBetreuungsstatus';
import {TSAntragStatus} from './enums/TSAntragStatus';
import * as moment from 'moment';
import {TSGesuchBetreuungenStatus} from './enums/TSGesuchBetreuungenStatus';
import {TSFinSitStatus} from './enums/TSFinSitStatus';

export default class TSGesuch extends TSAbstractAntragEntity {

    private _gesuchsteller1: TSGesuchstellerContainer;
    private _gesuchsteller2: TSGesuchstellerContainer;
    private _kindContainers: Array<TSKindContainer>;
    private _familiensituationContainer: TSFamiliensituationContainer;
    private _einkommensverschlechterungInfoContainer: TSEinkommensverschlechterungInfoContainer;
    private _bemerkungen: string;
    private _bemerkungenSTV: string;
    private _bemerkungenPruefungSTV: string;
    private _laufnummer: number;
    private _geprueftSTV: boolean = false;
    private _hasFSDokument: boolean = true;
    private _finSitStatus: TSFinSitStatus;
    private _gesperrtWegenBeschwerde: boolean = false;
    private _datumGewarntNichtFreigegeben: moment.Moment;
    private _datumGewarntFehlendeQuittung: moment.Moment;
    private _gesuchBetreuungenStatus: TSGesuchBetreuungenStatus;
    private _dokumenteHochgeladen: boolean = false;

    private _timestampVerfuegt: moment.Moment;
    private _gueltig: boolean;

    // Wir müssen uns merken, dass dies nicht das originalGesuch ist sondern eine Mutations- oder Erneuerungskopie
    // (Wichtig für laden des Gesuchs bei Navigation)
    private _emptyCopy: boolean = false;

    public get gesuchsteller1(): TSGesuchstellerContainer {
        return this._gesuchsteller1;
    }

    public set gesuchsteller1(value: TSGesuchstellerContainer) {
        this._gesuchsteller1 = value;
    }

    public get gesuchsteller2(): TSGesuchstellerContainer {
        return this._gesuchsteller2;
    }

    public set gesuchsteller2(value: TSGesuchstellerContainer) {
        this._gesuchsteller2 = value;
    }

    get kindContainers(): Array<TSKindContainer> {
        return this._kindContainers;
    }

    set kindContainers(value: Array<TSKindContainer>) {
        this._kindContainers = value;
    }

    get familiensituationContainer(): TSFamiliensituationContainer {
        return this._familiensituationContainer;
    }

    set familiensituationContainer(value: TSFamiliensituationContainer) {
        this._familiensituationContainer = value;
    }

    get einkommensverschlechterungInfoContainer(): TSEinkommensverschlechterungInfoContainer {
        return this._einkommensverschlechterungInfoContainer;
    }

    set einkommensverschlechterungInfoContainer(value: TSEinkommensverschlechterungInfoContainer) {
        this._einkommensverschlechterungInfoContainer = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }

    get bemerkungenSTV(): string {
        return this._bemerkungenSTV;
    }

    set bemerkungenSTV(value: string) {
        this._bemerkungenSTV = value;
    }

    get bemerkungenPruefungSTV(): string {
        return this._bemerkungenPruefungSTV;
    }

    set bemerkungenPruefungSTV(value: string) {
        this._bemerkungenPruefungSTV = value;
    }

    get laufnummer(): number {
        return this._laufnummer;
    }

    set laufnummer(value: number) {
        this._laufnummer = value;
    }

    get geprueftSTV(): boolean {
        return this._geprueftSTV;
    }

    set geprueftSTV(value: boolean) {
        this._geprueftSTV = value;
    }

    get hasFSDokument(): boolean {
        return this._hasFSDokument;
    }

    set hasFSDokument(value: boolean) {
        this._hasFSDokument = value;
    }

    get gesperrtWegenBeschwerde(): boolean {
        return this._gesperrtWegenBeschwerde;
    }

    set gesperrtWegenBeschwerde(value: boolean) {
        this._gesperrtWegenBeschwerde = value;
    }

    get emptyCopy(): boolean {
        return this._emptyCopy;
    }

    set emptyCopy(value: boolean) {
        this._emptyCopy = value;
    }

    get datumGewarntNichtFreigegeben(): moment.Moment {
        return this._datumGewarntNichtFreigegeben;
    }

    set datumGewarntNichtFreigegeben(value: moment.Moment) {
        this._datumGewarntNichtFreigegeben = value;
    }

    get datumGewarntFehlendeQuittung(): moment.Moment {
        return this._datumGewarntFehlendeQuittung;
    }

    set datumGewarntFehlendeQuittung(value: moment.Moment) {
        this._datumGewarntFehlendeQuittung = value;
    }

    get timestampVerfuegt(): moment.Moment {
        return this._timestampVerfuegt;
    }

    set timestampVerfuegt(value: moment.Moment) {
        this._timestampVerfuegt = value;
    }

    get gueltig(): boolean {
        return this._gueltig;
    }

    set gueltig(value: boolean) {
        this._gueltig = value;
    }

    get gesuchBetreuungenStatus(): TSGesuchBetreuungenStatus {
        return this._gesuchBetreuungenStatus;
    }

    set gesuchBetreuungenStatus(value: TSGesuchBetreuungenStatus) {
        this._gesuchBetreuungenStatus = value;
    }

    get dokumenteHochgeladen(): boolean {
        return this._dokumenteHochgeladen;
    }

    set dokumenteHochgeladen(value: boolean) {
        this._dokumenteHochgeladen = value;
    }

    public isMutation(): boolean {
        return this.typ === TSAntragTyp.MUTATION;
    }

    public isFolgegesuch(): boolean {
        return this.typ === TSAntragTyp.ERNEUERUNGSGESUCH;
    }

    public isOnlineGesuch(): boolean {
        return TSEingangsart.ONLINE === this.eingangsart;
    }

    public get finSitStatus(): TSFinSitStatus {
        return this._finSitStatus;
    }

    public set finSitStatus(value: TSFinSitStatus) {
        this._finSitStatus = value;
    }

    /**
     * Schaut ob der GS1 oder der GS2 mindestens eine umzugsadresse hat
     */
    public isThereAnyUmzug(): boolean {
        if (this.gesuchsteller1 && this.gesuchsteller1.getUmzugAdressen().length > 0) {
            return true;
        }
        if (this.gesuchsteller2 && this.gesuchsteller2.getUmzugAdressen().length > 0) {
            return true;
        }
        return false;
    }

    /**
     *
     * @returns {any} Alle KindContainer in denen das Kind Betreuung benoetigt
     */
    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        let listResult: Array<TSKindContainer> = [];
        if (this.kindContainers) {
            this.kindContainers.forEach((kind) => {
                if (kind.kindJA.familienErgaenzendeBetreuung) {
                    listResult.push(kind);
                }
            });
        }
        return listResult;
    }

    /**
     * Returns true when all Betreuungen are of one of the given types
     */
    private areThereOnlyAngeboteOfType(types: TSBetreuungsangebotTyp[]): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        if (kinderWithBetreuungList.length <= 0) {
            return false; // no Kind with bedarf
        }
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (!isOfAnyBetreuungsangebotTyp(betreuung.institutionStammdaten.betreuungsangebotTyp, types)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true when all Betreuungen are of kind SCHULAMT.
     * Returns false also if there are no Kinder with betreuungsbedarf
     */
    public areThereOnlySchulamtAngebote(): boolean {
        return this.areThereOnlyAngeboteOfType(getSchulamtBetreuungsangebotTypValues());
    }

    /**
     * Returns true when all Betreuungen are of kind FERIENINSEL.
     * Returns false also if there are no Kinder with betreuungsbedarf
     */
    public areThereOnlyFerieninsel(): boolean {
        return this.areThereOnlyAngeboteOfType([TSBetreuungsangebotTyp.FERIENINSEL]);
    }

    /**
     * Returns true when all Betreuungen are geschlossen ohne verfuegung
     */
    public areThereOnlyGeschlossenOhneVerfuegung(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        if (kinderWithBetreuungList.length <= 0) {
            return false; // no Kind with bedarf
        }
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (betreuung.betreuungsstatus !== TSBetreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG) {
                    return false;
                }
            }
        }
        return true;
    }

    public hasBetreuungInStatusWarten(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (betreuung.betreuungsstatus === TSBetreuungsstatus.WARTEN) {
                    return true;
                }
            }
        }
        return false;
    }

    public extractFamiliensituation(): TSFamiliensituation {
        if (this.familiensituationContainer) {
            return this.familiensituationContainer.familiensituationJA;
        }
        return undefined;
    }

    public extractFamiliensituationErstgesuch(): TSFamiliensituation {
        if (this.familiensituationContainer) {
            return this.familiensituationContainer.familiensituationErstgesuch;
        }
        return undefined;
    }

    public extractEinkommensverschlechterungInfo(): TSEinkommensverschlechterungInfo {
        if (this.einkommensverschlechterungInfoContainer) {
            return this.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA;
        }
        return undefined;
    }

    public canBeFreigegeben(): boolean {
        return this.status === TSAntragStatus.FREIGABEQUITTUNG;
    }

    /**
     * Schaut dass mindestens eine Betreuung erfasst wurde.
     * @returns {boolean}
     */
    public isThereAnyBetreuung(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        for (let kind of kinderWithBetreuungList) {
            if (kind.betreuungen && kind.betreuungen.length > 0) {
                return true;
            }
        }
        return false;
    }

    public extractKindFromKindNumber(kindNumber: number): TSKindContainer {
        if (this.kindContainers && kindNumber > 0) {
            for (let i = 0; i < this.kindContainers.length; i++) {
                if (this.kindContainers[i].kindNummer === kindNumber) {
                    return this.kindContainers[i];
                }
            }
        }
        return undefined;
    }
}
