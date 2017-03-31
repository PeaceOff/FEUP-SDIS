start rmiregistry
set /p nS=Number of Servers?
FOR /L %%A IN (1,1,%nS%) DO (
  start java backup_service/Server 224.1.1.1:1111 224.2.2.2:2222 224.3.3.3:3333 1.0 %%A "%%A"
)