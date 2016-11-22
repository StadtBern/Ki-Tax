import TSAbstractEntity from './TSAbstractEntity';
import TSAbwesenheit from './TSAbwesenheit';

export default class TSAbwesenheitContainer extends TSAbstractEntity {

    private _abwesenheitGS: TSAbwesenheit;
    private _abwesenheitJA: TSAbwesenheit;

    constructor(abwesenheitGS?: TSAbwesenheit, abwesenheitJA?: TSAbwesenheit) {
        super();
        this._abwesenheitGS = abwesenheitGS;
        this._abwesenheitJA = abwesenheitJA;
    }

    get abwesenheitGS(): TSAbwesenheit {
        return this._abwesenheitGS;
    }

    set abwesenheitGS(value: TSAbwesenheit) {
        this._abwesenheitGS = value;
    }

    get abwesenheitJA(): TSAbwesenheit {
        return this._abwesenheitJA;
    }

    set abwesenheitJA(value: TSAbwesenheit) {
        this._abwesenheitJA = value;
    }
}
