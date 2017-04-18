#!/bin/bash
echo -n "Server Name?\n> "
read nS
java out/backup_service/Client $nS "STATE"
