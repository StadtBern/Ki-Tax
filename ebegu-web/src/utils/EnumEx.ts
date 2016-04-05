module ebeguWeb.utils {
    'use strict';

    export class EnumEx {
        /**
         *
         * @param e takes an enum and returns the names of the objects as a string array
         * @returns {string[]}
         */
        static getNames(e:any): Array<string> {
            return Object.keys(e).filter(v => isNaN(parseInt(v, 10))
            );
        }

        static getValues(e:any) {
            return Object.keys(e).map(v => parseInt(v, 10)
            ).filter(v => !isNaN(v)
            );
        }

        static getNamesAndValues(e:any) {
            return EnumEx.getValues(e).map(v => { return {name = e[v] as string, value: v};
            });
        }


    }
}
