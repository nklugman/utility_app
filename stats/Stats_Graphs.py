#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import psycopg2
import matplotlib.pyplot as plt
import numpy as np

con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='graphs.grid.watch', port='5432')
con.autocommit = True
cur = con.cursor()

def printall():
    cur.execute("SELECT region, area, COUNT(area) FROM outages GROUP BY region, area HAVING COUNT(area)>=3 ORDER BY COUNT(area) DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0], row[1],row[2])
    
    cur.execute("SELECT length, COUNT(length) FROM outages GROUP BY length ORDER BY length DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0], row[1])
    
    cur.execute("SELECT start_time, COUNT(start_time) FROM outages GROUP BY start_time ORDER BY COUNT(start_time) DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0], row[1])
    
    cur.execute("SELECT end_time, COUNT(end_time) FROM outages GROUP BY end_time ORDER BY COUNT(end_time) DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0], row[1])
    
    cur.execute("SELECT DISTINCT region,COUNT(*) FROM outages GROUP BY region ORDER BY COUNT(*) DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0],row[1])
        
    cur.execute("SELECT region, area, sum(length) FROM outages GROUP BY region, area ORDER BY sum(length) DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0], row[1],row[2])

    cur.execute("SELECT end_time, COUNT(end_time) FROM outages GROUP BY region,end_time HAVING region='MT KENYA REGION' or region='MT. KENYA REGION' ORDER BY COUNT(end_time) DESC")
    rows = cur.fetchall()
    for row in rows:
        print(row[0], row[1])
        





"""        
cur.execute("SELECT length FROM outages")
rows = cur.fetchall()
a=np.array([])
for row in rows:
    time_float = row[0].total_seconds() /3600
    a=np.insert(a,0,time_float)
plt.hist(a,bins=np.arange(0,15.5,0.5))
plt.xlim(0,15)
plt.xlabel('Length of outage')
plt.show()

cur.execute("SELECT length FROM outages WHERE region='NORTH EASTERN REGION'")
rows = cur.fetchall()
a=np.array([])
for row in rows:
    time_float = row[0].total_seconds() /3600
    a=np.insert(a,0,time_float)
plt.hist(a,bins=np.arange(0,15.5,0.5),color='magenta')
plt.xlim(0,15)
plt.xlabel('Length of outages in NORTH EASTERN REGION')
plt.show()
"""

def printregion(userregion):
    cur.execute("SELECT length FROM outages WHERE region=" + userregion)
    rows = cur.fetchall()
    a=np.array([])
    for row in rows:
        time_float = row[0].total_seconds() /3600
        a=np.insert(a,0,time_float)
    plt.hist(a,bins=np.arange(0,15.5,0.5),color='blue')
    plt.xlim(0,15)
    plt.xlabel('Length of outages in ' + userregion)
    plt.show()
    
def printallregions():
    cur.execute("SELECT region, area, COUNT(area) FROM outages GROUP BY region, area HAVING COUNT(area)>=3 ORDER BY COUNT(area) DESC")
    rows = cur.fetchall()
    checkedregions = []
    bool = True
    for row in rows:
        for region in checkedregions:
            if region == row[0]:
                bool = False
        if bool == True:
            printregion(str("'" + row[0] + "'"))
            checkedregions.append(row[0])
        bool = True
#printallregions()

def printarea(userarea):
    cur.execute("SELECT length FROM outages WHERE area=" + userarea)
    rows = cur.fetchall()
    a=np.array([])
    for row in rows:
        time_float = row[0].total_seconds() /3600
        a=np.insert(a,0,time_float)
    plt.hist(a,bins=np.arange(0,15.5,0.5),color='purple')
    plt.xlim(0,15)
    plt.xlabel('Length of outages in ' + userarea)
    plt.show()
        
def printallareas():
    cur.execute("SELECT region, area, COUNT(area) FROM outages GROUP BY region, area HAVING COUNT(area)>=3 ORDER BY COUNT(area) DESC")
    rows = cur.fetchall()
    checkedareas = []
    bool = True
    for row in rows:
        for area in checkedareas:
            if area == row[1]:
                bool = False
        if bool == True:
            printarea(str("'" + row[1] + "'"))
            checkedareas.append(row[1])
        bool = True
        
"""
def printstart():
    cur.execute("SELECT start_time, COUNT(start_time) FROM outages GROUP BY start_time ORDER BY COUNT(start_time) DESC")
    rows = cur.fetchall()
    tx = ''
    x = np.array([])
    for row in rows:
        #print(row[0], row[1])
        tx = str(row[0])
        tx = tx[:2]
        tx = int(tx)
        x = np.insert(tx)
        print (x)
"""
    
#printstart()
printallregions()
printallareas()

