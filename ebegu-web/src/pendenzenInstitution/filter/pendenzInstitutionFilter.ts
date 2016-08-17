// Es wird empfohlen, Filters als normale Funktionen zu implementieren, denn es bringt nichts, dafuer eine Klasse zu implementieren.
PendenzInstitutionFilter.$inject = ['$filter'];

// Zuerst pruefen wir welcher Wert kommt, d.h. aus welcher Column. Je nach Column wird danach dem entsprechenden Comparator aufgerufen.
// Fuer mehrere Columns reicht es mit dem standard Comparator, der auch hier einfach implementiert wird.
export function PendenzInstitutionFilter($filter: any) {
    let filterFilter = $filter('filter');

    let standardComparator = function standardComparator(obj: any, text: any) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return (array: any, expression: any) => {

        function customComparator(actual: any, expected: any) {
            if (expression.institution && expression.institution === expected) {
                if (actual) {
                    return actual.name === expected;
                }
            }
            return standardComparator(actual, expected);
        }
        var output = filterFilter(array, expression, customComparator);
        return output;
    };
}
