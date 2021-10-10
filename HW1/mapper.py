#!/usr/bin/env python
import sys
import csv

cumsum = 0.
num = 0
vals = []
for line in sys.stdin:
    price = csv.reader([line])
    for i in price:
        try:
            if i[-7].isnumeric():
                cumsum += float(i[-7])
                num += 1
                vals.append(float(i[-7]))
        except:
            ...
mean = cumsum / num
mean_sq = sum([i ** 2 for i in vals]) / num
var = sum(vals)/ num
print(mean, mean_sq - mean ** 2, num, sep="\t")
