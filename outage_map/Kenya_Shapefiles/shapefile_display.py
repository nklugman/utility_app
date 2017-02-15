#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Feb 14 13:52:07 2017

@author: CHIAMAO_SHIH
"""

import fiona 
import shapely.geometry

kenya_subloc = fiona.open("5th_level_sublocations/kenya_sublocations.shp")
kenya_loc = fiona.open("4th_level_locations/kenya_locations.shp")
kenya_divisions = fiona.open("3rd_level_divisions_II/kenya_divisions.shp")
kenya_county = fiona.open("County/County.shp")

def properties_display(): 
    print(kenya_subloc[0]['properties'])
    print(kenya_loc[0]['properties'])
    print(kenya_divisions[0]['properties'])
    print(kenya_county[0]['properties'])


if __name__ == '__main__':
    properties_display();
    #lower_level_distribute;