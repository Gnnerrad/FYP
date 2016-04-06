__author__ = 'darre_000'

'Self lr 0.001 rmse'

data = []
labels = []
#READ CSV
for index in range(2):
    if(index == 0):
        file = 'Self hidden 100 5.0E-5'
        labels.append(file)
    elif(index == 1):
        file = 'Self hidden 100 7.5E-5'
        labels.append(file)
    # elif(index == 2):
    #     file = 'Self hidden 100 0.6'
    #     labels.append(file)
    # elif(index == 3):
    #     file = 'Self hidden 100 0.7'
    #     labels.append(file)
    # elif(index == 4):
    #     file = 'Self hidden 100 0.8'
    #     labels.append(file)
    # elif(index == 5):
    #     file = 'Self hidden 100 0.9'
    #     labels.append(file)
    # elif(index == 6):
    #     file = 'Self hidden 100 1.0'
    #     labels.append(file)
    lines = [line.rstrip('\n') for line in open("..\Data 2.0\\" + file)]
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

plt.title("Lambda vs RMSE")
for d in data:
    plt.plot(d, label=str(labels[data.index(d)]))
# plt.legend(bbox_to_anchor=(0., 1.02, 1., .102), loc=1, ncol=2, mode="expand", borderaxespad=0.)
plt.legend(bbox_to_anchor=(0., -0.15, 1., .102), loc=1, ncol=2, mode="expand", borderaxespad=0.)
plt.xlabel("Training games")
plt.ylabel("RMSE")
plt.show()