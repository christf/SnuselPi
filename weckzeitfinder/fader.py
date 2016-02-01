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
step=1

def changer(pin, amount):
    f = open('/dev/pi-blaster', 'w')
    f.write('%d=%s\n'%(pin, str(amount)))

def fade(color, direction, curval, val, step):
    multiplier = 100
    if (abs(curval - val)* multiplier < 15):
	multiplier = 1000
    if curval > val:
        step=-step
    delay = 0.5/(float(abs(curval - value)/abs(step))*multiplier)
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
	fade(white, 'up', curval, value, step)
