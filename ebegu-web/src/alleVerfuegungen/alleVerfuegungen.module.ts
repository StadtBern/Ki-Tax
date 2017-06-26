import {EbeguWebCore} from '../core/core.module';
import {AlleVerfuegungenViewComponentConfig} from './component/alleVerfuegungenView/alleVerfuegungenView';
import {alleVerfuegungenRun} from './alleVerfuegungen.route';

export const EbeguWebAlleVerfuegungen =
    angular.module('ebeguWeb.alleVerfuegungen', [EbeguWebCore.name])
        .run(alleVerfuegungenRun)
        .component('alleVerfuegungenView', new AlleVerfuegungenViewComponentConfig());
