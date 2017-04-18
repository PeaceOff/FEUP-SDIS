#!/bin/bash
echo -n "Server Name?\n> "
read nS
java backup_service/Client $nS "STATE"
