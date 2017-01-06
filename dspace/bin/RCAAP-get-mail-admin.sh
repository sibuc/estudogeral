#!/bin/sh

# pgraca: paulo.graca@fccn.pt
# this script retrieves the dspace configured mail admin
echo `grep 'mail.admin' /dspace/config/dspace.cfg|grep -v '#'|cut -d'=' -f2|sed 's| ||g'`
