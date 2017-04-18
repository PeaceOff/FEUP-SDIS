#!/bin/bash
echo -n "Server Name?\n> "
read nS
echo -n "Tamanho?\n> "
read tam
java backup_service/Client $nS "RECLAIM" $tam
