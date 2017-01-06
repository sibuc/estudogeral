#!/bin/sh


#
# This script copys the news files modified in the backoffice 
# news-side and news-top (including PT)
# 
#

#COPY THE NEWS
COPY_DIR=/usr/local/dspace/config/
DEST_DIR=/usr/local/src/dspace/dspace32++/dspace/config/

FILES_TO_COPY="news*.html"

rsync -v --update $COPY_DIR$FILES_TO_COPY $DEST_DIR
 
LICENSE="default.license"      

rsync -v --update $COPY_DIR$LICENSE $DEST_DIR
