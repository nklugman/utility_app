# -*- coding: utf-8 -*-
from time import strftime
import subprocess
import os
import glob
import time

USER = "postgres"
PASS = "capstone"
HOST = "localhost"
 
BACKUP_DIR = "C:\GridWatch\Backup\\" #where the backup files should be stored

#directory of an exe file called pg_dump
dumper = """ "C:\\program files\\postgresql\\9.6\\bin\\pg_dump" -U %s -Z 9 -f %s -F c %s  """                  
 
def log(string):
    print(time.strftime("%Y-%m-%d-%H-%M-%S", time.gmtime()) + ": " + str(string))

while(True):
    x_days_ago = time.time() - ( 60 * 60 * 24 * 30 )
     
    os.putenv('PGPASSWORD', PASS)
     
    database_list = ["capstone"]
     
    # Delete old backup files first.
    for database_name in database_list :
        database_name = database_name.strip()
        if database_name == '':
            continue
     
        glob_list = glob.glob(BACKUP_DIR + database_name + '*' + '.pgdump')
        for file in glob_list:
            file_info = os.stat(file)
            if file_info.st_ctime < x_days_ago:
                log("Unlink: %s" % file)
                os.unlink(file)
            else:
                log("Keeping : %s" % file)
     
    log("Backup files older than %s deleted." % time.strftime('%c', time.gmtime(x_days_ago)))
     
    # Now perform the backup.
    for database_name in database_list :
        log("dump started for %s" % database_name)
        thetime = str(strftime("%Y-%m-%d-%H-%M")) 
        file_name = database_name + '_' + thetime + ".sql.pgdump"
        #Run the pg_dump command to the right directory
        command = dumper % (USER,  BACKUP_DIR + file_name, database_name)
        log(command)
        subprocess.call(command,shell = True)
        log("%s dump finished" % database_name)
     
    log("Backup job complete.")
    time.sleep(60 * 60 * 24)