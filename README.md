# Ki-Tax

System for the management of external childcare subsidies

## Getting Started

Ki-Tax is an online portal through which parents can apply for financial support for childcare outside of the family. It was implemented in 2017 for the City of Bern.
 
The following childcare services are available:
 
* Kitas, for children not yet at school (Youth Office)
* Tagis, for school-age children (Youth Office)
* All-day schools (Education Office)
* Childminders (Youth Office)

The following roles are represented in Ki-Tax:
 
* **Applicant**: Can place an online application for one or more childcare services for one or more children.
* **Youth Office**: Processes incoming and updated applications. If an application is submitted by post, the application is entered into the system by the clerk. In addition, they can also view and approve payments to childcare centres, as well as export as an ISO20022 file. Statistics can also be created by this role.
* **Legal services / Lawyer**: Are involved when a dispute relating to an application has been submitted. The legal services have read only access.
* **Auditor**: The auditor of the Youth Office acts as a revision body and audits the subsidies which the Youth Office has made to institutions. For this purpose, they receive read access to relevant data.
* **Administrator**: In addition to the functions that are enabled for the clerk, this user has access to functions such as the administration of institutions and sponsors.
* **Education Office**: The Education Office takes on a similar role to the Youth Office.  They can assess and process applications but only those involving all-day schools.
* **Tax Office**: Tax Office users can validate the income and assets of the applicants on behalf of the Youth Office. This role has access to dossiers that have been marked by the administrator for review by the Tax Office. Read only access to the application is provided with the ability to comment where relevant.

### Installation

Refer to the file [Ki-Tax Installation Instructions](Ki-Tax_Installationshandbuch.pdf) (german) for more information on installation and operation.

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

