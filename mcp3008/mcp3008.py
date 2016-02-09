#!/usr/bin/python -u
# this will read the input of mcp3008. it will accept the analog input id as argument.
# the program will output the change in value wrt the sliding average over the last 1000 samples
import datetime
import os
import spidev
import time
import sys

spi = spidev.SpiDev()
spi.open(0,0)
input = 0
schwellwert = 3
interval  = float(os.environ['COLLECTD_INTERVAL'])
hostname = os.environ['COLLECTD_HOSTNAME']
sleep = 0.002
ringlen = 128
def readadc(adcnum):
        if ((adcnum > 7) or (adcnum < 0)):
                return -1
        r = spi.xfer([1,(8+adcnum)<<4,0])
        adcout = ((r[1]&3) << 8) + r[2]

        return adcout

def output(f, value):
	# t=str(0)
	t = str(datetime.datetime.now())
	f.write(t + " " + value)
	sys.stdout.write(value)

val=0
count=0
acount=0
dcount=0
debug=0
if (len(sys.argv) >= 2):
	debug=1
f = open ('/tmp/mcp3008.log', 'w')
f.write("spi-device " + str(input) + "\n")
f.write("schwellwert " + str(schwellwert) + "\n")
f.write("interval " + str(interval)+ "\n")
f.write("hostname: "+ str(hostname)+ "\n")
f.flush()
waititerations = int((1/sleep)*interval)
# this is fucking crazy. si is a corrected sleep-value because python will not do a sleep_until to offset for processing time within the main loop. the  smaller sleep, the higher the offeset
# also polling spi with such a high resolution is questionable at best.

si=(1-0.27)*sleep
# use 1-1.1 for sleep = 0.01 and 1-0.52 for sleep = 0.001 since adxl335 is equiped such that the frequency is 500hz for z, 
#MA*[i]= MA*[i-1] +X[i] - MA*[i-1]/N
avg = 0
dsum=0
strint = str(interval)
scount=0
itercount=0
strinp = str(input)
while True:
	lval = val
	val = readadc(input)
	avg = int(avg + val - (int( avg) >>7))
	sa=avg>>7
	diff = val - sa
	if (diff >= schwellwert) :
		scount+=1
	if (debug == 1 ):
		sys.stdout.write("                                wert: " + str(val) + " average:" +  str(sa) + " wert-average:" + str(diff) + " wert-lwert" + str(lval - val) + " schwellwertcount"  + str(count) + "        \n");
	if (itercount % waititerations == 0):
		sys.stdout.write(str(datetime.datetime.now()) + " \n");
		output(f, "PUTVAL " + hostname + "/exec-accel/gauge-Accel" + strinp + "_scount interval=" +  strint + " N:" + str(scount) + "\n"  )
		scount=0
		itercount=0
		f.flush()
	
	itercount+=1
	time.sleep(si)
f.close
