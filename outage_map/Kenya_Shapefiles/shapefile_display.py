#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Feb 14 13:52:07 2017

@author: CHIAMAO_SHIH
"""

import fiona 
#import shapely.geometry

#kenya_subloc = fiona.open("other_shapefiles/5th_level_sublocations/kenya_sublocations.shp") # num = 3715
#kenya_loc = fiona.open("other_shapefiles/4th_level_locations/kenya_locations.shp") # num = 1143
#kenya_divisions = fiona.open("other_shapefiles/3rd_level_divisions_II/kenya_divisions.shp") # num = 300
#kenya_county = fiona.open("other_shapefiles/County/County.shp") # num = 47
#kenya_constituency = fiona.open("Constituency Boundaries/constituencies.shp") #Suggested by Jay
kenya_constituency = fiona.open("Constituency_Simplified/constituencies_simplified.shp")

target = kenya_constituency
def properties_display(shape_file):
    for prop in shape_file[0]['properties']:
        print(prop)
    print(shape_file[0]['properties'])
def count_num(shape_file):
    print(len(target))
    
if __name__ == '__main__':
    properties_display(target);
    #count_num(kenya_divisions)