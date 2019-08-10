app.component('pageHome', {
  templateUrl: 'app/pageHome.html',
  bindings: {},
  controller: function(client) {
	  var ctrl = this;

	  ctrl.zones = [];
	  ctrl.zoneNew = {name: 'example.org.', nameserver: 'nameserver.provider.org.'};

	  ctrl.reload = function() {
		  client.zoneGets().then((resp) => {ctrl.zones = resp.data});
	  }
	  
	  ctrl.zoneCreate = function() {
	    client.zoneCreate(ctrl.zoneNew).then(() => ctrl.reload());
	  };
	  
	  ctrl.zoneDelete = function(valZone) {
		client.zoneDelete(valZone.name).then(() => ctrl.reload());
	  };
	  
	  ctrl.reload();
  }
});
