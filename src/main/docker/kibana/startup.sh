#!/bin/bash -x

KIBANA_CONF_FILE="/usr/share/kibana/config/kibana.yml"
SAVED_OBJECTS_PATH="/saved-objects/"
RESTORE_CMD="/usr/local/bin/restore.py -H http://127.0.0.1:5601/ -C $SAVED_OBJECTS_PATH -f"
BACKUP_BIN="/usr/local/bin/backup.py"
KIBANA_START_CMD="/usr/local/bin/kibana-docker"
LOG_FILE="/tmp/load.kibana.log"
KIBANA_LOAD_CMD="/usr/local/bin/kibana-docker -H 127.0.0.1 -l $LOG_FILE"
TIMEOUT=60
WAIT_TIME=2

if [ -n "$(ls -A ${SAVED_OBJECTS_PATH})" ];
then
    echo "---- Saved objects found, restoring files."

    $KIBANA_LOAD_CMD &
    KIB_PID=$!

    # Wait for log file to be avaiable
    LOG_TIMEOUT=60
    while [ ! -f $LOG_FILE ] && [ "$LOG_TIMEOUT" -gt "0" ];
    do
        echo "Waiting for $LOG_FILE to be available..."
        sleep $WAIT_TIME
        let LOG_TIMEOUT=$LOG_TIMEOUT-$WAIT_TIME
    done

    tail -f $LOG_FILE &
    LOG_PID=$!

    # Wait for kibana to be listening
    while [ -z "$(grep "Server running at" $LOG_FILE)" ] && [ "$TIMEOUT" -gt "0" ];
    do
        echo "Waiting for kibana to start..."
        sleep $WAIT_TIME
        let TIMEOUT=$TIMEOUT-$WAIT_TIME
    done
    sleep 1

    # restore files
    $RESTORE_CMD
    sleep 1

    # cleanup
    kill $KIB_PID
    kill $LOG_PID
else
    echo "---- No saved object found"
    ls -A ${SAVED_OBJECTS_PATH}
fi

echo "---- Starting kibana"

$KIBANA_START_CMD

