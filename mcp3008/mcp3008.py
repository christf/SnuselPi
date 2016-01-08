#!/usr/bin/python -u
# this will read the input of mcp3008. it will accept the analog input id as argument.
# the program will output the change in value wrt the sliding average over the last 1000 samples
import datetime
import os
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
        self.data[self.index] = x
        self.index = (self.index + 1) % self.data.size
        #self.index = x_index 

    def get(self):
        "Returns the first-in-first-out data in the ring buffer"
        idx = (self.index + np.arange(self.data.size)) %self.data.size
        return self.data[idx]
    def avg(self):
	return np.average(self.data);


spi = spidev.SpiDev()
spi.open(0,0)
input = 0
schwellwert = 1
interval  = float(os.environ['COLLECTD_INTERVAL'])
#interval = 10
hostname = os.environ['COLLECTD_HOSTNAME']
sleep = 0.001
ringlen = 100
def readadc(adcnum):
        if ((adcnum > 7) or (adcnum < 0)):
                return -1
        r = spi.xfer([1,(8+adcnum)<<4,0])
        adcout = ((r[1]&3) << 8) + r[2]

        return adcout

def output(f, value):
	t = str(datetime.datetime.now())
	f.write(t + " " + value)
	sys.stdout.write(value)

#rb = RingBuffer(ringlen, readadc(input) )
rb = RingBuffer(ringlen, 0 )

#rb = RingBuffer(
minutavg = RingBuffer(1/sleep/ringlen*interval, 0)
val=0
count=0
acount=0
f = open ('/tmp/mcp3008.log', 'w')
f.write("input " + str(input) + "\n")
f.write("schwellwert " + str(schwellwert) + "\n")
f.write("interval " + str(interval)+ "\n")
f.write("hostname: "+ str(hostname)+ "\n")
f.flush()
waititerations = (1/sleep)*interval
# this is fucking crazy. si is a correction value fbecause python will not do a sleep_until to offset for processing time within the main loop. the  smaller sleep, the higher the offeset
si=(1-0.51)*sleep
# use 1-1.1 for sleep = 0.01 and 1-0.51 for sleep = 0.001
while True:
	lval = val
	val = readadc(input)
	diff = abs(lval - val)
	rb.extend(diff)
	count+=1
	if (count % ringlen == 0) :
		avg = rb.avg()
		minutavg.extend(avg)
		if (avg > schwellwert):
			acount+=1
		if (count == waititerations):
			output(f, "PUTVAL " + hostname + "/exec-accel/gauge-Accel" + str(input) + "_avg interval=" +  str(interval) + " N:" + str(int(minutavg.avg()*1000)) + "\n"  )
			output(f, "PUTVAL " + hostname + "/exec-accel/gauge-Accelc" + str(input) + "_count interval=" +  str(interval) + " N:" + str(int(acount)) + "\n")
			sys.stdout.flush()
			f.flush()
			acount=0
			count=0
	time.sleep(si)
f.close
