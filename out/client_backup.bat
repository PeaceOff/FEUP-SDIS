set /p nS=Server?
set /p file=File?
set /p rD=Rep Degree?
java backup_service/Client %nS% "BACKUP" %file% %rD%