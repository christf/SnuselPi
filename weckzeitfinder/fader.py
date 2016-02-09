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
fdur=float(sys.argv[2])

def changer(pin, amount):
    f = open('/dev/pi-blaster', 'w')
    f.write('%d=%s\n'%(pin, str(amount)))

def fade(color, curval, val):
    step=1
    if curval > val:
        step=-step
    ubound=0.5
    mbound=0.3
#    ldur=fdur/val
#    if (val-mbound > 0):
#      mdur=fdur/(val-mbound)
#      if (val-ubound >0):
#        mdur=fdur/(ubound-mbound)
#	udur=fdur/(curval-ubound)
    ldur=fdur*mbound
    mdur=fdur*(ubound-mbound)
    udur=fdur*(1-ubound)
    while (curval <> val):
	    multiplier = 3000
	    secondbound = val
            firstbound=curval
	    if (curval >= ubound):
		multiplier = 256
		if (step < 0):
			secondbound=max(val,0.499)
		else:
			secondbound=val
		fadeint(color, firstbound, secondbound, step, multiplier, udur)
	    elif (curval >=mbound):
		multiplier = 512
		if (step < 0):
			secondbound=max(val,mbound-0.0001)
		else:
			secondbound=min(val,ubound)
		fadeint(color, firstbound, secondbound, step, multiplier, mdur)
	    else:
		multiplier=1024
		if (step < 0):
			secondbound=max(val,0)
		else:
			secondbound=min(val,mbound)
		fadeint(color, firstbound, secondbound, step, multiplier, ldur)
	    curval=secondbound
	    # print curval

		
def fadeint(color, curval, val, step, multiplier, dur) :   
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
