var app = tkDefaultApp({authRouteAfterLogout: ['Login']});

app.factory('client', ['$http', function($http) {
	return {
		probe: () => $http.get('/api/probe'),
		zoneGets: () => $http.get('/api/zones'),
		zoneGet: (id) => $http.get('/api/zone/' + id),
		zoneCreate: (body) => $http.post('/api/zone', body),
		zoneDelete: (id) => $http.delete('/api/zone/' + id),
		recordGet: (zoneId, recordId) => $http.get('/api/zone/' + zoneId + '/record/' + recordId),
		recordCreate: (zoneId, body) => $http.post('/api/zone/' + zoneId + '/record', body),
		recordDelete: (zoneId, recordId) => $http.delete('/api/zone/' + zoneId + '/record/' + recordId),
		recordUpdate: (zoneId, recordId, body) => $http.post('/api/zone/' + zoneId + '/record/' + recordId, body)
	}
}]);

app.component('app', {
	templateUrl: 'app/app.html',
	bindings: { $router: '<' },
	$routeConfig: [
		{path: '/home', name: 'Home', component: 'pageHome', useAsDefault: true},
		{path: '/login', name: 'Login', component: 'tkLoginForm'},
		{path: '/zone/:zoneId', name: 'Zone', component: 'pageZone'},
		{path: '/zone/:zoneId/:recordId', name: 'Record', component: 'pageRecord'}
	],
	controller: function($scope, $location, $element, $rootRouter) {
		var ctrl = this;
		ctrl.path = '';

		$scope.$on('$routeChangeSuccess', function(scope, current, pre) {
			ctrl.path = $location.path();
		});
	}
});
