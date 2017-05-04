# FEUP-SDIS
Repository for the project of Distributed Systems

To compile in WINDOWS:

    - navigate to the project's root folder

    - create a folder named "out" in the same directory as "src"

    - open a terminal in that directory (root folder) and type the following command:

        "javac -d out -sourcepath src -encoding ISO-8859-1 src/backup_service/Server.java src/backup_service/Client.java"

To compile in LINUX:

    - create a folder named "out" in the same directory as "src"

    - using a terminal, navigate to the project's root folder and type "sh compile.sh"

To run the peer:

    - navigate to the project's "out" folder

    - open a terminal in that directory and type the following commands:

        "rmiregistry" only needed one time per local host

        "java backup_service.Server <Version> <Server_id> <Server_name> <mc_addr>:<mc_port> <mdb_addr>:<mdb_port> <mdr_addr>:<mdr_port>"

Note : We are using RMI for the communication between the Client and the Server so <Server_name> is the name the Server has on the local rmiregistry.

To run the client:

    - navigate to the project's "out" folder

    - open a terminal in that directory and type the following command:

        "java backup_service.Client <peer_ap> <sub-protocol> <opnd_1> <opnd_2>"
