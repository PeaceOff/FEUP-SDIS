#!/bin/bash
gnome-terminal -x bash -c "rmiregistry"
echo -n "Number of Servers?"
read nS
echo -n "Starting at?"
read sS
total = $((nS + sS - 1))
for i in {$sS..$total}
do
   gnome-terminal -x bash -c "java backup_service/Server 1.0 $i $i 224.1.1.1:1111 224.2.2.2:2222 224.3.3.3:3333"
done
