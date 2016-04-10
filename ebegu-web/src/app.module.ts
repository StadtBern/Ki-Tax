import 'angular';
import './app.module.less';

import core from './core/core.module';

// export default angular.module('ebeguWeb', ['ebeguWeb.core', 'ebeguWeb.admin', 'ebeguWeb.gesuch']);
export default angular.module('ebeguWeb', [core.name]);
