#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: CHIAMAO_SHIH
"""

import tweepy
import pandas as pd  #to store tweets into csv
import time
import datetime

consumer_key = 'mtBURqiQRdYdpPmBFo0ebV3cA'
consumer_secret = '7moNATGly2V2ZTlu2qxHMgjoYMIiiAurDTJ38Co7e2DtW6OX6f'
access_key = '2836818390-VWP9SmNjiDrlO4GpY6TgWgTbYGbcqv0B754Uvn1'
access_secret = 'Efsuu5Xwdjq6s915iBsEZZ3ns7m53mcmPyXUWJiQTRljp'

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_key, access_secret)

twitter_GridWatch = 'GwScanner'
twitter_TimesNow = 'TimesNow'
twitter_KPLC = 'kenyapower_care'

api = tweepy.API(auth)
max_replies = 1000 ##1000 is the maximum when replies collecting

def get_latest_replies(target):
    count=0
    latest_replies = []
    data=pd.read_csv("%s_all_replies.csv"%(target))
    since_ID=data['tweet_id'].loc[0]
    
    try:
        new_replies = [status for status in tweepy.Cursor(api.search, q=target, since_id=since_ID).items(max_replies)]
        oldest = new_replies[-1].id - 1
    except IndexError:
        print ("No new replies")
        exit()
    
        count = len(new_replies)
    latest_replies.extend(new_replies)
    print (latest_replies[0].id)
    
    while len(new_replies) > 0:
        print("10 seconds time delay start now.")
        time.sleep(10)
        print ("getting replies before %s" % (oldest))
        new_replies = [status for status in tweepy.Cursor(api.search, q=twitter_KPLC,max_id=oldest,since_id=since_ID).items(max_replies)]
        count = count + len(new_replies)
        latest_replies.extend(new_replies)
        oldest = latest_replies[-1].id - 1
        print ("...%s replies downloaded so far" % (len(latest_replies)))
        
    print (latest_replies[0].id)
    print ("count: %s" % count) 
    time_now = datetime.datetime.now()    
    print("Update untill %s" %time_now)
    
    new_data=[[obj.user.screen_name, \
           obj.id_str, \
           "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
            "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second), \
            obj.text.encode("utf8"), \
            "%s/%s/%s" % (time_now.year, time_now.month, time_now.day), \
            "%s:%s:%s" % (time_now.hour, time_now.minute, time_now.second)] for obj in latest_replies ]
    dataframe=pd.DataFrame(new_data,columns=['screen_name', \
                                         'tweet_id', \
                                         'reply_date', \
                                         'reply_time', \
                                         'reply', \
                                         'collecting_date', \
                                         'collecting_time'])
    dataframe=[dataframe,data]
    dataframe=pd.concat(dataframe)
    dataframe.to_csv("%s_all_replies.csv"%(target),index=False)

	
if __name__ == '__main__':
    #pass in the username of the account you want to download
    get_latest_replies(twitter_KPLC)