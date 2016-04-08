__author__ = 'darre_000'

'Self lr 0.001 rmse'

data = []
labels = []
#READ CSV
for index in range(8):
    if(index == 0):
        dir = ''
        file = 'Self lr 0.00001 rmse'
        labels.append(file)
    elif(index == 1):
        dir = ''
        file = 'Self lr 0.0001 rmse'
        labels.append(file)
    elif(index == 2):
        dir = ''
        file = 'Self lr 0.001 rmse'
        labels.append(file)
    if(index == 3):
        dir = '..\Data 2.0\\'
        file = 'Self 100 lr 7.5E-5 rmse'
        labels.append(file)
    elif(index == 4):
        dir = '..\Data 2.0\\'
        file = 'Self 100 lr 5.0E-5 rmse'
        labels.append(file)
    elif(index == 5):
        dir = '..\Data 2.0\\'
        file = 'Self 100 lr 1.0E-4 rmse'
        labels.append(file)
    elif(index == 6):
        dir = '..\Data 2.0\\'
        file = 'Self 100 lr 5.0E-4 rmse'
        labels.append(file)
    elif(index == 7):
        dir = '..\Data 2.0\\'
        file = 'Self 100 lr 2.5E-4 rmse'
        labels.append(file)
    lines = [line.rstrip('\n') for line in open(dir + file)]#("..\Data 2.0\\" + file)]
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

for d in data:
    plt.plot(d, label=str(labels[data.index(d)]))
# plt.legend(bbox_to_anchor=(0., 1.02, 1., .102), loc=1, ncol=2, mode="expand", borderaxespad=0.)
plt.legend(bbox_to_anchor=(0., -0.15, 1., .102), loc=1, ncol=2, mode="expand", borderaxespad=0.)
plt.xlabel("Training games")
plt.ylabel("RMSE")
plt.show()