#!/bin/bash
echo -n "Server Name?\n> "
read nS
echo -n "File Path?\n> "
read file
echo -n "Rep Degree?\n> "
read rD
java backup_service/Client $nS "BACKUP" $file $rD
