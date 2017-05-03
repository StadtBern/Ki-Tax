import EbeguUtil from '../../utils/EbeguUtil';

// Es wird empfohlen, Filters als normale Funktionen zu implementieren, denn es bringt nichts, dafuer eine Klasse zu implementieren.
PosteingangFilter.$inject = ['$filter', 'EbeguUtil', 'CONSTANTS'];

// Zuerst pruefen wir welcher Wert kommt, d.h. aus welcher Column. Je nach Column wird danach dem entsprechenden Comparator aufgerufen.
// Fuer mehrere Columns reicht es mit dem standard Comparator, der auch hier einfach implementiert wird.
export function PosteingangFilter($filter: any, ebeguUtil: EbeguUtil, CONSTANTS: any) {
    let filterFilter = $filter('filter');
    let dateFilter = $filter('date');

    let standardComparator = function standardComparator(obj: any, text: any) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return (array: any, expression: any) => {
        function customComparator(actual: any, expected: any) {
            // Von
            if (expression.sender && expression.sender === expected) {
                return actual.getFullName().toUpperCase().indexOf(expected.toUpperCase()) >= 0;
            }
            // Fall-Nummer
            if (expression.fall && expression.fall.fallNummer && expression.fall.fallNummer === expected) {
                let actualString = ebeguUtil.addZerosToNumber(actual, CONSTANTS.FALLNUMMER_LENGTH);
                return actualString.indexOf(expected) >= 0;
            }
            // Familie
            if (expression.fall && expression.fall.besitzer && expression.fall.besitzer === expected) {
                return actual.getFullName().toUpperCase().indexOf(expected.toUpperCase()) >= 0;
            }
            // Datum gesendet
            if (expression.sentDatum && expression.sentDatum === expected) {
                return compareDates(actual, expected);
            }
            // Verantwortlicher
            if (expression.empfaenger && expression.empfaenger === expected) {
                return actual.getFullName().indexOf(expected) >= 0;
            }
            return standardComparator(actual, expected);
        }
        return filterFilter(array, expression, customComparator);
    };

    function compareDates (actual: any, expected: any): boolean {
        let datum = dateFilter(new Date(actual), 'dd.MM.yyyy');
        return datum === expected;
    }
}
