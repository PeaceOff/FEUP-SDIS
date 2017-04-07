#!/bin/bash
echo -n "Server Name?"
read nS
java backup_service/Client $nS "STATE"
