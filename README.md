# Ki-Tax

System zur Verwaltung von Vergünstigungen für die externe Kinderbetreuung

## Getting Started

Ki-Tax is a online portal through which parents can apply for financial support for childcare outside of the family. It 
was implemented in 2017 for the City of Bern.

The following childcare services are available:

* Kitas, for children not yet at school (Jugendamt)
* Tagis, for school-age children (Jugendamt)
* All-day schools (Schulamt)
* Childminders (Jugendamt)

In Ki-Tax sind folgende Rollen abgebildet:
* Gesuchsteller: Stellt einen Antrag mit einem oder mehreren Betreuungsangeboten für ein oder mehrere Kinder
* Sachbearbeiter Jugendamt: Bearbeitet die eingehenden Gesuche und Mutationsmeldungen. Geht das Gesuch auf dem Postweg 
ein, werden die Gesuchdaten durch den Sachbearbeiter erfasst. Darüber hinaus kann der Sachbearbeiter die Zahlungen an 
die Kitas einsehen und freigeben sowie als ISO20022-File exportieren. Auch Statistiken können durch den Sachbearbeiter
Jugendamt erstellt werden.
* Rechtsdienst / Jurist: Wird involviert, wenn eine Beschwerde eingereicht wird. Der Rechtsdienst hat nur Lesezugriff
* Revisor: Der Revisor des Jugendamts fungiert als Revisionsstelle und prüft die Auszahlungen, die das Jugendamt an die 
Institutionen getätigt hat. Dazu erhält er Lesezugriff auf die relevanten Daten.
* Administrator: Neben den Funktionen die für den Sachbearbeiter freigeschaltet sind, hat dieser Benutzer Zugriff auf 
Administrationsfunktionen, wie z.B. die Verwaltung der Institutionen und Trägerschaften
* Schulamt: Die Schulamt User übernehmen fachlich eine ähnliche Funktion wie die Sachbearbeiter des Jugendamts. Das 
heisst, sie prüfen die vom Gesuchsteller eingegebenen Daten und entscheiden über deren Annahme. Sie prüfen nur Gesuche 
die das Betreuungsangebot Tagesschule betreffen
* Steuerverwaltung: Die Benutzer der Steuerverwaltung können im Auftrag des Jugendamts gemeldete Einkommens- und 
Vermögensdaten der Gesuchsteller prüfen. Diese Rolle hat Zugriff auf Dossiers die vom Sachbearbeiter zur Prüfung durch 
die Steuerverwaltung markiert wurden. Der Benutzer kann lesend auf die eingegebenen Gesuchdaten zugreifen und wo 
vorgesehen kommentieren.

### Installation

Im Dokument 'Ki-Tax_Installationsanweisung' finden Sie weitere Informationen zu Installation und Betrieb.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Contributing Guidelines

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for the process for submitting pull requests to us.

## Code of Conduct

One healthy social atmospehere is very important to us, wherefore we rate our Code of Conduct high.
 For details check the file [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

## Authors

* **DV Bern AG** - *Initial work* - [dvbern](https://github.com/dvbern)

See also the list of [contributors](https://github.com/StadtBern/Ki-Tax/contributors)
 who participated in this project.

## License

This project is licensed under the GNU Affero General Public License - see the [LICENSE.md](LICENSE.md) file for details.

