#!/usr/bin/python
import time
from time import sleep
import sys
import os

white = 17
blue = 21
green = 23
red = 22

curval=0


value=float(sys.argv[1])
dur=float(sys.argv[2])

def changer(pin, amount):
    f = open('/dev/pi-blaster', 'w')
    f.write('%d=%s\n'%(pin, str(amount)))

def fade(color, curval, val):
    step=1
    if curval > val:
        step=-step

    while (curval <> val):
	    multiplier = 3000
	    secondbound = val
            firstbound=curval
	    if (curval >= 0.5):
		multiplier = 100
		if (step < 0):
			secondbound=max(val,0.499)
		else:
			secondbound=val
	    elif (curval >= 0.3):
		multiplier = 1000
		if (step < 0):
			secondbound=max(val,0.299)
		else:
			secondbound=min(val,0.5)
	    else:
		multiplier=3000
		if (step < 0):
			secondbound=max(val,0)
		else:
			secondbound=min(val,0.3)
	    
	    fadeint(color, firstbound, secondbound, step, multiplier)
	    curval=secondbound
	    print curval

		
def fadeint(color, curval, val, step, multiplier) :   
    delay = dur/(float(abs(curval - value)/abs(step))*multiplier)
    for i in range (int(multiplier * curval),  int(multiplier * val), int(step)):
        f=(i/float(multiplier))
        changer (color, f)
        sleep(delay)

    changer ( color, val)

g = open('/tmp/' + str(white), 'r+')
curval = float(g.read())
g.seek(0)
g.write('%f'%value)
g.truncate()
g.close()
if curval <> value:
	fade(white, curval, value)
