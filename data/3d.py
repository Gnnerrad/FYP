__author__ = 'darre_000'

'Self lr 0.001 rmse'

#READ CSV
lines = [line.rstrip('\n') for line in open('Epoch lr 0.00001 rmse')]
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
##############################################

#Plotting
import matplotlib.pyplot as plt
import numpy as n

# plt.title("Self lr 0.0001 rmse || average")
plt.plot(avg)
plt.show()