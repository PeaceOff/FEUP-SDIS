#!/bin/bash
gnome-terminal -x bash -c "rmiregistry"
set total=$(($1+$2-1))
echo $total
for (( i=$sS; i<=$total; i++ ))
do
	echo $i 
   gnome-terminal -x bash -c "java backup_service/Server 1.0 $i $i 224.1.1.1:1111 224.2.2.2:2222 224.3.3.3:3333"
done
