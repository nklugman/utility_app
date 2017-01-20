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
max_count = 200 ##200 is the maximum of count

def get_latest_tweets(screen_name):
    count=0
    latest_tweets=[]
    data=pd.read_csv("%s_tweets.csv"%(screen_name))
    since_Id=data['tweet_id'].loc[0]
	
    try:
        new_tweets=api.user_timeline(screen_name=screen_name,since_id=since_Id,count=max_count)
        oldest = new_tweets[-1].id - 1
    except IndexError:
        print ("No new tweets")
        exit()
    
    count=len(new_tweets)
    latest_tweets.extend(new_tweets)
    print (latest_tweets[0].id)
    
    while len(new_tweets) > 0:
        new_tweets=api.user_timeline(screen_name=screen_name,max_id=oldest,since_id=since_Id,count=max_count)
        count = count + len(new_tweets)
        print ("...%s tweets downloaded so far"%(len(latest_tweets)))
        latest_tweets.extend(new_tweets)
        oldest=latest_tweets[-1].id - 1
    
    print (latest_tweets[0].id)
    print ("count: %s" % count)
    time_now = datetime.datetime.now()
    print("Now: %s" %time_now)
    
    new_data=[[obj.user.screen_name, \
               obj.id_str, \
               "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
                "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second), \
                obj.text.encode("utf8"), \
                "%s/%s/%s" % (time_now.year, time_now.month, time_now.day), \
                "%s:%s:%s" % (time_now.hour, time_now.minute, time_now.second)] for obj in latest_tweets ]
    dataframe=pd.DataFrame(new_data,columns=['screen_name', \
                                             'tweet_id', \
                                             'tweet_date', \
                                             'tweet_time', \
                                             'tweet', \
                                             'collecting_date', \
                                             'collecting_time'])
    dataframe=[dataframe,data] 
    dataframe=pd.concat(dataframe)
    dataframe.to_csv("%s_tweets.csv"%(screen_name),index=False)

if __name__=='__main__':
    get_latest_tweets(twitter_KPLC)