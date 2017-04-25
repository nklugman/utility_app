# -*- coding: utf-8 -*-
from pathlib import Path
import PyPDF2
import re
import psycopg2
import datetime
import time

import sys
reload(sys)
sys.setdefaultencoding('utf-8')

def getlatestpdf(pdf_files):
    curr = "00.00.0000.pdf"
    for pdf_file in pdf_files:
        temp = str(pdf_file)
        temp2 = str(curr)
        if (temp[9] > temp2[9]):
            curr = pdf_file
        elif (temp[9] == temp2[9]) and (temp[3:5] > temp2[3:5]):
            curr = pdf_file
        elif (temp[9] == temp2[9]) and (temp[3:5] == temp2[3:5]) and (temp[0:2] > temp2[0:2]):
            curr = pdf_file
    #print (curr)
    return str(curr)

def parseTime(_time):
    hour_break=re.search('(?=[0-9]+)',_time).end()
    _time=_time[hour_break:]
    hour_break=re.search('[0-9]+',_time).end()
    hour=_time[:hour_break].strip()
    _time=_time[hour_break+1:]
    minute_break=re.search('[0-9]+',_time).end()
    minute=_time[:minute_break].strip()
    _time=_time[minute_break:]
    time=hour+':'+minute+' '
    if _time.find("A")!=-1:
        time+='AM'
    else:
        time+='PM'
    return time
            
def parse(directory):
    lists=[]
    r = ""
    start_time=""
    end_time=""
    pdfFileObj = open(directory, 'rb')
    pdfReader = PyPDF2.PdfFileReader(pdfFileObj)
    num_pages = pdfReader.numPages
    for i in range(num_pages):
        pageObj = pdfReader.getPage(i)
        text = pageObj.extractText()

        text = text.replace("\n","")
        text = text.replace("Œ","-")
        text = text.replace("™","'")
        text = text.replace("˚","ffi")
        text = text.replace("˛","ff")
        text = text.replace("˜","fi")
        text = text.replace("˝","fl")

        content=text.split("Notice is hereby ")
        for j in range(len(content)):
            if j == 0:
                regions  = re.split("((?:[A-Z])(?:[A-Z|\.]+\s)+(?:REGION))", content[0])
            else:
                content=content[1].split("etc.)")
                regions  = re.split("((?:[A-Z])(?:[A-Z|\.]+\s)+(?:REGION))", content[1])

            for region in regions:
               if region.find("REGION")!=-1:
                   r = str(region)
               else:
                  area_ints = re.split("AREA:",region)
                  for area_int in area_ints:
                      areas = area_int.split("& adjacent customers.")
                      for area in areas:
                         area=area.strip()
                         if area.find("COUNTY")==-1 and len(area)>1:
                            try:
                                data = area
                                a=""
                                date=""
                                time=""

                                area_break = data.index("DATE")
                                a = data[:area_break].strip()
                                data = data[area_break+len("DATE:"):]

                                date_break = data.index("TIME")
                                date = data[:date_break].strip()
                                data = data[date_break+len("TIME:"):]

                                day_break=re.search('(?=[0-9]+)',date).end()
                                date=date[day_break:]
                                date_=date.split(".")
                                day = date_[0].strip()
                                month = date_[1].strip()
                                year = date_[2].strip()
                                year_break=re.search('[0-9]+',year).end()
                                year = year[:year_break].strip()
                                date=year+'-'+month+'-'+day

                                time_break = re.search('(A\.M|P\.M|AM|PM).*(A\.M|P\.M|AM|PM)', data).end()
                                times = data[:time_break].strip()
                                data = data[time_break:]
                                start_time=""
                                end_time=""
                                
                                time=re.split("-|Š",times)
                                start_time=parseTime(time[0])
                                end_time=parseTime(time[1])
                                
                                areas_=a.split(",")
                                for area_ in areas_:
                                     if re.search('PART OF|PARTS OF|WHOLE OF',area_):
                                            area__break=re.search('PART OF|PARTS OF|WHOLE OF',area_).end()
                                            area_=area_[area__break:].strip()
                                     list = []
                                     list.append(date)
                                     list.append(start_time)
                                     list.append(end_time)
                                     list.append(r)
                                     list.append(area_.strip())
                                     for j in range(len(list)):
                                         if list[j]=="":
                                            list[j]=None
                                     
                                     length=""
                                     start_datetime=date+" "+start_time
                                     start=datetime.datetime.strptime(start_datetime,"%Y-%m-%d %I:%M %p")
                                     start_datetime=str(start)+"+03"
                                     end_datetime=date+" "+end_time
                                     end=datetime.datetime.strptime(end_datetime,"%Y-%m-%d %I:%M %p")
                                     end_datetime=str(end)+"+03"
                                     length=end-start
                                     list.append(length)
                                     list.append(start_datetime)
                                     list.append(end_datetime)
                                     lists.append(list)

                            except Exception as e:
                                print(e)
                                print(directory)
                                print(area)
                                print("----------------------------")
    pdfFileObj.close()
    return lists

def log(string):
    print(time.strftime("%Y-%m-%d-%H-%M-%S", time.gmtime()) + ": " + str(string))
    
latest=''
folder='/home/nklugman/kplc_app/data/scheduled_outages//pdfs//'
log("start parsing scheduled outages")
while(True):
    log("checking for update")
    dir_path = Path('')
    # get all pdf files in directory
    pdf_files = dir_path.glob('*.pdf') # it returns a list
    dir = getlatestpdf(pdf_files)
    if not dir==latest:
        log("new file found, parsing %s" % dir)
        directory= folder + dir
        lists = parse(directory)
        #con = psycopg2.connect(database='outage_map', user='postgres') 
        con = psycopg2.connect(database='capstone', user='capstone', password='capstone', host='141.212.11.206', port='5432')
        con.autocommit = True
        cur = con.cursor()
        try:
            dataText = ', '.join(map(bytes.decode,(cur.mogrify('(%s, %s, %s, %s, %s, %s,%s,%s)', element) for element in lists)))
            cur.execute('INSERT INTO outages (date,start_time,end_time,region,area,length,start_datetime,end_datetime) VALUES ' + dataText)
        except Exception as e:
            print(e)
            print(lists)
            print("----------------------------")
        con.commit()
        latest=dir
        log("parsing finish")
    else:
        log("no update")
    time.sleep(60*60)