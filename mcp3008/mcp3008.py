#!/usr/bin/python -u
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
interval = int(sys.argv[3])
sleep = 0.001
ringlen = 100
hostname = sys.argv[4]
def readadc(adcnum):
        if ((adcnum > 7) or (adcnum < 0)):
                return -1
        r = spi.xfer2([1,(8+adcnum)<<4,0])
        adcout = ((r[1]&3) << 8) + r[2]

        return adcout

#rb = RingBuffer(ringlen, readadc(input) )
rb = RingBuffer(ringlen, 0 )
#rb = RingBuffer(
minutavg = RingBuffer(1/sleep/ringlen*interval, 0)
val=0
count=0
acount=0
mcount=0
f = open ('/tmp/mcp3008.log', 'w')
f.write(str(input))
f.write(str(schwellwert))
f.write(str(interval))
f.write(str(hostname))
f.flush()
while True:
	lval = val
	val = readadc(input)
	diff = abs(lval - val)
	rb.extend(diff)
	count+=1
	if (count%ringlen == 0):
		avg = rb.avg()
	#	sys.stderr.write('\r' + str(avg) + "       " ) # + str(offset) +   "       " )
	#	sys.stderr.flush()
		minutavg.extend(avg)
		mcount+=1
		if (avg > schwellwert):
			acount+=1
		count=0
		if (mcount%(1/sleep/ringlen*interval) == 0):
			print "PUTVAL \"" + hostname + "/exec-accel/gauge-Accel" + str(input) + "_avg\" interval=" +  str(interval) + " N:" + str(int(minutavg.avg()*1000))  
			f.write("PUTVAL \"" + hostname + "/exec-accel/gauge-Accel" + str(input) + "_avg\" interval=" +  str(interval) + " N:" + str(int(minutavg.avg()*1000))+ "\n")
			f.flush()
			print "PUTVAL \"" + hostname + "/exec-accel/gauge-Accelc" + str(input) + "_count\" interval=" +  str(interval) + " N:" + str(int(acount))
			print "PUTVAL \"localhost/exec-Freifunk_Frankfurt/gauge-Connections_fastd1\" interval=60 N:102"
			sys.stdout.flush()
			mcount=0
			acount=0
	time.sleep(sleep)

f.close
