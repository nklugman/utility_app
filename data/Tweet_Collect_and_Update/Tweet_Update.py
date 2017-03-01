#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jan 16 16:48:25 2017
1.Frame source: https://github.com/suraj-deshmukh/get_tweets
2.For object of user: https://dev.twitter.com/overview/api/users
3.For object of tweet: https://dev.twitter.com/overview/api/tweets
@author: CHIAMAO_SHIH
"""

import pandas as pd
import tweepy
import datetime
import psycopg2

#Twitter API credentials

consumer_key = 'mtBURqiQRdYdpPmBFo0ebV3cA'
consumer_secret = '7moNATGly2V2ZTlu2qxHMgjoYMIiiAurDTJ38Co7e2DtW6OX6f'
access_key = '2836818390-VWP9SmNjiDrlO4GpY6TgWgTbYGbcqv0B754Uvn1'
access_secret = 'Efsuu5Xwdjq6s915iBsEZZ3ns7m53mcmPyXUWJiQTRljp'
twitter_GridWatch = 'GwScanner'
twitter_TimesNow = 'TimesNow'
twitter_KPLC = 'kenyapower_care'

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_key, access_secret)
api = tweepy.API(auth)
max_count = 200 ##200 is the maximum of count in 'user_timeline'
tweet_fetch_limit = 3000 ##actually it's some number around 3500

def get_latest_tweets(screen_name, since_ID):
    latest_tweets=[]    
    ############################################################
    ####TODO: Get the 'tweet_id' of the newest tweet on database
    ############################################################  
    try:
        new_tweets=api.user_timeline(screen_name=screen_name,since_id=since_ID,count=max_count)
        latest_tweets.extend(new_tweets)
        oldest = new_tweets[-1].id - 1
    except IndexError:
        print ("No new tweets")
        exit()
    
    counting = len(new_tweets)
    latest_tweets.extend(new_tweets)
    
    while len(latest_tweets) < tweet_fetch_limit:
        new_tweets=api.user_timeline(screen_name=screen_name, max_id=oldest, since_id=since_ID,count=max_count)
        counting = counting + len(new_tweets)
        print ("...%s tweets downloaded so far"%(len(latest_tweets)))
        latest_tweets.extend(new_tweets)
        oldest=latest_tweets[-1].id - 1

    print (latest_tweets[0].id)
    print ("count: %s" % counting)
    time_now = datetime.datetime.now()
    print("Now: %s" %time_now)
    new_data = []
    for obj in latest_tweets:
        if((obj.in_reply_to_status_id == None) & (obj.in_reply_to_screen_name == None)):
            new_data.append([obj.user.screen_name, \
                   obj.id_str, \
                   "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
                    "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second), \
                    obj.text, \
                    "%s/%s/%s" % (time_now.year, time_now.month, time_now.day), \
                    "%s:%s:%s" % (time_now.hour, time_now.minute, time_now.second), \
                    obj.in_reply_to_status_id, \
                    obj.in_reply_to_status_id == None, \
                    obj.in_reply_to_screen_name])
    since_ID = (latest_tweets[-1].id_str)
    dataframe=pd.DataFrame(new_data,columns=['screen_name', \
                                             'tweet_id', \
                                             'tweet_date', \
                                             'tweet_time', \
                                             'tweet', \
                                             'collecting_date', \
                                             'collecting_time', \
                                             'in_reply_to_status_id', \
                                             'not_reply',
                                             'in_reply_to_screen_name'])
    
    try:
        parsing_to_news_feed(dataframe)
    except Exception as e:
        print(e)
    print(since_ID)
    return since_ID

def parsing_to_news_feed(tweets):
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    outages=[]
    print(len(tweets))
    for i in range(len(tweets)):
        tweets['tweet_timestamp']=tweets['tweet_date']+" "+tweets['tweet_time']+"+03"
        time = tweets.iloc[i]['tweet_timestamp']
        source = 1
        tweet = str(tweets.iloc[i]['tweet'])
        outages.append([time,source,tweet])
    dataText = ', '.join(map(bytes.decode,(cur.mogrify('(%s,%s,%s)',outage) for outage in outages)))
    cur.execute('INSERT INTO news_feed (time,source,content) VALUES ' + dataText)

    
if __name__=='__main__':
    sinceID = 833227432403730432
    get_latest_tweets(twitter_KPLC, sinceID)