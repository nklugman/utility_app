#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Feb 10 13:21:00 2017

@author: CHIAMAO_SHIH
"""

import region_define
import display_logic
import psycopg2
import fiona
import datetime

####area_list = ["Nairobi", "UC Berkeley", "Bamburi", "Mazeras, Coast Region, Kenya"]
####Fetching list of outage area
con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
con.autocommit = True
cur = con.cursor()
cur.execute("SELECT area FROM outage WHERE index=0")
rows = cur.fetchall()
area_list =[]
for row in rows:
	area_list.append(row[0])
####From area list to id list. The polygon of the id covers the given area.
id_list = []
id_list = region_define.region_check(area_list)

####From id list to polygon on Google Map.
kenya_shape = fiona.open("Kenya_sublocations/kenya_sublocations.shp")
default_blue = '#00008B'
mymap = display_logic.GoogleMapPlotter(-1.2921, 36.8219, 16)
mymap = display_logic.GoogleMapPlotter.from_geocode("Nairobi")
for point in id_list:
    mymap.polygon(display_logic.get_long_path(point), display_logic.get_lat_path(point), edge_color="cyan", edge_width=5, face_color="blue", face_alpha=0.1)
time_now = datetime.datetime.now()
mymap.draw('./%s.html' %time_now)


