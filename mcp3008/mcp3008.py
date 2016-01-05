#!/usr/bin/python
# this will read the input of mcp3008. it will accept the analog input id as argument.
# the program will output the change in value wrt the sliding average over the last 1000 samples

import spidev
import time
import sys
import numpy as np

class RingBuffer():
    "A 1D ring buffer using numpy arrays"
    def __init__(self, length, offset):
        self.data = np.zeros(length, dtype='f')
	self.data.fill(offset)
        self.index = 0

    def extend(self, x):
        "adds array x to ring buffer"
        x_index = (self.index + np.arange(1)) % self.data.size
        self.data[x_index] = x
        self.index = x_index[-1] + 1

    def get(self):
        "Returns the first-in-first-out data in the ring buffer"
        idx = (self.index + np.arange(self.data.size)) %self.data.size
        return self.data[idx]
    def avg(self):
	return np.average(self.data);


spi = spidev.SpiDev()
spi.open(0,0)

input = int(sys.argv[1])
schwellwert = int(sys.argv[2])
sleep = 0.01
ringlen = 1000

def readadc(adcnum):
        if ((adcnum > 7) or (adcnum < 0)):
                return -1
        r = spi.xfer2([1,(8+adcnum)<<4,0])
        adcout = ((r[1]&3) << 8) + r[2]

        return adcout

rb = RingBuffer(ringlen, readadc(input) )
#rb = RingBuffer(
while True:
	offset = rb.avg()
	val = readadc(input)
	rb.extend(val)
	sys.stdout.write('\r' + str(abs(val - offset)))# + " " + str(offset) +   "       " )
	sys.stdout.flush()
	time.sleep(sleep)

