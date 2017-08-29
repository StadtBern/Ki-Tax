import TSAbstractAntragDTO from './TSAbstractAntragDTO';

export default class TSFallAntragDTO extends TSAbstractAntragDTO {

    private _fallID: string;

    public static readonly serverClassName = 'JaxFallAntragDTO';

    constructor(fallID?: string, fallNummer?: number, familienName?: string) {

        super(fallNummer, familienName);
        this._fallID = fallID;
    }

    public get fallID(): string {
        return this._fallID;
    }

    public set fallID(fallID: string) {
        this._fallID = fallID;
    }
}
