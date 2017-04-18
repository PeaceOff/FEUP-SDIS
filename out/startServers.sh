#!/bin/bash
gnome-terminal -x bash -c "rmiregistry"
echo -n "Number of Servers\n> "
read peer_number
echo -n "Starting at\n> "
read start
echo -n "Server Version\n> "
read version

while [ "$start" -lt "$peer_number" ]
do
	gnome-terminal -x bash -c "java out/backup_service/Server $version $i $i 224.1.1.1:1111 224.2.2.2:2222 224.3.3.3:3333"
	start=`expr $start + 1`
done
