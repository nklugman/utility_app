#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: CHIAMAO_SHIH
"""

import tweepy
import pandas as pd  #to store tweets into csv

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

def get_all_replies(screen_name):
	all_replies = []
	new_replies = [status for status in tweepy.Cursor(api.search, q=twitter_KPLC).items(max_replies)]
	all_replies.extend(new_replies)
	oldest = all_replies[-1].id - 1
	while len(new_replies) > 0:
		print ("getting replies before %s" % (oldest))
		new_replies = [status for status in tweepy.Cursor(api.search, q=twitter_KPLC,max_id=oldest).items(max_replies)]
		all_replies.extend(new_replies)
		oldest = all_replies[-1].id - 1
		print ("...%s tweets downloaded so far" % (len(all_replies)))
	data=[[obj.user.screen_name, \
        obj.id_str, \
        "%s/%s/%s" % (obj.created_at.year, obj.created_at.month, obj.created_at.day), \
        "%s:%s:%s" % (obj.created_at.hour,obj.created_at.minute, obj.created_at.second), \
        obj.text.encode("utf8")] for obj in all_replies ]
	dataframe=pd.DataFrame(data,columns=['screen_name', \
                                      'tweet_id', \
                                      'tweet_date', \
                                      'tweet_time', \
                                      'tweet'])
	dataframe.to_csv("%s_all_replies.csv"%(screen_name),index=False)

	
if __name__ == '__main__':
	#pass in the username of the account you want to download
	get_all_replies(twitter_KPLC)