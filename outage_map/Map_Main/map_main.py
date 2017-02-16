#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Feb 10 13:21:00 2017

@author: CHIAMAO_SHIH
"""

import shape_allocate
import region_define
import google_map_plotter as gmplot
import psycopg2
import fiona
#import datetime

#kenya_subloc = fiona.open("../Kenya_Shapefiles/5th_level_sublocations/kenya_sublocations.shp") # num = 3715
#kenya_loc = fiona.open("../Kenya_Shapefiles/4th_level_locations/kenya_locations.shp") # num = 1143
kenya_divisions = fiona.open("../Kenya_Shapefiles/3rd_level_divisions_II/kenya_divisions.shp") # num = 300
kenya_county = fiona.open("../Kenya_Shapefiles/County/County.shp") # num = 47

lower_shp = kenya_divisions
upper_shp = kenya_county


def allocate_shp(lower, upper): 
    print("-----Allocating shape of lower level to an upper one...")
    #center_list = shape_allocate.get_lower_center(lower)
    #allocation = shape_allocate.allocate_lower_to_upper(center_list, lower, upper)
    #print (allocation)
    #return allocation
    return [['0', '1', '2', '3', '10', '11', '18', '20'], ['4', '12', '13', '15', '22', '27'], ['5', '6', '7', '8', '9', '16'], ['14', '17', '19', '23', '25', '39'], ['21', '26', '29', '30', '33'], ['24', '31', '34', '35', '60'], ['28', '41', '46'], ['32', '47', '63', '69', '73', '84', '86', '102'], ['36', '37', '43', '45', '55', '70'], ['38', '40', '44'], ['42', '50', '51', '52', '53', '57', '59', '62', '64'], ['48', '79', '82', '88', '122', '124', '189', '218', '250'], ['49', '58', '71', '78'], ['66', '80', '81', '85', '93', '97', '101'], ['54', '56', '72', '76'], ['61', '67', '68', '83', '89', '104', '109'], ['65', '75', '77', '90', '91', '94', '98', '115', '127'], ['74', '87', '95', '106', '108'], ['92', '96', '103', '107', '117', '128', '131', '137', '138', '139', '148', '151', '152', '155', '157', '165', '169', '173'], ['99', '112', '116', '129', '156', '158', '163', '196', '200'], ['100', '105', '110', '113'], ['111', '118', '135', '154', '180'], ['114', '143'], ['119', '132', '140'], ['120', '123', '125', '136', '142', '145', '146', '147', '149', '150', '153', '160'], ['121', '133', '134', '159', '162', '167', '174', '191', '194'], ['126', '234', '259'], ['130', '214', '226', '236', '239', '253'], ['141', '176', '178', '197'], ['144', '164', '166', '168', '190'], ['161', '170', '171', '175', '179', '181', '182', '185', '186', '187', '195', '198'], ['172', '183', '206', '211'], ['177', '188', '192', '203'], ['184', '201', '216', '227', '231'], ['193', '202', '205', '209', '212', '213', '215', '220'], ['199', '204', '208', '210', '222', '223'], ['207', '217', '225', '237'], ['219', '224', '228', '229', '233', '238', '241'], ['221', '230', '235', '240', '249'], ['232', '257', '272'], ['242', '243', '244', '245', '246', '247', '248', '251'], ['252', '254', '255', '256', '269', '276'], ['258', '260', '261', '262', '264', '273', '274'], ['275', '281', '284', '287'], ['277', '278', '279', '280', '282'], ['286', '291', '293', '294', '295', '296'], ['288', '289']]
    
def fetch_outage_list():
    print("-----Fetching area list from Database...")
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    cur.execute("SELECT area FROM outages WHERE index>=1 and index <=30")
    rows = cur.fetchall()
    area_list =[]
    for row in rows:
        area_list.append(row[0]+", Kenya") ##Add ", Kenya" increases accuracy of finding coordinate.
    print("-----Area list fetched.")
    return area_list

    
def locate_area_to_shpID(area_list):
    print("-----Identifying the locations of each area...")
    id_list = region_define.region_check(area_list, lower_shp)
    print("-----The IDs of corresponding sublocations found.")
    return id_list


def display_google_map(id_list, allct, lower, upper):
    print("-----Displaying the outage map through Google Map...")
    count = 0
    for composition in allct:
        upper_name = upper[count]['properties']['COUNTY']#The last index based on the upper level shapefile.
        mymap = gmplot.GoogleMapPlotter.from_geocode(upper_name) ##Setting the center of the map displayed.
        num_poly = 0
        for point in id_list:
            if point in composition:
                mymap.polygon(gmplot.get_long_path('%s'%point, lower), gmplot.get_lat_path('%s'%point, lower))
                num_poly += 1
        if(num_poly > 0):
            try:
                mymap.draw('./outage_map/%s.html'%(upper_name))
            except UnboundLocalError:
                count = count; #do nothing
        count += 1

    print("-----All maps are good to go.")

if __name__ == '__main__':
    allocation = allocate_shp(lower_shp, upper_shp)
    outage_list = fetch_outage_list()
    id_list = locate_area_to_shpID(outage_list)
    display_google_map(id_list, allocation, lower_shp, upper_shp)