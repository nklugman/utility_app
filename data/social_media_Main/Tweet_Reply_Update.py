#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: CHIAMAO_SHIH
"""

import tweepy
import pandas as pd
import parsing_replies_to_server


consumer_key = 'mtBURqiQRdYdpPmBFo0ebV3cA'
consumer_secret = '7moNATGly2V2ZTlu2qxHMgjoYMIiiAurDTJ38Co7e2DtW6OX6f'
access_key = '2836818390-VWP9SmNjiDrlO4GpY6TgWgTbYGbcqv0B754Uvn1'
access_secret = 'Efsuu5Xwdjq6s915iBsEZZ3ns7m53mcmPyXUWJiQTRljp'
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_key, access_secret)
#twitter_GridWatch = 'GwScanner'
twitter_KPLC = 'kenyapower_care'
api = tweepy.API(auth)
max_replies = 1000 ##1000 is the maximum when replies collecting
id_csv = 'tweet_replies_max_id.csv'

def get_latest_replies(target):
    latest_replies = []
    try:
        data=pd.read_csv(id_csv)
        since_ID=data['max_ID'].loc[0]
    except Exception as e:
        since_ID = 826978555925983000
    
    try:
        new_replies = [status for status in tweepy.Cursor(api.search, q=target, since_id=since_ID).items(max_replies)]
        latest_replies.extend(new_replies)
    except IndexError:
        print ("No new replies")
        return
        
    ##Loop will cause exceeding rate limit.
    print("%s replies downloaded." % (len(latest_replies)))
    new_data=[]
    for obj in latest_replies:
        new_data.append([obj.text, \
               "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
               "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second)])
        next_since_ID = obj.id_str
    dataframe=pd.DataFrame(new_data,columns=['reply','reply_date','reply_time'])
    
    newest_id=pd.DataFrame([next_since_ID],columns=['max_ID'])
    newest_id.to_csv(id_csv ,index=False)
    
    parsing_replies_to_server.parsing_to_db(dataframe, 'reply', True);
    return dataframe
    
if __name__ == '__main__':
    dataframe = get_latest_replies(twitter_KPLC)
    #dataframe = pd.DataFrame.from_csv('../fake_reply.csv')

    