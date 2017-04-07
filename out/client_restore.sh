#!/bin/bash
echo -n "Server Name?"
read nS
echo -n "File Path?"
read file
java backup_service/Client $nS "RESTORE" $file
