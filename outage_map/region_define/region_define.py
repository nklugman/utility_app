#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  2 15:07:17 2017

@author: CHIAMAO_SHIH
"""

import fiona 
import shapely.geometry

fc = fiona.open("AC_All_Final_April_2012/AC_All_Final.shp")
##shapefile_record = fc.next() ##Original but buggy
for shapefile_rec in fc:
    print(shapefile_rec['properties'])
    

shapefile_record = next(iter(fc))
polygon = shapefile_record['geometry']
shape = shapely.geometry.asShape(polygon)
def is_constituency(lat,lng):
    point = shapely.geometry.Point(lat,lng)
    ##print ("polygon.bounds: %s" %(polygon.bounds))
    '''
    print ("shape.contains(point): %s" %(shape.contains(point)))
    print("shapefile_record['properties']['Latitude']: %s" %(shapefile_record['properties']['Latitude']))
    print("shapefile_record['properties']['Longitude']: %s" %(shapefile_record['properties']['Longitude']))
    print("shape.bounds: ", shape.bounds)
    print(shapefile_record['properties']['DIST_NAME'])
    '''
    
if __name__ == '__main__':
    is_constituency(78.8900, 30.9787);