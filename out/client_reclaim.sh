#!/bin/bash
echo -n "Server Name?"
read nS
echo -n "Tamanho?"
read tam
java backup_service/Client $nS "RECLAIM" $tam
