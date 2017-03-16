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

def parse_reply(reply,dict,cur,time):
    reply = str(reply).encode('utf-8')
    wordList = re.sub("[^\w]", " ",  reply).split()
    for word in wordList:
        if word in dict[0]:
            cur.execute("INSERT INTO social_media (area,time) VALUES ('%s','%s')" % (word,time))
    for i in range(1,10):
        puzzleList=puzzle(wordList,i)
        for word in puzzleList:
            if word in dict[i]:
                cur.execute("INSERT INTO social_media (area,time) VALUES ('%s','%s')" % (word,time))
                
def parsing_to_db(dataframe, category, isTwitter):
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
    for i in range(len(dataframe)):
        if(isTwitter):
            dataframe['reply_timestamp']=dataframe['reply_date']+" "+dataframe['reply_time']+"+03"
        time = dataframe.iloc[i]['reply_timestamp']
        reply = dataframe.iloc[i][category].lower()
        parse_reply(reply,dict,cur,time)
        
    print("parsing finish")