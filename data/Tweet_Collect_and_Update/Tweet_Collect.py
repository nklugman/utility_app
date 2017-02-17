#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jan 16 18:19:30 2017
1.Download all tweets of a user.
2.Into a csv
3.Data include: screen_name, tweet_data, tweet_time, tweet_id, collecting_date, collecting_time
4.Use Tweet_Update.py to update, and this one just for initializing.
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

def get_all_tweets(screen_name):
    alltweets = []	
    new_tweets = api.user_timeline(screen_name = screen_name,count=max_count) 
    alltweets.extend(new_tweets)
    oldest = alltweets[-1].id - 1
    while len(new_tweets) > 0:
        print ("getting tweets before %s" % (oldest))
        new_tweets = api.user_timeline(screen_name = screen_name,count=max_count,max_id=oldest)
        alltweets.extend(new_tweets)
        oldest = alltweets[-1].id - 1
        print ("...%s tweets downloaded so far" % (len(alltweets)))
	
    time_now = datetime.datetime.now()
    data=[[obj.user.screen_name, \
           obj.id_str, \
           "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
            "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second), \
            obj.text.encode("utf8"), \
            "%s/%s/%s" % (time_now.year, time_now.month, time_now.day), \
            "%s:%s:%s" % (time_now.hour, time_now.minute, time_now.second)] for obj in alltweets ]
    dataframe=pd.DataFrame(data,columns=['screen_name', \
                                         'tweet_id', \
                                         'tweet_date', \
                                         'tweet_time', \
                                         'tweet', \
                                         'collecting_date', \
                                         'collecting_time'])
    dataframe.to_csv("%s_tweets.csv"%(screen_name),index=False)

if __name__ == '__main__':
    #pass in the username of the account you want to download
    get_all_tweets(twitter_KPLC)