__author__ = 'darre_000'

'Self lr 0.001 rmse'

data = []
#READ CSV
for index in range(4):
    if(index == 0):
        file = 'Self hidden 40'
    elif(index == 1):
        file = 'Self hidden 60'
    elif(index == 2):
        file = 'Self hidden 80'
    elif(index == 3):
        file = 'Self hidden 100'
    lines = [line.rstrip('\n') for line in open(file)]
    sub = []
    avg = []
    av = 0
    for line in lines:
        sub.append(line.split("\t"))

    for x in sub:
        for y in x:
            if(float(y) < 1 and float(y) > 0):
                av+=float(y)
        avg.append(av/4)
        av = 0
    data.append(avg)
    ##############################################

#Plotting
import matplotlib.pyplot as plt
import numpy as n

# plt.title("Self lr 0.0001 rmse || average")
for d in data:
    plt.plot(d)
plt.show()