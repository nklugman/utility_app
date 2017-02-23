#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  2 20:51:15 2017

@author: CHIAMAO_SHIH
"""

import fiona 
import shapely.geometry
import pandas as pd
from google_map_plotter import GoogleMapPlotter

#kenya_constituency = fiona.open("../Kenya_Shapefiles/Constituency Boundaries/constituencies.shp") #Suggested by Jay
kenya_constituency = fiona.open("../Kenya_Shapefiles/Constituency_Simplified/constituencies_simplified.shp") #Simplified by QFIS
unknown_csv_file = 'unknown_area'
def region_check(area_list, shpfile):
    location_undefined = []
    points_list = []
    effect_area = []
    ##Find the Lat & Long of a given string.
    for area in area_list:
        try:
            points_list.append(GoogleMapPlotter.geocode(area)) ##(lat, lng)
            effect_area.append(area)
            print("The coordinates of ", area, "is: ",points_list[-1])
        except IndexError:
            location_undefined.append(area)
    print("Areas google map don't recognize: ", location_undefined)
    
    ##Check which pieces of shapefile contain points given   
    shp_idx = 0
    effect_id = []
    outage_info = []
    for shapefile_rec in shpfile:
        effect_list = []
        count = 0
        for point in points_list:
            this_point = shapely.geometry.Point(point[1],point[0])
            if(shapely.geometry.asShape(shapefile_rec['geometry']).contains(this_point)):
                effect_list.append(effect_area[count])
                if(count not in effect_id):
                    effect_id.append(shp_idx)
            count += 1
        if(len(effect_list)!= 0):
            outage_info.append([shp_idx, len(effect_list), effect_list])
        shp_idx += 1
    print(outage_info)
    new_data=[[obj]for obj in location_undefined]
    
    ##Documents the unkonwn area.
    try:
        data=pd.read_csv("%s.csv"%(unknown_csv_file))
        count = 0
        for count in range(0,len(data)):
            for new in new_data:
                if(data[unknown_csv_file].loc[count] == new[0]):
                    new_data.remove(new)
        dataframe=pd.DataFrame(new_data,columns=[unknown_csv_file])
        dataframe=[dataframe,data]
        dataframe=pd.concat(dataframe)
        dataframe.to_csv("unknown_area.csv",index=False)
    except OSError:
        dataframe=pd.DataFrame(new_data,columns=[unknown_csv_file])
        dataframe.to_csv("%s.csv"%(unknown_csv_file))

    return outage_info

if __name__ == '__main__':
    area_list = ["Nairobi", "University of Nairobi", "Mazeras, Coast Region, Kenya", "aaaapek", "UC Berkeley", "oooppqqq"]
    region_check(area_list, kenya_constituency);