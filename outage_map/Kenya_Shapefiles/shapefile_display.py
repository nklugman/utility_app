#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Feb 14 13:52:07 2017

@author: CHIAMAO_SHIH
"""

import fiona 
#import shapely.geometry

kenya_subloc = fiona.open("5th_level_sublocations/kenya_sublocations.shp") # num = 3715
kenya_loc = fiona.open("4th_level_locations/kenya_locations.shp") # num = 1143
kenya_divisions = fiona.open("3rd_level_divisions_II/kenya_divisions.shp") # num = 300
kenya_county = fiona.open("County/County.shp") # num = 47

def properties_display(shape_file): 
    print(shape_file[0]['properties'])
    #print(shape_file[0]['properties']['LOCATION'])

def count_num(shape_file):
    print(len(kenya_divisions))
    
if __name__ == '__main__':
    properties_display(kenya_loc);
    #count_num(kenya_divisions)