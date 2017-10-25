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

import TSFinanzielleSituationContainer from './TSFinanzielleSituationContainer';
import TSGesuch from './TSGesuch';
import TSFinanzielleSituation from './TSFinanzielleSituation';
import TSEinkommensverschlechterungContainer from './TSEinkommensverschlechterungContainer';
import TSEinkommensverschlechterung from './TSEinkommensverschlechterung';
import TSEinkommensverschlechterungInfoContainer from './TSEinkommensverschlechterungInfoContainer';

export default class TSFinanzModel {

    private _gemeinsameSteuererklaerung: boolean;
    private _sozialhilfeBezueger: boolean;
    private _verguenstigungGewuenscht: boolean;
    private _finanzielleSituationContainerGS1: TSFinanzielleSituationContainer;
    private _finanzielleSituationContainerGS2: TSFinanzielleSituationContainer;
    private _einkommensverschlechterungContainerGS1: TSEinkommensverschlechterungContainer;
    private _einkommensverschlechterungContainerGS2: TSEinkommensverschlechterungContainer;
    private _einkommensverschlechterungInfoContainer: TSEinkommensverschlechterungInfoContainer;

    private basisjahr: number;
    private basisjahrPlus: number;
    private gesuchsteller2Required: boolean;
    private gesuchstellerNumber: number;

    constructor(basisjahr: number, gesuchsteller2Required: boolean, gesuchstellerNumber: number, basisjahrPlus?: number) {
        this.basisjahr = basisjahr;
        this.basisjahrPlus = basisjahrPlus;
        this.gesuchsteller2Required = gesuchsteller2Required;
        this.gesuchstellerNumber = gesuchstellerNumber;
    }

    get gemeinsameSteuererklaerung(): boolean {
        return this._gemeinsameSteuererklaerung;
    }

    set gemeinsameSteuererklaerung(value: boolean) {
        this._gemeinsameSteuererklaerung = value;
    }

    public get sozialhilfeBezueger(): boolean {
        return this._sozialhilfeBezueger;
    }

    public set sozialhilfeBezueger(value: boolean) {
        this._sozialhilfeBezueger = value;
    }

    public get verguenstigungGewuenscht(): boolean {
        return this._verguenstigungGewuenscht;
    }

    public set verguenstigungGewuenscht(value: boolean) {
        this._verguenstigungGewuenscht = value;
    }

    get finanzielleSituationContainerGS1(): TSFinanzielleSituationContainer {
        return this._finanzielleSituationContainerGS1;
    }

    set finanzielleSituationContainerGS1(value: TSFinanzielleSituationContainer) {
        this._finanzielleSituationContainerGS1 = value;
    }

    get finanzielleSituationContainerGS2(): TSFinanzielleSituationContainer {
        return this._finanzielleSituationContainerGS2;
    }

    set finanzielleSituationContainerGS2(value: TSFinanzielleSituationContainer) {
        this._finanzielleSituationContainerGS2 = value;
    }

    public copyFinSitDataFromGesuch(gesuch: TSGesuch) {

        this.gemeinsameSteuererklaerung = this.getCopiedValueOrFalse(gesuch.extractFamiliensituation().gemeinsameSteuererklaerung);
        this.sozialhilfeBezueger = this.getCopiedValueOrFalse(gesuch.extractFamiliensituation().sozialhilfeBezueger);
        this.verguenstigungGewuenscht = this.getCopiedValueOrFalse(gesuch.extractFamiliensituation().verguenstigungGewuenscht);
        this.finanzielleSituationContainerGS1 = angular.copy(gesuch.gesuchsteller1.finanzielleSituationContainer);
        if (gesuch.gesuchsteller2) {
            this.finanzielleSituationContainerGS2 = angular.copy(gesuch.gesuchsteller2.finanzielleSituationContainer);
        }
        this.initFinSit();
    }

    private getCopiedValueOrFalse(value: boolean): boolean {
        if (value) {
            return angular.copy(value);
        } else {
            return false;
        }
    }

    copyEkvDataFromGesuch(gesuch: TSGesuch) {
        if (gesuch.einkommensverschlechterungInfoContainer) {
            this.einkommensverschlechterungInfoContainer = angular.copy(gesuch.einkommensverschlechterungInfoContainer);
        } else {
            this.einkommensverschlechterungInfoContainer = new TSEinkommensverschlechterungInfoContainer;
        }
        //geesuchstelelr1 nullsave?
        this.einkommensverschlechterungContainerGS1 = angular.copy(gesuch.gesuchsteller1.einkommensverschlechterungContainer);
        if (gesuch.gesuchsteller2) {
            this.einkommensverschlechterungContainerGS2 = angular.copy(gesuch.gesuchsteller2.einkommensverschlechterungContainer);
        }

    }

    public initFinSit() {
        if (!this.finanzielleSituationContainerGS1) {
            this.finanzielleSituationContainerGS1 = new TSFinanzielleSituationContainer();
            this.finanzielleSituationContainerGS1.jahr = this.basisjahr;
            this.finanzielleSituationContainerGS1.finanzielleSituationJA = new TSFinanzielleSituation();
        }

        if (this.gesuchsteller2Required && !this.finanzielleSituationContainerGS2) {
            this.finanzielleSituationContainerGS2 = new TSFinanzielleSituationContainer();
            this.finanzielleSituationContainerGS2.jahr = this.basisjahr;
            this.finanzielleSituationContainerGS2.finanzielleSituationJA = new TSFinanzielleSituation();
        }
    }

    copyFinSitDataToGesuch(gesuch: TSGesuch): TSGesuch {
        gesuch.extractFamiliensituation().gemeinsameSteuererklaerung = this.gemeinsameSteuererklaerung;
        gesuch.extractFamiliensituation().sozialhilfeBezueger = this.sozialhilfeBezueger;
        gesuch.extractFamiliensituation().verguenstigungGewuenscht = this.verguenstigungGewuenscht;
        gesuch.gesuchsteller1.finanzielleSituationContainer = this.finanzielleSituationContainerGS1;
        if (gesuch.gesuchsteller2) {
            gesuch.gesuchsteller2.finanzielleSituationContainer = this.finanzielleSituationContainerGS2;
        } else {
            if (this.finanzielleSituationContainerGS2) {
                //wenn wir keinen gs2 haben sollten wir auch gar keinen solchen container haben
                console.log('illegal state: finanzielleSituationContainerGS2 exists but no gs2 is available');
            }
        }
        this.resetSteuerveranlagungErhalten(gesuch);
        return gesuch;
    }

    /**
     * if gemeinsameSteuererklaerung has been set to true and steuerveranlagungErhalten ist set to true for the GS1
     * as well, then we need to set steuerveranlagungErhalten to true for the GS2 too, if it exists.
     */
    private resetSteuerveranlagungErhalten(gesuch: TSGesuch) {
        if (gesuch.extractFamiliensituation().gemeinsameSteuererklaerung === true
            && gesuch.gesuchsteller1 && gesuch.gesuchsteller2
            && gesuch.gesuchsteller1.finanzielleSituationContainer.finanzielleSituationJA.steuerveranlagungErhalten === true) {

            gesuch.gesuchsteller2.finanzielleSituationContainer.finanzielleSituationJA.steuerveranlagungErhalten = true;
        }
    }

    copyEkvSitDataToGesuch(gesuch: TSGesuch): TSGesuch {
        gesuch.einkommensverschlechterungInfoContainer = this.einkommensverschlechterungInfoContainer;
        gesuch.gesuchsteller1.einkommensverschlechterungContainer = this.einkommensverschlechterungContainerGS1;
        if (gesuch.gesuchsteller2) {
            gesuch.gesuchsteller2.einkommensverschlechterungContainer = this.einkommensverschlechterungContainerGS2;
        } else {
            if (this.einkommensverschlechterungContainerGS2) {
                //wenn wir keinen gs2 haben sollten wir auch gar keinen solchen container haben
                console.log('illegal state: einkommensverschlechterungContainerGS2 exists but no gs2 is available');
            }
        }
        return gesuch;
    }

    public getFiSiConToWorkWith(): TSFinanzielleSituationContainer {
        if (this.gesuchstellerNumber === 2) {
            return this.finanzielleSituationContainerGS2;
        } else {
            return this.finanzielleSituationContainerGS1;
        }
    }

    public getEkvContToWorkWith(): TSEinkommensverschlechterungContainer {
        if (this.gesuchstellerNumber === 2) {
            return this.einkommensverschlechterungContainerGS2;
        } else {
            return this.einkommensverschlechterungContainerGS1;
        }
    }

    public getEkvToWorkWith(): TSEinkommensverschlechterung {
        if (this.gesuchstellerNumber === 2) {
            return this.getEkvOfBsj_JA(this.einkommensverschlechterungContainerGS2);
        } else {
            return this.getEkvOfBsj_JA(this.einkommensverschlechterungContainerGS1);
        }
    }

    private getEkvOfBsj_JA(einkommensverschlechterungContainer: TSEinkommensverschlechterungContainer): TSEinkommensverschlechterung {
        if (this.basisjahrPlus === 2) {
            return einkommensverschlechterungContainer.ekvJABasisJahrPlus2;
        } else {
            return einkommensverschlechterungContainer.ekvJABasisJahrPlus1;
        }
    }

    public getEkvToWorkWith_GS(): TSEinkommensverschlechterung {
        if (this.gesuchstellerNumber === 2) {
            return this.getEkvOfBsj_GS(this.einkommensverschlechterungContainerGS2);
        } else {
            return this.getEkvOfBsj_GS(this.einkommensverschlechterungContainerGS1);
        }
    }

    private getEkvOfBsj_GS(einkommensverschlechterungContainer: TSEinkommensverschlechterungContainer): TSEinkommensverschlechterung {
        if (this.basisjahrPlus === 2) {
            return einkommensverschlechterungContainer.ekvGSBasisJahrPlus2;
        } else {
            return einkommensverschlechterungContainer.ekvGSBasisJahrPlus1;
        }
    }

    public getGesuchstellerNumber(): number {
        return this.gesuchstellerNumber;
    }

    public isGesuchsteller2Required(): boolean {
        return this.gesuchsteller2Required;
    }

    get einkommensverschlechterungContainerGS1(): TSEinkommensverschlechterungContainer {
        return this._einkommensverschlechterungContainerGS1;
    }

    set einkommensverschlechterungContainerGS1(value: TSEinkommensverschlechterungContainer) {
        this._einkommensverschlechterungContainerGS1 = value;
    }

    get einkommensverschlechterungContainerGS2(): TSEinkommensverschlechterungContainer {
        return this._einkommensverschlechterungContainerGS2;
    }

    set einkommensverschlechterungContainerGS2(value: TSEinkommensverschlechterungContainer) {
        this._einkommensverschlechterungContainerGS2 = value;
    }

    get einkommensverschlechterungInfoContainer(): TSEinkommensverschlechterungInfoContainer {
        return this._einkommensverschlechterungInfoContainer;
    }

    set einkommensverschlechterungInfoContainer(value: TSEinkommensverschlechterungInfoContainer) {
        this._einkommensverschlechterungInfoContainer = value;
    }

    public initEinkommensverschlechterungContainer(basisjahrPlus: number, gesuchstellerNumber: number): void {

        if (gesuchstellerNumber === 1) {
            if (!this.einkommensverschlechterungContainerGS1) {
                this.einkommensverschlechterungContainerGS1 = new TSEinkommensverschlechterungContainer();
            }

            if (basisjahrPlus === 1) {
                if (!this.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1) {
                    this.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
                }
            }

            if (basisjahrPlus === 2) {
                if (!this.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2) {
                    this.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
                }
                // Wenn z.B. in der Periode 2016/2017 eine Einkommensverschlechterung für 2017 geltend gemacht wird,
                // ist es unmöglich, dass die Steuerveranlagung und Steuererklärung für 2017 schon dem Gesuchsteller vorliegt
                this.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.gemeinsameSteuererklaerung_BjP2 = false;
                this.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2.steuerveranlagungErhalten = false;
                this.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2.steuererklaerungAusgefuellt = false;
            }
        }

        if (gesuchstellerNumber === 2) {
            if (!this.einkommensverschlechterungContainerGS2) {
                this.einkommensverschlechterungContainerGS2 = new TSEinkommensverschlechterungContainer();
            }

            if (basisjahrPlus === 1) {
                if (!this.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1) {
                    this.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
                }
            }

            if (basisjahrPlus === 2) {
                if (!this.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2) {
                    this.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
                }
                // Wenn z.B. in der Periode 2016/2017 eine Einkommensverschlechterung für 2017 geltend gemacht wird,
                // ist es unmöglich, dass die Steuerveranlagung und Steuererklärung für 2017 schon dem Gesuchsteller vorliegt
                this.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.gemeinsameSteuererklaerung_BjP2 = false;
                this.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2.steuerveranlagungErhalten = false;
                this.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2.steuererklaerungAusgefuellt = false;
            }
        }
    }

    public getGemeinsameSteuererklaerungToWorkWith(): boolean {
        if (this.basisjahrPlus === 2) {
            return this.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.gemeinsameSteuererklaerung_BjP2;
        } else {
            return this.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.gemeinsameSteuererklaerung_BjP1;
        }
    }

    getBasisJahrPlus() {
        return this.basisjahrPlus;
    }
}
