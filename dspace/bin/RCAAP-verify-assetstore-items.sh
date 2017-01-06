#!/bin/bash

# pgraca: paulo.graca@fccn.pt
# this script verifies if bitstream files in the assetstore have metadata defined in the database

# dependencies: find, diff, psql/postgresql, sed, grep, sort, cat

LOG_FILE="/var/log/dspace/verify_assetstore.log"

# Current script dir
pushd `dirname $0` > /dev/null
SCRIPTPATH=`pwd`
popd > /dev/null

usage()
{
cat <<EOF
Usage: $(basename $0) [options]
 
This shell script exports AIP packages from dspace and verifies the 
result comparing it with archived itens on database.
 
Options:
 
  --dir_source   Based directory. The assetstore
 
Example:

   $(basename $0)  --dir_source /srv/dspace/assetstore
 
EOF
} 


while [ "$1" ]; do
  case "$1" in
        --dir_source)
            shift
            BASE_DIR="$1"
            ;;
        --help)
            usage
            exit 0
            ;;
        *)
            echo "$(basename $0): invalid option $1" >&2
            echo "see --help for usage"
            exit 1
                  ;;
  esac
  shift
done

if [ -z "$BASE_DIR" ]; then
  BASE_DIR=/srv/dspace/assetstore
fi

BITSTREAMS_ASSETSTORE_ID=$(cat /proc/sys/kernel/random/uuid)
BITSTREAMS_DB_ID=$(cat /proc/sys/kernel/random/uuid)

# find only for files and output their name sorted
find $BASE_DIR -type f -printf '%f\n'|sort 1>/tmp/${BITSTREAMS_ASSETSTORE_ID}.txt


DB_USERNAME=`grep "db.username" ${SCRIPTPATH}/../config/dspace.cfg|sed 's|db.username =||g'|sed 's| ||g'`
DB_DATABASE=`grep "db.url" ${SCRIPTPATH}/../config/dspace.cfg|sed 's|db.url =||g'|sed 's| ||g'|rev|cut -d'/' -f1|rev`

# find all bitstream and output their internal_id trimming all spaces
echo "SELECT internal_id FROM bitstream ORDER BY internal_id;" | psql -tU ${DB_USERNAME} ${DB_DATABASE}|sed 's| ||g' 1>/tmp/${BITSTREAMS_DB_ID}.txt

# compare the files - check if there is any file in the assetstore that isn't in the database
diff -b --ignore-blank-lines /tmp/${BITSTREAMS_ASSETSTORE_ID}.txt /tmp/${BITSTREAMS_DB_ID}.txt| grep -v "^---" | grep -v "^[0-9c0-9]" | grep -v "^>" |sed "s|<|Only in assetstore:|g"|sed "s|>|Only in database:|g" 1>${LOG_FILE}
