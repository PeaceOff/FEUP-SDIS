#!/bin/bash
echo -n "Server Name?\n> "
read nS
echo -n "File Path?\n> "
read file
java backup_service/Client $nS "RESTORE" $file
