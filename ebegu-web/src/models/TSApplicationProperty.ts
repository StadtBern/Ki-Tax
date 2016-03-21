module ebeguWeb.API {

    export class TSApplicationProperty extends TSAbstractEntity {
        private _name:string;
        private _value:string;

        constructor(name:string, value:string) {
            super();
            this._name = name;
            this._value = value;
        }

        public set name(name:string) {
            this._name = name;
        }

        public set value(value:string) {
            this._value = value;
        }

        public get name():string {
            return this._name;
        }

        public get value():string {
            return this._value;
        }
    }
}
