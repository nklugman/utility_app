#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Feb 14 16:59:00 2017

@author: CHIAMAO_SHIH
"""
import fiona 
import shapely.geometry

kenya_subloc = fiona.open("../Kenya_Shapefiles/5th_level_sublocations/kenya_sublocations.shp") ## num = 3714
kenya_loc = fiona.open("../Kenya_Shapefiles/4th_level_locations/kenya_locations.shp")
kenya_divisions = fiona.open("../Kenya_Shapefiles/3rd_level_divisions_II/kenya_divisions.shp") ## num = 262
kenya_county = fiona.open("../Kenya_Shapefiles/County/County.shp") ## num = 47

def get_lower_center(shpfile):
    center_list = []
    for shape in shpfile:
        count = 0
        lat_sum = 0
        long_sum = 0
        center_id = []
        for coordinates in shape['geometry']['coordinates']:
            #print(coordinates[0][0])
            long_sum = coordinates[0][0] + long_sum
            lat_sum = coordinates[0][1] + lat_sum
            count += 1
        center_id = [long_sum/count, lat_sum/count, shape['id']]
        center_list.append(center_id)
    #print(center_list)
    allocate_lower_to_upper(center_list, kenya_subloc, kenya_county)
        
def allocate_lower_to_upper(center_list, lower, upper):
    allocation = []
    for upper_shape in upper:
        lower_id_list = []
        for point in center_list:
            this_point = shapely.geometry.Point(point[1],point[0])
            if(shapely.geometry.asShape(upper_shape['geometry']).contains(this_point)):
                lower_id_list.append(point[2])
                #center_list.pop(0) #This makes the loop incompelete.
        allocation.append(lower_id_list)
    print(allocation)
if __name__ == '__main__':
    get_lower_center(kenya_subloc)


