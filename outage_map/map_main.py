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

##################################
###Fetching list of outage area###
##################################
print("-----Fetching area list from Database...-")
con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
con.autocommit = True
cur = con.cursor()
cur.execute("SELECT area FROM outages WHERE index=1")
rows = cur.fetchall()
area_list =[]
for row in rows:
	area_list.append(row[0])
print("-----Area list fetched.")

##############################################################
###Find the IDs of sublocations that covers the given area.###
##############################################################
print("-----Identifying the locations of each area.")
id_list = []
id_list = region_define.region_check(area_list)
print("-----The IDs of corresponding sublocations found.")

#############################################
####From id list to polygon on Google Map.###
#############################################
print("-----Displaying the outage map through Google Map.")
kenya_shape = fiona.open("Kenya_sublocations/kenya_sublocations.shp")
default_blue = '#00008B'
##mymap = display_logic.GoogleMapPlotter(-1.2921, 36.8219, 16) ##Setting the center of the map displayed.
mymap = display_logic.GoogleMapPlotter.from_geocode("Nairobi") ##Setting the center of the map displayed.
for point in id_list:
    mymap.polygon(display_logic.get_long_path(point), display_logic.get_lat_path(point))
time_now = datetime.datetime.now()
mymap.draw('./%s.html' %time_now.date())
print("-----Map is good to go.")


