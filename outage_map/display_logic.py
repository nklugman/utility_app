
"""
Spyder Editor

This is a temporary script file.
"""
###Step 1: Read shapefile with FIONA
###Step 2: Given "id", find coordinateds of points
###Step 3: Feed these coordinates to Google Map
###Display!!!

import requests
import json

default_blue = '#00008B'
def safe_iter(var):
    try:
        return iter(var)
    except TypeError:
        return [var]


class GoogleMapPlotter(object): 

    def __init__(self, center_lat, center_lng, zoom):
        self.center = (float(center_lat), float(center_lng))
        self.zoom = int(zoom)
        self.paths = []
        self.shapes = []
        self.points = []
        self.radpoints = []

    @classmethod
    def from_geocode(cls, location_string, zoom=13):
        lat, lng = cls.geocode(location_string)
        return cls(lat, lng, zoom)

    @classmethod
    def geocode(self, location_string):
        geocode = requests.get(
            'http://maps.googleapis.com/maps/api/geocode/json?address="%s"' % location_string)
        geocode = json.loads(geocode.text)
        latlng_dict = geocode['results'][0]['geometry']['location']
        return latlng_dict['lat'], latlng_dict['lng']

    def _process_kwargs(self, kwargs):
        settings = dict()
        settings["edge_color"] = default_blue
        settings["edge_alpha"] = 1.0
        settings["edge_width"] = 1.0
        settings["face_alpha"] = 0.3
        settings["face_color"] = default_blue
        settings["color"] = default_blue

        for key, color in settings.items():
            if 'color' in key:
                settings[key] = default_blue

        settings["closed"] = kwargs.get("closed", None)
        return settings

    def polygon(self, lats, lngs, **kwargs):
        settings = self._process_kwargs(kwargs)
        shape = zip(lats, lngs)
        self.shapes.append((shape, settings))

    # create the html file which include one google map and all points and
    # paths
    def draw(self, htmlfile):
        f = open(htmlfile, 'w')
        f.write('<html>\n')
        f.write('<head>\n')
        f.write('<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />\n')
        f.write('<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>\n')
        f.write('<title>Google Maps - pygmaps </title>\n')
        f.write('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?libraries=visualization&sensor=true_or_false"></script>\n')
        f.write('<script type="text/javascript">\n')
        f.write('\tfunction initialize() {\n')
        self.write_map(f)
        self.write_shapes(f)
        f.write('\t}\n')
        f.write('</script>\n')
        f.write('</head>\n')
        f.write('<body style="margin:0px; padding:0px;" onload="initialize()">\n')
        f.write('\t<div id="map_canvas" style="width: 100%; height: 100%;"></div>\n')
        f.write('</body>\n')
        f.write('</html>\n')
        f.close()

    #############################################
    # # # # # # Low level Map Drawing # # # # # #
    #############################################

    def write_shapes(self, f):
        for shape, settings in self.shapes:
            self.write_polygon(f, shape, settings)

    def write_map(self,  f):
        f.write('\t\tvar centerlatlng = new google.maps.LatLng(%f, %f);\n' % (self.center[0], self.center[1]))
        f.write('\t\tvar myOptions = {\n')
        f.write('\t\t\tzoom: %d,\n' % (self.zoom))
        f.write('\t\t\tcenter: centerlatlng,\n')
        f.write('\t\t\tmapTypeId: google.maps.MapTypeId.ROADMAP\n')
        f.write('\t\t};\n')
        f.write(
            '\t\tvar map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);\n')
        f.write('\n')

    def write_polygon(self, f, path, settings):
        clickable = False
        geodesic = True
        strokeColor = settings.get('edge_color') or settings.get('color')
        strokeOpacity = settings.get('edge_alpha')
        strokeWeight = settings.get('edge_width')
        fillColor = settings.get('face_color') or settings.get('color')
        fillOpacity= settings.get('face_alpha')
        f.write('var coords = [\n')
        for coordinate in path:
            f.write('new google.maps.LatLng(%f, %f),\n' %
                    (coordinate[0], coordinate[1]))
        f.write('];\n')
        f.write('\n')

        f.write('var polygon = new google.maps.Polygon({\n')
        f.write('clickable: %s,\n' % (str(clickable).lower()))
        f.write('geodesic: %s,\n' % (str(geodesic).lower()))
        f.write('fillColor: "%s",\n' % (fillColor))
        f.write('fillOpacity: %f,\n' % (fillOpacity))
        f.write('paths: coords,\n')
        f.write('strokeColor: "%s",\n' % (strokeColor))
        f.write('strokeOpacity: %f,\n' % (strokeOpacity))
        f.write('strokeWeight: %d\n' % (strokeWeight))
        f.write('});\n')
        f.write('\n')
        f.write('polygon.setMap(map);\n')
        f.write('\n\n')

if __name__ == "__main__":

    mymap = GoogleMapPlotter(37.428, -122.145, 16)
    #mymap = GoogleMapPlotter.from_geocode("Stanford University")

    print("-------", mymap.geocode("Euclid and Hearst Ave"))

    path = [(37.429, 37.428, 37.427, 37.427, 37.427),
             (-122.145, -122.145, -122.145, -122.146, -122.146)]
    path2 = [[i+.01 for i in path[0]], [i+.02 for i in path[1]]]
    path3 = [(37.433302 , 37.431257 , 37.427644 , 37.430303),
             (-122.14488, -122.133121, -122.137799, -122.148743)]
    path4 = [(37.423074, 37.422700, 37.422410, 37.422188, 37.422274, 37.422495, 37.422962, 37.423552, 37.424387, 37.425920, 37.425937),
         (-122.150288, -122.149794, -122.148936, -122.148142, -122.146747, -122.14561, -122.144773, -122.143936, -122.142992, -122.147863, -122.145953)]
    mymap.polygon(path3[0], path3[1], edge_color="cyan", edge_width=5, face_color="blue", face_alpha=0.1)
    mymap.draw('./outage_map.html')