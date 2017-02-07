#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  2 20:51:15 2017

@author: CHIAMAO_SHIH
"""

import fiona 
import shapely.geometry

kenya_shape = fiona.open("Kenya_sublocations/kenya_sublocations.shp")

def region_check(points_list):
    count = 0
    for point in points_list:
        this_point = shapely.geometry.Point(point[1],point[0]) ##longtitude goes first
        for shapefile_rec in kenya_shape:
            if(shapely.geometry.asShape(shapefile_rec['geometry']).contains(this_point)):
                print("Point %s is in Kenya with region id =" %(count) ,shapefile_rec['id'], ".")
                for lat_and_long in shapefile_rec['geometry']['coordinates'][0]:
                    print(lat_and_long)
                ##print(shapefile_rec['geometry'])
                break;
            if(shapefile_rec['id'] == str(len(kenya_shape)-1)):
                print("Point %s is not in Kenya." %(count))
        count += 1;

if __name__ == '__main__':
    ##(lat, long) of Nairobi = (1.2921° S, 36.8219° E)
    points_list = [(36.8219, -1.2921),(-1.2920, 36.8218), (500, 500)]
    region_check(points_list);