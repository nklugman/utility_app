#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jan 16 18:19:30 2017

1.Download all tweets of a user.
2.Into a csv
3.Data include: screen_name, name, year, month, data, time, tweet_id, tweet
4.Still need: Time of collecting
5.Use Tweet_Update.py to update, and this one for initializing.
@author: CHIAMAO_SHIH
"""

import pandas as pd  #to store tweets into csv
import tweepy

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

def get_all_tweets(screen_name):
	alltweets = []	
	new_tweets = api.user_timeline(screen_name = screen_name,count=200)
	alltweets.extend(new_tweets)
	oldest = alltweets[-1].id - 1
	while len(new_tweets) > 0:
		print ("getting tweets before %s" % (oldest))
		new_tweets = api.user_timeline(screen_name = screen_name,count=200,max_id=oldest)
		alltweets.extend(new_tweets)
		oldest = alltweets[-1].id - 1
		print ("...%s tweets downloaded so far" % (len(alltweets)))
	data=[[obj.user.screen_name, \
        obj.created_at.year, \
        obj.created_at.month,obj.created_at.day, \
        "%s.%s"%(obj.created_at.hour,obj.created_at.minute), 
        obj.id_str, \
        obj.text.encode("utf8")] for obj in alltweets ]
	dataframe=pd.DataFrame(data,columns=['screen_name', \
                                      'year', \
                                      'month', \
                                      'date', \
                                      'time', \
                                      'tweet_id', \
                                      'tweet'])
	dataframe.to_csv("%s_tweets.csv"%(screen_name),index=False)

	


if __name__ == '__main__':
	#pass in the username of the account you want to download
	get_all_tweets(twitter_KPLC)