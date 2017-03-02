#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar  1 20:10:48 2017

@author: CHIAMAO_SHIH
"""

import Tweet_Update
import Facebook_Post_Update
import datetime
import time

twitter_KPLC = 'kenyapower_care'
facebook_KPLC = 'KenyaPowerLtd'


def start_fetching():
    Tweet_Update.get_latest_tweets(twitter_KPLC)
    Facebook_Post_Update.fetch(facebook_KPLC)
    time_now = datetime.datetime.now()
    print("Now: ", time_now)

if __name__=='__main__':
    #start_fetching();
    while(True):
        start_fetching();
        time.sleep(8*60) #8 minutes, don't be shorter than this.
