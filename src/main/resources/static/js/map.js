$(document).ready(function()
{
	initMap();
	
});

function initMap()
{
	var map = new ol.Map({
        target: 'map',
        layers: [
          new ol.layer.Tile({
            source: new ol.source.OSM()
          })
        ],
        view: new ol.View({
          center: ol.proj.fromLonLat([19.851044, 45.246496]),
          zoom: 17
        })
      });
}