@echo off
setlocal enabledelayedexpansion

@REM chemin de apache tomcat sous linux :  /home/faniry/Documents/apache-tomcat-10.1.48

:: Déclaration des variables
set "work_dir=C:\Users\ME-PC\Documents\GitHub\Spring-boot-clone\test_project_new"
set "web_apps=C:\Users\ME-PC\Documents\apache-tomcat-10.1.50\apache-tomcat-10.1.50\webapps"
set "war_name=test_project"

:: Effacer le fichier .war dans [web_apps] s'il existe
if exist "%web_apps%\%war_name%.war" (
    del /f /q "%web_apps%\%war_name%.war"
)

:: Copier le fichier .war vers [web_apps]
copy /y "%work_dir%\target\%war_name%.war" "%web_apps%"

echo Déploiement terminé.
pause
