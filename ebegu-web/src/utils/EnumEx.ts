export class EnumEx {
    /**
     *
     * @param e takes an enum and returns the names of the objects as a string arry
     * @returns {string[]}
     */
    static getNames(e: any): string[] {
        return Object.keys(e).filter(v => isNaN(parseInt(v, 10))
        );
    }

    static getValues(e: any) { // TODO return type
        return Object.keys(e)
            .map(v => parseInt(v, 10))
            .filter(v => !isNaN(v));
    }

    //static getNamesAndValues(e: any) { // TODO return type
    //    return EnumEx.getValues(e)
    //        .map(v => {
    //            return {name: e[v] as string, value: v};
    //        });
    //}
}
