#!/bin/bash
echo -n "Server Name?"
read nS
echo -n "File Path?"
read file
echo -n "Rep Degree?"
read rD
java backup_service/Client $nS "BACKUP" $file $rD
