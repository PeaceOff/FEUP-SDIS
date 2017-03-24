set /p nS=Server?
set /p file=FileID?
java backup_service/Client %nS% "RESTORE" %file%