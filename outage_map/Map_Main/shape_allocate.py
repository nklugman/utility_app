#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Feb 14 16:59:00 2017

@author: CHIAMAO_SHIH
"""
import fiona 
import shapely.geometry
import datetime

kenya_subloc = fiona.open("../Kenya_Shapefiles/5th_level_sublocations/kenya_sublocations.shp") # num = 3715
kenya_loc = fiona.open("../Kenya_Shapefiles/4th_level_locations/kenya_locations.shp") # num = 1143
kenya_divisions = fiona.open("../Kenya_Shapefiles/3rd_level_divisions_II/kenya_divisions.shp") # num = 300
kenya_county = fiona.open("../Kenya_Shapefiles/County/County.shp") # num = 47

lower_shp = kenya_subloc
upper_shp = kenya_divisions

def get_lower_center(shpfile):
    center_list = []
    for shape in shpfile:
        count = 0
        lat_sum = 0
        long_sum = 0
        center_id = []
        for coordinates in shape['geometry']['coordinates'][0]:
            long_sum = coordinates[0] + long_sum
            lat_sum = coordinates[1] + lat_sum
            count += 1
        center_id = [long_sum/count, lat_sum/count, shape['id']]
        center_list.append(center_id)
    return center_list
def allocate_lower_to_upper(center_list, lower, upper):
    print("Allocating started at: ", datetime.datetime.now())
    allocation = []
    lower_id_list = []
    for upper_shape in upper:
        lower_id_list = []
        for point in center_list:
            this_point = shapely.geometry.Point(point[0],point[1])
            if(shapely.geometry.asShape(upper_shape['geometry']).contains(this_point)):
                lower_id_list.append(point[2])
        allocation.append(lower_id_list)
    print(allocation)
    print("Allocating ended at: ", datetime.datetime.now())
    count = 0
    for item in allocation:
        if(len(item) == 0):
            print("---id = %s: Contains nço lower level administrative divsion." %(count))
        count += 1;
    return(allocation, lower, upper)
if __name__ == '__main__':
    #allocate_lower_to_upper(get_lower_center(kenya_subloc), kenya_subloc, upper_shp)
    get_lower_center(kenya_subloc)


