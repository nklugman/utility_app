#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar  1 20:10:48 2017

@author: CHIAMAO_SHIH
"""

import Tweet_Update
import Tweet_Reply_Update
import Facebook_Post_Update
import datetime
import time

twitter_KPLC = 'kenyapower_care'
facebook_KPLC = 'KenyaPowerLtd'
def start_fetching():
    Tweet_Update.get_latest_tweets(twitter_KPLC)
    Facebook_Post_Update.fetch(facebook_KPLC)
    if(not data_limited):
        Tweet_Reply_Update.get_latest_replies(twitter_KPLC)
    
def periodical_loop(minutes_interval): ##Don't be shorter than 15 minutes.
    global data_limited
    data_limited = False
    count = 0
    while(True):
        count += 1
        print("Now: ", datetime.datetime.now())
        try:
            start_fetching();
        except Exception as e:
            data_limited = True
            limited_event = count
            print(e)
        if(((count - limited_event)*minutes_interval) > 25*60):
            data_limited = False
        time.sleep(minutes_interval*60) 

if __name__=='__main__':
    periodical_loop(20)
    #start_fetching();
    
