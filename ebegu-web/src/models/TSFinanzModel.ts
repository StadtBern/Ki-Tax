import TSFinanzielleSituationContainer from './TSFinanzielleSituationContainer';
import TSGesuch from './TSGesuch';
import TSFinanzielleSituation from './TSFinanzielleSituation';
import TSEinkommensverschlechterungContainer from './TSEinkommensverschlechterungContainer';
import TSEinkommensverschlechterung from './TSEinkommensverschlechterung';
import TSEinkommensverschlechterungInfoContainer from './TSEinkommensverschlechterungInfoContainer';

export default class TSFinanzModel {

    private _gemeinsameSteuererklaerung: boolean;
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

        if (gesuch.extractFamiliensituation().gemeinsameSteuererklaerung) {
            this.gemeinsameSteuererklaerung = angular.copy(gesuch.extractFamiliensituation().gemeinsameSteuererklaerung);
        } else {
            this.gemeinsameSteuererklaerung = false;
        }
        this.finanzielleSituationContainerGS1 = angular.copy(gesuch.gesuchsteller1.finanzielleSituationContainer);
        if (gesuch.gesuchsteller2) {
            this.finanzielleSituationContainerGS2 = angular.copy(gesuch.gesuchsteller2.finanzielleSituationContainer);
        }
        this.initFinSit();
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
        gesuch.gesuchsteller1.finanzielleSituationContainer = this.finanzielleSituationContainerGS1;
        if (gesuch.gesuchsteller2) {
            gesuch.gesuchsteller2.finanzielleSituationContainer = this.finanzielleSituationContainerGS2;
        } else {
            if (this.finanzielleSituationContainerGS2) {
                //wenn wir keinen gs2 haben sollten wir auch gar keinen solchen container haben
                console.log('illegal state: finanzielleSituationContainerGS2 exists but no gs2 is available');
            }
        }
        return gesuch;
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
