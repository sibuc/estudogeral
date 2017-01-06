#!/bin/bash

# pgraca: paulo.graca@fccn.pt
# this script verifies if handle-server is running - to be used in SNMP

if [[ $(/sbin/service handle-servers status|/bin/grep "not running"|/usr/bin/wc -c) -gt "0" ]] ; then /bin/echo 0; else /bin/echo 1; fi
