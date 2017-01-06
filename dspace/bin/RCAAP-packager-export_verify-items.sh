#!/bin/bash

# pgraca: paulo.graca@fccn.pt
# this script verifies if the exporting process of files of dSpace packager works properly

RESULT_FILE_NAME="/var/tmp/dspace/dspace_aip_dump_status"
LOG_FILE="/var/log/dspace/export_aip.log"

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
 
  --email        Email of eperson to use on export.
 
  --prefix       Handle prefix.
 
  --dir_target   Target directory, the place to save exported result.

Example:

   $(basename $0)  --email test@example.com
 
EOF
} 

while [ "$1" ]; do
  case "$1" in
        --email)
            shift
            EMAIL="$1"
            ;;
        --prefix)
            shift
            HANDLE_PREFIX="$1"
            ;;
        --dir_target)
            shift
            BACKUP_DIR="$1"
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

if [ -z "$EMAIL" ]; then
  echo "$(basename $0): email mandatory"
  exit 1
fi

if [ -z "$HANDLE_PREFIX" ]; then
  HANDLE_PREFIX=`grep "handle.prefix" ${SCRIPTPATH}/../config/dspace.cfg|sed 's|handle.prefix =||g'|sed 's| ||g'`
fi

if [ -z "$BACKUP_DIR" ]; then
  BACKUP_DIR="${SCRIPTPATH}/../exports"
fi


#export AIP packages
./dspace packager -d -a -u -t AIP -e ${EMAIL} -i ${HANDLE_PREFIX}/0 ${BACKUP_DIR}/sitewide-aip.zip>${LOG_FILE}


#find on the backup directory and count the number of files
NUMBER_BACKUPS=(`find $BACKUP_DIR -mtime 0 ! -size 0 -type f -name "ITEM*.zip" | wc -l`)
#retrive the number of archived items
NUMBER_ITEMS=(`echo "SELECT count(*) FROM item WHERE in_archive=TRUE AND discoverable=TRUE;" | psql -tU postgres dspace|sed 's| ||g'`)

if [ "$NUMBER_BACKUPS" = "$NUMBER_ITEMS" ]; then
    echo "1" > ${RESULT_FILE_NAME}
else
    echo "0" > ${RESULT_FILE_NAME}
fi
