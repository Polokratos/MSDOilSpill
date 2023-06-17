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

#454 x 800km => 227 x 400 cells.
LENGTH = 454//2 #2x2 km
HEIGHT = 800//2 
TIME = 3 # 2 tyg.

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

#Apply the fact thta a day has 24h.
for x in UsefulDrifterStamps:
    x.Time += 24*x.Date
    x.Time = x.Time //1

#Filter to useful by time
UsefulDrifterStamps = [x for x in UsefulDrifterStamps if x.Time < TIME]

#Converter from Lon and Lat to a cell.
def LonLatToCell(Lon,Lat):
    Lon_offset = Lon - Left_bd
    Lon_rel_offset = Lon_offset / (Right_bd - Left_bd)
    x = (LENGTH*Lon_rel_offset) // 1
    if(x == LENGTH): x-=1 #Guard against Out of Bounds
    Lat_offset = Lat - Bot_bd
    Lat_rel_offset = Lat_offset / (Top_bd - Bot_bd)
    y = (HEIGHT*Lat_rel_offset) // 1
    if(y == HEIGHT): y-=1 #Guard against Out of Bounds
    
    if(x < 0 or y < 0 or x > LENGTH or y > HEIGHT): raise ValueError("Out of bounds!")
    return [int(x),int(y)]
    
#How to get to a datapoint from this array: CEV[x][y][t] (x => length, y=> height, t => time)
CEV = [[[None for _ in range(TIME)] for _ in range(HEIGHT)] for _ in range(LENGTH)]

LockedDPList = [] #LockedDPList[index] =  [x,y,t]

#CEV init value setting.
for ds in UsefulDrifterStamps:
    [x,y] = LonLatToCell(float(ds.Lon),float(ds.Lat))
    if(CEV[x][y][ds.Time] == None):
        CEV[x][y][ds.Time] = [ds.u,ds.v]
        LockedDPList.append([x,y,ds.Time])
    else:
        CEV[x][y][ds.Time].append(ds.u)
        CEV[x][y][ds.Time].append(ds.v)

#Locked CEV averaging, for when there are multiple u and v values.
for lkd in LockedDPList:
    allVals = CEV[lkd[0]][lkd[1]][lkd[2]] #Grab 
    allU = [float(allVals[x]) for x in range(len(allVals)) if x%2 == 0] #Extract
    allV = [float(allVals[x]) for x in range(len(allVals)) if x%2 == 1]
    U = sum(allU) / len(allU) #calculate avg
    V = sum(allV) / len(allV)
    CEV[lkd[0]][lkd[1]][lkd[2]] = [U,V] #Put the avg back.

#EKSTRAPOLACJA
# Biore srednia ważoną z wszystkich znanych eksperymentalnie wartości, gdzie premiuje to co jest blisko czy to dystansowo, czy to czasowo. (waga to 1/ odleglosc)
# Srednia predkosc to 0.3 m/s (wykalkulowalem) => wiec 2 km przerobi w 2.16h przerobmy na 2h+. Ergo roznica 2 kom. w czasie ekwiwalentna to 1 kom. w przestrzeni.


for x in range(LENGTH):
    for y in range(HEIGHT):
        for t in range(TIME):
            if(CEV[x][y][t] != None): # We have this via experimental data.
                continue
            u = 0.0
            v = 0.0
            total_weight = 0.0
            for ref in LockedDPList:
                
                #Time-distance deltas
                dx = 1.0/abs(x-ref[0]) if x!= ref[0] else 0
                dy = 1.0/abs(y-ref[1]) if y!= ref[1] else 0
                dt = 2.0/abs(t-ref[2]) if t!= ref[2] else 0 #zmiany w czasie sa mniej odczuwalne
                delta = (dx**2+dy**2+dt**2)**(1/2)
                cell = CEV[ref[0]][ref[1]][ref[2]] #CEV[x][y][t] = [u,v]
                
                
                u+= float(cell[0])*delta  
                v+= float(cell[1])*delta
                total_weight += delta
            
            u/= total_weight
            v/= total_weight
            CEV[x][y][t] = [u,v]
        

for t in range(TIME):
    file = open(str(t)+".csv","w")
    for y in range(HEIGHT):
        for x in range(LENGTH-1):
            file.write(str(CEV[x][y][t])+",")
        file.write(str(CEV[x][y][t])+"\n")

