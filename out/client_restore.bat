set /p nS=Server?
set /p file=File Path?
java backup_service/Client %nS% "RESTORE" %file%
pause