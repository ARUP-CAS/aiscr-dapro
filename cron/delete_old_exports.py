#!/usr/bin/python3

from datetime import datetime  
from datetime import timedelta  
import os
import sys
import time
import glob

EXPDATA='/var/cache/exports/data'
LOGFILE='/var/cache/exports/tmp/delete_export.log'
TIMEFILE='/var/cache/exports/var/export.date'

export_date = datetime.strptime(time.ctime(os.path.getctime(TIMEFILE)), "%a %b %d %H:%M:%S %Y")

date = export_date

surfix = ['projekt', 'akce', 'lokalita', 'dokument', 'dok_jednotka', 'soubor', 'let', 'pian', 'ext_zdroj', 'samostatny_nalez', 'adb']

xml_prefix = 'export_'
rdf_prefix = 'rdf_'

constDays = 2
countRDF = len(surfix)
countXML = len(surfix)*5  #A,B,C,D, amcr

oldestFile = min(os.listdir(EXPDATA), key=lambda f: os.path.getctime("{}/{}".format(EXPDATA, f)))
oldestTime = datetime.strptime(time.ctime(os.path.getctime(EXPDATA + '/' + oldestFile)), "%a %b %d %H:%M:%S %Y")

maxDate = oldestTime = oldestTime - timedelta(days=1) 

setFound = 0

while date > maxDate:
    #print(date)
    findXML = False
    findRDF = False

    pathXML = EXPDATA+'/' + xml_prefix + date.strftime("%Y-%m-%d") + '_*.xml'
    pathRDF = EXPDATA+'/' + rdf_prefix + date.strftime("%Y-%m-%d") + '_*.xml'

    #Prochazim XML 
    myDailyList = glob.glob(pathXML)
    listFound = 0
    for s in surfix:
        for file in myDailyList:
                if s in file:
                        listFound += 1

    if listFound == countXML:
         #print("Set found xml")
         findXML = True
    else:
        date = date - timedelta(days=1)
        continue #nema cenu prochazet RDF

    #Prochazim RDF
    myDailyList = glob.glob(pathRDF)
    listFound = 0

    for s in surfix:
        for file in myDailyList:
            if s in file:
                listFound += 1
                break

    if listFound == countRDF:
         #print("Set found RDF")
         findRDF = True

    if findXML == True and findRDF == True:
        setFound +=1
        if (setFound == 1):
            #print("Set RDF and XML completed")
            days_min = (date + timedelta(days=1)).strftime("%Y-%m-%d")
            #print(days_min)

    if setFound == constDays:
        break

    date = date - timedelta(days=1)

if(setFound == constDays):
    days_max=(datetime.now()-date).days
    delete_string = 'find ' + EXPDATA + '/* -type f -mtime +' + str(days_max) + ' | xargs rm -f >>' + LOGFILE + ' 2>&1'
    print(delete_string)
    os.system(delete_string)

#delete not complete set of exports
try:
    delete_string = 'find ' + EXPDATA +' -type f -newermt \"' + days_min + '\" -delete >>' + LOGFILE + ' 2>&1'
    print(delete_string)
    os.system(delete_string)
except:
    print("Nothing to delete")
