import psycopg2
import matplotlib.pyplot as plt
import numpy as np

con = psycopg2.connect(database='outage_map', user='postgres')
con.autocommit = True
cur = con.cursor()

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

cur.execute("SELECT region, area, sum(length) FROM outages GROUP BY region, area ORDER BY sum(length) DESC")
rows = cur.fetchall()
for row in rows:
    print(row[0], row[1],row[2])

cur.execute("SELECT end_time, COUNT(end_time) FROM outages GROUP BY region,end_time HAVING region='MT KENYA REGION' or region='MT. KENYA REGION' ORDER BY COUNT(end_time) DESC")
rows = cur.fetchall()
for row in rows:
    print(row[0], row[1])