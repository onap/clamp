require('./node_modules/jquery/dist/jquery.min.js');
require('./node_modules/angular/angular.min.js');
require('./node_modules/angular-mocks/angular-mocks.js');
require('./node_modules/angular-route/angular-route.min.js');
require('./node_modules/angular-resource/angular-resource.min.js');
require('./node_modules/angular-cookies/angular-cookies.min.js');
require('./node_modules/angular-animate/angular-animate.min.js');
require('./node_modules/angular-sanitize/angular-sanitize.min.js');
require('./node_modules/angular-touch/angular-touch.min.js');
require('./node_modules/popper.js/dist/umd/popper.min.js');
require('./node_modules/bootstrap/dist/js/bootstrap.min.js');
require('./node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js');
require('./node_modules/angular-loading-bar/src/loading-bar.js');
require('./node_modules/angular-dialog-service/dist/dialogs.js');
require('./scripts/app.js');
require('./scripts/DashboardCtrl.js');

describe('Dashboard ctrl tests', function() {

	beforeEach(angular.mock.module('clds-app'));

	var $controllerService;
	
	beforeEach(angular.mock.inject(function(_$controller_) {
		$controllerService = _$controller_;
	}));

	describe('$scope.showPalette', function() {

		it('test showPalette', function() {

			var $scopeTest = {};
			var $rootScopeTest = {};
			var $resourceTest = {};
			var $httpTest = {};
			var $timeoutTest = {};
			var $locationTest = {};
			var $intervalTest = function(){};
			var $controllerDashboard = $controllerService('DashboardCtrl', {
			    '$scope' : $scopeTest,
			    '$rootScope' : $rootScopeTest,
			    '$resource' : $resourceTest,
			    '$http' : $httpTest,
			    '$timeout' : $timeoutTest,
			    '$location' : $locationTest,
			    '$interval' : $intervalTest
			});
			$scopeTest.showPalette();
			expect($rootScopeTest.isModel).toEqual(true);
		});
	});
});