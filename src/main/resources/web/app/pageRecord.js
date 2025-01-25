app.component('pageRecord', {
  templateUrl: 'app/pageRecord.html',
  bindings: {},
  controller: function(client) {
	  var ctrl = this;
	  
	  ctrl.types = ["A", "AAAA", "CNAME", "MX", "TXT"];
	  
	  ctrl.zoneId = null;
	  ctrl.recordId = null;
	  ctrl.zone = null;
	  ctrl.record = null;
	  
	  ctrl.url = window.location.protocol + "//" + window.location.host + "/api";
	  ctrl.token = 'TOKEN';
	  ctrl.simpleValue = '';
	  ctrl.simpleSuffix = '';
	  
	  ctrl.$routerOnActivate = function(next, previous) {
		  ctrl.zoneId = next.params.zoneId;
		  ctrl.recordId = next.params.recordId;
		  ctrl.reloadZone();
	  };
	  
	  ctrl.reloadZone = function() {
		  client.zoneGet(ctrl.zoneId).then((resp) => {
			  ctrl.zone = resp.data;
			  ctrl.record = ctrl.zone.records.find((el) => {return el.id == ctrl.recordId});
			  if(ctrl.record.token) {
			    ctrl.token = ctrl.record.token;
			  }
			  
				if("A" == ctrl.record.type) {
					ctrl.simpleValue = '127.0.0.1';
				}
				if("AAAA" == ctrl.record.type) {
					ctrl.simpleValue = '2001:0db8:85a3:0000:0000:8a2e:0370:7334';
				}
				if("CNAME" == ctrl.record.type) {
					ctrl.simpleValue = 'somewhere.example.com.';
					ctrl.simpleSuffix = ctrl.simpleValue;
				}
				if("MX" == ctrl.record.type) {
					ctrl.simpleValue = 'mailserver.example.com.';
					ctrl.simpleSuffix = ctrl.simpleValue;
				}
				if("TXT" == ctrl.record.type) {
					ctrl.simpleValue = 'SoMeTeXt_wItHouT-SpecIaL_cHaRacTers';
					ctrl.simpleSuffix = ctrl.simpleValue;
				}
				
				
		  });
	  }
  }
});
