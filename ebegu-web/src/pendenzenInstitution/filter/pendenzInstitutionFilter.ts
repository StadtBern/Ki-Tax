// Es wird empfohlen, Filters als normale Funktionen zu implementieren, denn es bringt nichts, dafuer eine Klasse zu implementieren.
import EbeguUtil from '../../utils/EbeguUtil';
PendenzInstitutionFilter.$inject = ['$filter', 'EbeguUtil'];

// Zuerst pruefen wir welcher Wert kommt, d.h. aus welcher Column. Je nach Column wird danach dem entsprechenden Comparator aufgerufen.
// Fuer mehrere Columns reicht es mit dem standard Comparator, der auch hier einfach implementiert wird.
export function PendenzInstitutionFilter($filter: any, ebeguUtil: EbeguUtil) {

    let filterFilter = $filter('filter');
    let dateFilter = $filter('date');

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
            if (expression.eingangsdatum && expression.eingangsdatum === expected) {
                let actualDate = dateFilter(new Date(actual), 'dd.MM.yyyy');
                return actualDate === expected;
            }
            if (expression.geburtsdatum && expression.geburtsdatum === expected) {
                let actualDate = dateFilter(new Date(actual), 'dd.MM.yyyy');
                return actualDate === expected;
            }
            if (expression.gesuchsperiodeString && expression.gesuchsperiodeString === expected) {
                let gesuchsperiodeString = actual.gesuchsperiodeString;
                return gesuchsperiodeString === expected;
            }
            return standardComparator(actual, expected);
        }
        return filterFilter(array, expression, customComparator);
    };
}
