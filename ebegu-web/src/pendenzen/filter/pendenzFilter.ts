import EbeguUtil from '../../utils/EbeguUtil';

// Es wird empfohlen, Filters als normale Funktionen zu implementieren, denn es bringt nichts, dafuer eine Klasse zu implementieren.
PendenzFilter.$inject = ['$filter', 'EbeguUtil'];

// Zuerst pruefen wir welcher Wert kommt, d.h. aus welcher Column. Je nach Column wird danach dem entsprechenden Comparator aufgerufen.
// Fuer mehrere Columns reicht es mit dem standard Comparator, der auch hier einfach implementiert wird.
export function PendenzFilter($filter: any, ebeguUtil: EbeguUtil) {
    let filterFilter = $filter('filter');
    let dateFilter = $filter('date');

    let standardComparator = function standardComparator(obj: any, text: any) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return (array: any, expression: any) => {
console.log('expression: ', expression);
        function customComparator(actual: any, expected: any) {
console.log('expected: ', expected);
            if (expression.eingangsdatum && expression.eingangsdatum === expected) {
console.log('entra en eingangsdatum: ');
                let actualDate = dateFilter(new Date(actual), 'dd.MM.yyyy');
                return actualDate === expected;
            }
            if (expression.fallNummer && expression.fallNummer === expected) {
                let actualString = ebeguUtil.addZerosToNumber(actual, 6);
                return actualString.indexOf(expected) >= 0;
            }
            if (expression.gesuchsperiode && expression.gesuchsperiode === expected) {
                let gesuchsperiodeString = ebeguUtil.getGesuchsperiodeAsString(actual);
                return gesuchsperiodeString === expected;
            }

            return standardComparator(actual, expected);
        }
        var output = filterFilter(array, expression, customComparator);
        return output;
    };
}
