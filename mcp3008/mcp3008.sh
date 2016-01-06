#!/bin/bash
HOSTNAME="${COLLECTD_HOSTNAME:-snuselpi}"
INTERVAL="${COLLECTD_INTERVAL:-60}"

trapfunc() {
	kill $pid
}

trap 'trapfunc' 1 2 3 6 9 13 14 15

/usr/local/bin/mcp3008.py 0 1 "${INTERVAL%.*}" $HOSTNAME 

export pid=$!

wait
