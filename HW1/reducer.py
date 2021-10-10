#!/usr/bin/env python
import sys
import csv


amean = 0.
avar = 0.
anum = 0
for line in sys.stdin:
    mean, var, num = map(float, line.split('\t'))
    amean, avar = (anum * amean + num * mean) / (anum + num), (anum * avar + num * var) / (anum + num) + anum * num * ((amean - mean) / (anum + num))**2
    anum += num
print(amean, avar, sep='\t')
