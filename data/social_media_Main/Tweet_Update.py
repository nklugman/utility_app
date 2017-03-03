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
import psycopg2


#Twitter API credentials

consumer_key = 'mtBURqiQRdYdpPmBFo0ebV3cA'
consumer_secret = '7moNATGly2V2ZTlu2qxHMgjoYMIiiAurDTJ38Co7e2DtW6OX6f'
access_key = '2836818390-VWP9SmNjiDrlO4GpY6TgWgTbYGbcqv0B754Uvn1'
access_secret = 'Efsuu5Xwdjq6s915iBsEZZ3ns7m53mcmPyXUWJiQTRljp'
#twitter_GridWatch = 'GwScanner'
twitter_KPLC = 'kenyapower_care'
facebook_KPLC = 'KenyaPowerLtd'

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_key, access_secret)
api = tweepy.API(auth)
max_count = 200 ##200 is the maximum of count in 'user_timeline'
tweet_fetch_limit = 2800 ##actually it's some number around 3200
minutes_to_update = 1
id_csv = '"tweet_max_id.csv"'

def get_latest_tweets(screen_name):
    try:
        data=pd.read_csv(id_csv)
        since_ID=data['max_ID'].loc[0]
    except Exception as e:
        since_ID = 826978555925983000
    latest_tweets=[]    
    try:
        new_tweets=api.user_timeline(screen_name=screen_name,since_id=since_ID,count=max_count)
        latest_tweets.extend(new_tweets)
        oldest = new_tweets[-1].id - 1
        print("New tweets.")
    except IndexError:
        print ("No new tweets")
        exit()
    latest_tweets.extend(new_tweets)
    count = 0
    
    while (len(new_tweets) > 0) & (count < tweet_fetch_limit/max_count):
        new_tweets=api.user_timeline(screen_name=screen_name, max_id=oldest, since_id=since_ID,count=max_count)
        print ("...%s tweets downloaded so far"%(len(latest_tweets)))
        latest_tweets.extend(new_tweets)
        oldest=latest_tweets[-1].id - 1
        count += 1
        
    new_data = []
    for obj in latest_tweets:
        if((obj.in_reply_to_status_id == None) & (obj.in_reply_to_screen_name == None)):
            new_data.append([obj.text, \
                   "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
                   "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second)])
        next_since_ID = obj.id_str
    
    dataframe=pd.DataFrame(new_data,columns=['tweet', 'tweet_date', 'tweet_time'])

    newest_id=pd.DataFrame([next_since_ID],columns=['max_ID'])
    newest_id.to_csv(id_csv ,index=False)

    try:
        tweets_to_news_feed(dataframe)
    except Exception as e:
        print("No tweet, all replies.")
        print(e)

    return since_ID
    
def tweets_to_news_feed(tweets):
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    outages=[]
    print(len(tweets))
    for i in range(len(tweets)):
        tweets['tweet_timestamp']=tweets['tweet_date']+" "+tweets['tweet_time']+"+03"
        timeStamp = tweets.iloc[i]['tweet_timestamp']
        print(timeStamp)
        source = 1
        tweet = str(tweets.iloc[i]['tweet'])
        outages.append([timeStamp, source, tweet])
    dataText = ', '.join(map(bytes.decode,(cur.mogrify('(%s,%s,%s)',outage) for outage in outages)))
    cur.execute('INSERT INTO news_feed (time,source,content) VALUES ' + dataText)


if __name__=='__main__':
    get_latest_tweets(twitter_KPLC)