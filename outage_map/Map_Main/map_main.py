#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Feb 10 13:21:00 2017

@author: CHIAMAO_SHIH
"""

import region_define
import google_map_plotter as gmplot
import psycopg2
import fiona
import datetime

kenya_subloc = fiona.open("../Kenya_Shapefiles/5th_level_sublocations/kenya_sublocations.shp") # num = 3715
kenya_loc = fiona.open("../Kenya_Shapefiles/4th_level_locations/kenya_locations.shp") # num = 1143
kenya_divisions = fiona.open("../Kenya_Shapefiles/3rd_level_divisions_II/kenya_divisions.shp") # num = 300
kenya_county = fiona.open("../Kenya_Shapefiles/County/County.shp") # num = 47

shapefile = kenya_subloc

def fetch_outage_list():
    print("-----Fetching area list from Database...")
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    cur.execute("SELECT area FROM outages WHERE index>=1 and index <=30")
    rows = cur.fetchall()
    area_list =[]
    for row in rows:
        area_list.append(row[0]+", Kenya") ##Add ", Kenya" increases accuracy of finding coordinate.
    print("-----Area list fetched.")
    return area_list

def locate_area_to_shpID(area_list):
    print("-----Identifying the locations of each area...")
    id_list = region_define.region_check(area_list, shapefile)
    print("-----The IDs of corresponding sublocations found.")
    return id_list

def display_google_map(id_list):
    print("-----Displaying the outage map through Google Map...")
    mymap = gmplot.GoogleMapPlotter.from_geocode("Nairobi") ##Setting the center of the map displayed.   
    for point in id_list:
        mymap.polygon(gmplot.get_long_path(point, shapefile),
                      gmplot.get_lat_path(point, shapefile),
                      alpha = 0.5,
                      edge_width = 3.5,
                      face_color = "#8B0000",
                      color = gmplot.outage_color,
                      face_alpha=0.1)
    mymap.draw('./%s.html' %datetime.datetime.now())
    print("Outage map is good to go.")

if __name__ == '__main__':
    outage_list = fetch_outage_list()
    id_list = locate_area_to_shpID(outage_list)
    display_google_map(id_list)