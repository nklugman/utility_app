#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  2 20:51:15 2017

@author: CHIAMAO_SHIH
"""

import fiona 
import shapely.geometry
from map_display import GoogleMapPlotter

kenya_shape = fiona.open("Kenya_sublocations/kenya_sublocations.shp")

def region_check(area_list):
    points_list = []
    id_list = []
    for area in area_list:
        points_list.append(GoogleMapPlotter.geocode(area))
        print("The coordinates of ", area, "is: ",GoogleMapPlotter.geocode(area))
    count = 0
    for point in points_list:
        this_point = shapely.geometry.Point(point[1],point[0])
        ##SWAP the coordinates!!!!!!!!
        ##Longtitude goes first in shapely
        for shapefile_rec in kenya_shape:
            if(shapely.geometry.asShape(shapefile_rec['geometry']).contains(this_point)):
                print("%s is in Kenya with region id =" %(area_list[count]) ,shapefile_rec['id'], ".")
                id_list.append(shapefile_rec['id'])
                points_num = 0
                for lat_and_long in shapefile_rec['geometry']['coordinates'][0]:
                    points_num += 1
                #print("number of points is: ", points_num)
                break;
            if(shapefile_rec['id'] == str(len(kenya_shape)-1)):
                print("%s is not in Kenya." %(area_list[count]))
        count += 1;
    print(id_list)
    return id_list

if __name__ == '__main__':
    area_list = ["Nairobi", "UC Berkeley", "Bamburi", "Mazeras, Coast Region, Kenya"]
    region_check(area_list);