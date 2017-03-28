set /p nS=Server?
set /p file=File?
java backup_service/Client %nS% "DELETE" %file%