#!/bin/bash
echo -n "Server Name?\n> "
read nS
echo -n "File Path?\n> "
read file
java out/backup_service/Client $nS "DELETE" $file
