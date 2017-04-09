start rmiregistry
set /p nS=Number of Servers?
set /p sS=Starting at?
set /p vS=Server Version?
set /A total= %nS% + %sS% - 1
FOR /L %%A IN (%sS%,1,%total%) DO (
  start java backup_service/Server %vS% %%A "%%A" 224.1.1.1:1111 224.2.2.2:2222 224.3.3.3:3333
)