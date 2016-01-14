#!/bin/bash
# set -ix
cpath=/home/christof/.calendars/
soundfile=/var/lib/mpd/music/local/wecker
WECKZEITFINDER=./weckzeitfinder.sh
interval=10
while read
do
  temp="${REPLY#*[[:blank:]]}"
  endtime="${temp%%[[:blank:]]*}"
done < <($WECKZEITFINDER -f "$cpath" -l ERROR|grep " Wecker$"|head -n1 )
uendtime=$(date --date=${endtime} +%s)
sleeptime=$((uendtime - $(date +%s)))

if (( $sleeptime < $((24*3600)) ))
then
  while (($sleeptime > $interval))
  do
    sleep $interval
    echo sleeping $((sleeptime-=interval)) until $endtime
  done

  # TODO Test this on raspi:
  soundlevel=$(/usr/bin/amixer -c 1  get  Master|tail -n 1 |cut -d"[" -f2|cut -d"]" -f1)
  /usr/bin/amixer -c 1  set  Master "100%"
  /usr/bin/amixer -c 1  set  Master on
  mplayer "$soundfile"
  /usr/bin/amixer -c 1  set  Master ${soundlevel:-80%}
fi
