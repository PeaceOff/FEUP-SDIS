set /p nS=Server?
set /p tam=Tamanho?
java backup_service/Client %nS% "RECLAIM" %tam%