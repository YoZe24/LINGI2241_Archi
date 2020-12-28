import csv as csv
import numpy as np
import matplotlib.pyplot as plt
import math as math
import statistics as stats

xPlot = [0.2,0.5,1,2,10]

# EASY
averagesEasy = []
deviationsEasy = []

# tab 1
datas1 = []
f = open("response_5_easy.txt", "r")
for x in f:
  datas1.append(x.rstrip());

for i in range(0, len(datas1)-1): 
    datas1[i] = int(datas1[i]) 

datas1.pop()
averagesEasy.append(stats.mean(datas1))
deviationsEasy.append(stats.stdev(datas1))

# tab 2
datas2 = []
f = open("response_2_easy.txt", "r")
for x in f:
  datas2.append(x.rstrip());

for i in range(0, len(datas2)-1): 
    datas2[i] = int(datas2[i]) 

datas2.pop()
averagesEasy.append(stats.mean(datas2))
deviationsEasy.append(stats.stdev(datas2))

# tab 3
datas3 = []
f = open("response_1_easy.txt", "r")
for x in f:
  datas3.append(x.rstrip());

for i in range(0, len(datas3)-1): 
    datas3[i] = int(datas3[i]) 

datas3.pop()
averagesEasy.append(stats.mean(datas3))
deviationsEasy.append(stats.stdev(datas3))

# tab 4
datas4 = []
f = open("response_05_easy.txt", "r")
for x in f:
  datas4.append(x.rstrip());

for i in range(0, len(datas4)-1): 
    datas4[i] = int(datas4[i]) 

datas4.pop()
averagesEasy.append(stats.mean(datas4))
deviationsEasy.append(stats.stdev(datas4))

# tab 5
datas5 = []
f = open("response_01_easy.txt", "r")
for x in f:
  datas5.append(x.rstrip());

for i in range(0, len(datas5)-1): 
    datas5[i] = int(datas5[i]) 

datas5.pop()
averagesEasy.append(stats.mean(datas5))
deviationsEasy.append(stats.stdev(datas5))

# Build plot
fig,ax = plt.subplots()

limY = 4000
plt.ylim(0,limY)

plt.grid(b = True, which='major', color='#b5e5eb', linestyle='-')
ax.set(xlabel = "Request rate", ylabel = "Response time", title = "Response time depending the request rate")

mSize = 1
capSize = 3
ax.errorbar(xPlot, averagesEasy, yerr = deviationsEasy, ecolor = "blue", marker = '', markersize = mSize, capsize = capSize, label = "Easy")
#ax.errorbar(xPlot, averages2, yerr = deviations2, ecolor = "orange", marker = '', markersize = mSize, capsize = capSize, label = "Spinlock")

ax.legend(loc = "upper left")
plt.show()
