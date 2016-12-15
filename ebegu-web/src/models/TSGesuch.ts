import TSKindContainer from './TSKindContainer';
import TSAbstractAntragEntity from './TSAbstractAntragEntity';
import TSFamiliensituation from './TSFamiliensituation';
import TSEinkommensverschlechterungInfo from './TSEinkommensverschlechterungInfo';
import {TSAntragTyp} from './enums/TSAntragTyp';
import TSGesuchstellerContainer from './TSGesuchstellerContainer';
import TSEinkommensverschlechterungInfoContainer from './TSEinkommensverschlechterungInfoContainer';
import TSFamiliensituationContainer from './TSFamiliensituationContainer';

export default class TSGesuch extends TSAbstractAntragEntity {

    private _gesuchsteller1: TSGesuchstellerContainer;
    private _gesuchsteller2: TSGesuchstellerContainer;
    private _kindContainers: Array<TSKindContainer>;
    private _familiensituationContainer: TSFamiliensituationContainer;
    private _einkommensverschlechterungInfoContainer: TSEinkommensverschlechterungInfoContainer;
    private _bemerkungen: string;
    private _laufnummer: number;


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

    get laufnummer(): number {
        return this._laufnummer;
    }

    set laufnummer(value: number) {
        this._laufnummer = value;
    }

    public isMutation(): boolean {
        return this.typ === TSAntragTyp.MUTATION;
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
}
