import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
let template = require('./verfuegenView.html');
require('./verfuegenView.less');


export class VerfuegenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenViewController;
    controllerAs = 'vm';
}

export class VerfuegenViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'EbeguUtil'];

    private nombres: string[] = ['hola', 'adios'];

    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil) {
        super(state, gesuchModelManager, undefined);
        this.initViewModel();
    }

    private initViewModel(): void {
    }

    public cancel(): void {
        this.state.go('gesuch.verfuegen');
    }

    public getVerfuegungZeitabschnitte(): Array<string> {
        //todo beim zeitabschnitte vom Server holen
        // this.verfuegenRS.getVerfuegen(betreuung).then((response) => {
        //     this.gesuchModelManager.setVerfuegenToWorkWith(response);
        // });

        return this.nombres;
    }

    public getFall() {
        if (this.gesuchModelManager && this.gesuchModelManager.gesuch) {
            return this.gesuchModelManager.gesuch.fall;
        }
        return undefined;
    }

    public getGesuchsperiode() {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getGesuchsperiode();
        }
        return undefined;
    }

    public getKindName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getKindToWorkWith().kindJA) {
            return this.gesuchModelManager.getKindToWorkWith().kindJA.getFullName();
        }
        return undefined;
    }

    public getInstitutionName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten) {
            return this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten.institution.name;
        }
        return undefined;
    }

    public getBetreuungNumber(): string {
        if (this.ebeguUtil && this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.ebeguUtil.calculateBetreuungsId(this.getGesuchsperiode(), this.getFall(), this.gesuchModelManager.getKindToWorkWith().kindNummer,
                this.gesuchModelManager.getBetreuungToWorkWith().betreuungNummer);
        }
        return undefined;
    }

    public getBetreuungsstatus(): TSBetreuungsstatus {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus;
        }
        return undefined;
    }

}
