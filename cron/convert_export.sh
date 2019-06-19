#!/bin/bash

# Note that the download itself cannot be included here because it
# requires credentials.

EXPDATA=/var/cache/exports/data
EXPLIB=/var/cache/exports/lib
EXPTMP=/var/cache/exports/tmp
EXPMAPPING=$EXPLIB
DATEFILE=/var/cache/exports/var/export.date
LOGFILE=$EXPTMP/convert_export_$( date +%m_%d ).log

java -Xmx4000m -Xms4000m -jar $EXPLIB/rdfcon-1.0-SNAPSHOT.jar -i $EXPDATA/ -o $EXPDATA/ -x $EXPMAPPING -p $EXPMAPPING/AMCR_Generator_Policy.xml > $LOGFILE 2>&1 && \
    java -Xmx2560m -Xms2560m -jar $EXPLIB/scrub-1.0-SNAPSHOT.jar -i $EXPDATA -o $EXPDATA >> $LOGFILE 2>&1 && \
    touch $DATEFILE

find $EXPTMP -xtype f -mmin +2160 -exec rm -rfv {} \; >> $LOGFILE
