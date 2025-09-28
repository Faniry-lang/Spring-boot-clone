@echo off
REM Aller dans le dossier framework
cd /d %~dp0

REM Compiler les classes Java dans build
javac -cp lib\servlet-api.jar -d build com\itu\framework\*.java

REM Créer le jar à partir du contenu de build
jar cvf framework.jar -C build .

REM Copier le jar généré vers test_project/lib
copy framework.jar ..\test_project\lib\

echo === Build terminé avec succès ===
