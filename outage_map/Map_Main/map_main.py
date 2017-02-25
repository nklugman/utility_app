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

#kenya_constituency = fiona.open("../Kenya_Shapefiles/Constituency Boundaries/constituencies.shp") #Suggested by Jay
kenya_constituency = fiona.open("../Kenya_Shapefiles/Constituency_Simplified/constituencies_simplified.shp") #Simplified by QGIS
shapefile = kenya_constituency


def fetch_outage_list():
    print("-----Fetching area list from Database...")
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    
    cur.execute("SELECT area FROM outages WHERE index>=1 and index <=10")
    rows = cur.fetchall()
    area_matrix =[]
    area_list_pdf = []
    area_list_reply = []
    for row in rows:
        area_list_pdf.append(row[0])
    print("-----Area list from PDF file on database is fetched.")
    
    cur.execute("SELECT area FROM social_media WHERE index>=2 and index <=11") 
    rows = cur.fetchall()
    for row in rows:
        area_list_reply.append(row[0])
    print("-----Area list from social media on database is fetched.")
    area_matrix = [area_list_pdf, area_list_reply]
    print("-----All area list fetched.")
    
    locate_area_to_shpID(area_matrix)
    return area_matrix

def locate_area_to_shpID(area_matrix):
    print("-----Identifying the locations of each area...")
    pdf_outage_info = region_define.region_check(area_matrix[0], shapefile)
    reply_outage_info = region_define.region_check(area_matrix[1], shapefile)
    outage_info_matrix = [pdf_outage_info, reply_outage_info]
    display_google_map(outage_info_matrix)
    return outage_info_matrix

def display_google_map(outage_info_matrix):
    print("-----Displaying the outage map through Google Map...")
    mymap = gmplot.GoogleMapPlotter.from_geocode("Nairobi") ##Setting the center of the map displayed.
    
    ###Draw Polygon of PDF
    for point in outage_info_matrix[0]:
        mymap.polygon(gmplot.get_long_path(point[0], shapefile),
                      gmplot.get_lat_path(point[0], shapefile),
                      event_num = point[1],
                      events = point[2],  
                      alpha = 0.5,
                      edge_width = 3.5,
                      face_color = "red",
                      color = "red",
                      face_alpha=0.1)

    ###Draw Polygon of Social Media Reply
    for point in outage_info_matrix[1]:
        mymap.polygon(gmplot.get_long_path(point[0], shapefile),
                      gmplot.get_lat_path(point[0], shapefile),
                      event_num = point[1],
                      events = point[2],  
                      alpha = 0.5,
                      edge_width = 3.5,
                      edge_color = "black",
                      color = "black",
                      face_alpha=0.1)
    

    mymap.draw('./maps_html/%s.html' %datetime.datetime.now())
    
    ################################
    ###TODO: Push the map to server.
    ################################
    print("Outage map is good to go.")

if __name__ == '__main__':
    fetch_outage_list()
    