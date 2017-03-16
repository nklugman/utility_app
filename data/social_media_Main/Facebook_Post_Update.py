#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar  1 17:59:21 2017

@author: CHIAMAO_SHIH
"""

import facebook
import pandas as pd
import psycopg2
import datetime
import pytz
import parsing_replies_to_server

access_token = '1838162886420379|1a9ae182f2a68c3a4dd20665bd8395a4'
facebook_KPLC = 'KenyaPowerLtd'
last_post_csv = 'facebook_last_time'
graph = facebook.GraphAPI(access_token)
def fetch(user):
    now_kenya = datetime.datetime.now(pytz.timezone('Africa/Nairobi'))
    try:
        data=pd.read_csv(last_post_csv+'.csv')
        last_time = data[last_post_csv].loc[0]
    except Exception as e:
        last_time = '2017-03-01 06:24:57'

    feed = graph.get_object('/KenyaPowerLtd/' +'posts', since='%s'%last_time, until='%s'%now_kenya, limit=100)

    if(len(feed['data']) > 0):
        return get_data(feed)
    else:
        print("No new post on Facebook.")
        return
    
def get_data(posts):
    data=[]
    for post in posts['data']:
        #print(post['id'])
        data.append([post['message'], post['id'], post['created_time']])
    print("Posts from Facebook are fetched.")
    latest_post_time = timeStamp_parsing(posts['data'][-1]['created_time'])
    dataframe=pd.DataFrame([latest_post_time],columns=[last_post_csv])
    dataframe.to_csv(last_post_csv+'.csv',index=False)
    print(latest_post_time)
    post_to_database(data)
    get_comment(data)
    return latest_post_time

def get_comment(posts):
    new_data = []
    for post in posts:
        comments = graph.get_connections(id=post[1], connection_name='comments')
        for message in comments['data']:
            new_data.append([message['message'], timeStamp_parsing(message['created_time'])])
    
    dataframe=pd.DataFrame(new_data,columns=['reply','reply_timestamp'])
    parsing_replies_to_server.parsing_to_db(dataframe, 'reply', False)     
            
def timeStamp_parsing(timeStamp):
    timeStamp = timeStamp.replace("T", " ")
    return timeStamp
    
def post_to_database(data):
    dataframe=pd.DataFrame(data,columns=['message', 'id', 'created_time'])
    #dataframe.to_csv("Facebook Post.csv",index=False)
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
        timeStamp = timeStamp_parsing(fbposts.iloc[i]['created_time'].encode('utf-8'))
        fbposts['time_stamp'] = timeStamp
        source = 0
        post = fbposts.iloc[i]['message'].encode('utf-8')
        outages.append([timeStamp, source, post])
    dataText = ', '.join(cur.mogrify('(%s,%s,%s)',outage) for outage in outages)
    cur.execute('INSERT INTO news_feed (time,source,content) VALUES ' + dataText)    
    print("Posts are pushed to database.")
    
if __name__=='__main__':
    fetch(facebook_KPLC)