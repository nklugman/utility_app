#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Feb 17 21:00:50 2017

@author: CHIAMAO_SHIH
"""
import re
import psycopg2

def puzzle(wordList,i): #this phrase has i+1 words
    length=len(wordList)
    puzzleList=[]
    for j in range(length//(i+1)):
        phrase=""
        for k in range(i+1):
            phrase=phrase+wordList[i*j+k]+" " #the kth word in the jth phrase
        puzzleList.append(phrase.strip())
    return puzzleList

def parse_tweet(tweet,dict,cur):
    tweet = str(tweet)
    wordList = re.sub("[^\w]", " ",  tweet).split()
    for word in wordList:
        if word in dict[0]:
            cur.execute("INSERT INTO social_media (area) VALUES ('%s')" % word)
    for i in range(1,10):
        puzzleList=puzzle(wordList,i)
        for word in puzzleList:
            if word in dict[i]:
                cur.execute("INSERT INTO social_media (area) VALUES ('%s')" % word)
                
                
def parsing_to_db(tweets, name):
    #tweets=pd.read_csv(r'C:\GridWatch\utility_app\data\Tweet_Collect_and_Update\kenyapower_care_all_replies.csv',skipinitialspace=True)
    #con = psycopg2.connect(database='outage_map', user='postgres') 
    con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
    con.autocommit = True
    cur = con.cursor()
    cur.execute("SELECT area FROM area")
    rows = cur.fetchall()
    dict=[]
    for i in range(11):
        dict.append({})
    for row in rows:
        area=str(row[0]).lower()
        areaList=area.split()
        length=len(areaList)
        dict[length-1][area]=area
    for i in range(len(tweets)):
        tweet = tweets.iloc[i][name].lower()
        parse_tweet(tweet,dict,cur)
    print("parsing finish")