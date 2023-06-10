import os
import csv
import datetime

#Data sources
CG = "../OillSPill/Cost_Guard"
OCG =  "../OillSPill/OceanCirculationGroup"

#Area boundaries
Top_bd = 30.529433 #Lat
Bot_bd = 26.443105
Left_bd = -92.048461 #Lon
Right_bd = -84.862920
#454 x 800km

#28.736628 -88.365997 Rig Location

#Boudary checking.
def between(left,toCheck,right):
    return (left < toCheck and toCheck < right) or (left > toCheck and toCheck > right)
    

#Class for unifying structure for both CG and OCG.
class DrifterStamp:
    def __init__(self,Date,Time,Lat,Lon,speed,dir,u,v,sst=None) -> None:
        self.Date = Date
        self.Time = Time
        self.Lat = Lat
        self.Lon = Lon
        self.speed = speed
        self.dir = dir
        self.u = u
        self.v = v
        self.sst = sst
AllDrifterStamps = []


#Load data
for relative_path in os.listdir(CG):
    with open(CG + "/" + relative_path) as csvfile:
        reader = csv.reader(csvfile,dialect="excel")
        for val in reader:
            if(val[0] == "Date"): continue # Ignore headers
            #Date,Time,Lat,Lon,Speed,Dir,U(m/s),V(m/s)
            AllDrifterStamps.append(DrifterStamp(val[0],val[1],val[2],val[3],val[4],val[5],val[6],val[7],None))
for relative_path in os.listdir(OCG):
    with open(OCG + "/" + relative_path) as csvfile:
        reader = csv.reader(csvfile,dialect="excel")
        for val in reader:
            if(val[0] == "Date"): continue # Ignore headers
            #Date,Time,Lat,Lon,sst,Speed,Dir,U(m/s),V(m/s)
            AllDrifterStamps.append(DrifterStamp(val[0],val[1],val[2],val[3],val[5],val[6],val[7],val[8],val[4]))

UsefulDrifterStamps = [x for x in AllDrifterStamps if between(Top_bd,float(x.Lat),Bot_bd) and between(Left_bd,float(x.Lon),Right_bd)]


#Replace datetimes with a format that is easier to handle.
# Date -> days passed since Day 0 == 2010-05-07 (generated via first datapoint)
Day0 = datetime.date(2010,5,7)

for x in UsefulDrifterStamps:
    date = x.Date.split("-")
    date = datetime.date(int(date[0]),int(date[1]),int(date[2]))
    delta = date - Day0
    x.Date = delta.days

#Round hh:mm:ss to full hours only It will be scuffed AF already, it really doesn't matter.
for x in UsefulDrifterStamps:
    time = x.Time.split(":")
    x.Time = int(time[0])
    if(int(time[1]) >= 30):
        x.Time+=1

LENGTH = 454//2 #2x2 km
HEIGHT = 800//2 
TIME = 24*145 #24h * 145 days, i got this from sorting useful stamps via date.

#How to get to a datapoint from this array: CEV[x][y][t] (x => length, y=> height, t => time)
CEV = [[[None for _ in range(TIME)] for _ in range(HEIGHT)] for _ in range(LENGTH)]