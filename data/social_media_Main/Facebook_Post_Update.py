#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar  1 17:59:21 2017

@author: CHIAMAO_SHIH
"""

import facebook
import pandas as pd
import psycopg2

access_token = '1838162886420379|1a9ae182f2a68c3a4dd20665bd8395a4'
facebook_KPLC = 'KenyaPowerLtd'

def fetch(user):
    graph = facebook.GraphAPI(access_token)
    profile = graph.get_object(user)
    posts = graph.get_connections(profile['id'], 'posts')
    return get_data(posts)
    
def get_data(posts):
    #print(post['created_time'])
    data=[]
    for post in posts['data']:
        data.append([post['message'], post['id'], post['created_time']])
    return post_to_csv(data)
    
def post_to_csv(data):
    #time_now = datetime.datetime.now()
    dataframe=pd.DataFrame(data,columns=['message', 'id', 'created_time'])
    try:
        facebook_posts_to_news_feed(dataframe)
    except Exception as e:
        print(e)

def facebook_posts_to_news_feed(fbposts):
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    outages=[]
    for i in range(len(fbposts)):
        timeStamp = fbposts.iloc[i]['created_time'].replace("T", " ")
        timeStamp = timeStamp.replace('+0000',"")
        timeStamp = timeStamp + "+03"
        fbposts['time_stamp'] = timeStamp
        source = 0
        post = str(fbposts.iloc[i]['message'])
        outages.append([timeStamp, source, post])
    dataText = ', '.join(map(bytes.decode,(cur.mogrify('(%s,%s,%s)',outage) for outage in outages)))
    cur.execute('INSERT INTO news_feed (time,source,content) VALUES ' + dataText)    

if __name__=='__main__':
    fetch(facebook_KPLC)