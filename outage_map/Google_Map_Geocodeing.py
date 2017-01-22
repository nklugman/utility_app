#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Jan 21 18:28:09 2017

@author: CHIAMAO_SHIH
"""


goole_map_API_KEY = "AIzaSyDgHS0aTLt1ZQLZbrwtU21bcbNsRzydKH0"

import gmplot

"""
gmap = gmplot.GoogleMapPlotter(37.428, -122.145, 16)
gmap.plot(latitudes, longitudes, 'cornflowerblue', edge_width=10)
gmap.scatter(more_lats, more_lngs, '#3B0B39', size=40, marker=False)
gmap.scatter(marker_lats, marker_lngs, 'k', marker=True)
gmap.heatmap(heat_lats, heat_lngs)
"""

gmap = gmplot.GoogleMapPlotter.from_geocode("San Francisco")
gmap.draw("mymap.html")