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

kenya_subloc = fiona.open("../Kenya_Shapefiles/5th_level_sublocations/kenya_sublocations.shp") # num = 3715
#kenya_loc = fiona.open("../Kenya_Shapefiles/4th_level_locations/kenya_locations.shp") # num = 1143
#kenya_divisions = fiona.open("../Kenya_Shapefiles/3rd_level_divisions_II/kenya_divisions.shp") # num = 300
#kenya_county = fiona.open("../Kenya_Shapefiles/County/County.shp") # num = 47
unknown_csv_file = 'unknown_area'
def region_check(area_list, shpfile):
    points_list = []
    id_list = []
    location_undefined = []
    for area in area_list:
        try:
            points_list.append(GoogleMapPlotter.geocode(area)) ##(lat, lng)
            print("The coordinates of ", area, "is: ",points_list[-1])
        except IndexError:
            location_undefined.append(area)
    count = 0
    for point in points_list:
        this_point = shapely.geometry.Point(point[1],point[0])
        ##Longtitude goes first in shapely, while others have Latitude goes first.
        for shapefile_rec in shpfile:
            if(shapely.geometry.asShape(shapefile_rec['geometry']).contains(this_point)):
                print("%s is in Kenya with region id =" %(area_list[count]) ,shapefile_rec['id'], ".")
                if(shapefile_rec['id'] not in id_list):
                    id_list.append(shapefile_rec['id'])
                points_num = 0
                for lat_and_long in shapefile_rec['geometry']['coordinates'][0]:
                    points_num += 1
                break;
            if(shapefile_rec['id'] == str(len(shpfile)-1)):
                print("%s is not in Kenya." %(area_list[count]))
        count += 1;
    print("Areas google map don't recognize: ", location_undefined)
    
    new_data=[[obj]for obj in location_undefined]
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
    
    print("new_data: ", new_data)
    
    
    print("id_list: ", id_list)
    return id_list

if __name__ == '__main__':
    area_list = ["Nairobi", "UC Berkeley", "Bamburi", "Mazeras, Coast Region, Kenya", "aaaapek", "oooppqqq"]
    region_check(area_list, kenya_subloc);