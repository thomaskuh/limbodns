app.component('pageRecord', {
  templateUrl: 'app/pageRecord.html',
  bindings: {},
  controller: function(client) {
	  var ctrl = this;
	  
	  ctrl.types = ["A", "AAAA", "CNAME"];
	  
	  ctrl.zoneId = null;
	  ctrl.recordId = null;
	  ctrl.zone = null;
	  ctrl.record = null;
	  
	  ctrl.uurl = {auto: '', manu: '', wget: ''};
		
	  ctrl.$routerOnActivate = function(next, previous) {
		  ctrl.zoneId = next.params.zoneId;
		  ctrl.recordId = next.params.recordId;
		  ctrl.reloadZone();
	  };
	  
	  ctrl.reloadZone = function() {
		  client.zoneGet(ctrl.zoneId).then((resp) => {
			  ctrl.zone = resp.data;
			  ctrl.record = ctrl.zone.records.find((el) => {return el.id == ctrl.recordId});
			  
			  ctrl.uurl.auto = window.location.protocol + "//" + window.location.host + "/update/" + ctrl.record.token;
					
				if("A" == ctrl.record.type) {
					ctrl.uurl.manu = ctrl.uurl.auto + "/127.0.0.1";
					ctrl.uurl.wget = 'wget --inet4-only -qO - ' + ctrl.uurl.auto;
				}
				if("AAAA" == ctrl.record.type) {
					ctrl.uurl.manu = ctrl.uurl.auto + "/2001:0db8:85a3:0000:0000:8a2e:0370:7334";
					ctrl.uurl.wget = 'wget --inet6-only -qO - ' + ctrl.uurl.auto;
				}
				
		  });
	  }
  }
});
