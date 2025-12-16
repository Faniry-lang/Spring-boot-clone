#!/bin/bash

# Script pour déployer l'application .war dans Tomcat

# --- Variables à configurer ---
# Chemin vers le dossier webapps de votre serveur Tomcat
TOMCAT_WEBAPPS="/home/faniry/Documents/apache-tomcat-10.1.48/webapps"

# Nom du fichier .war (sans l'extension)
# We deploy as the ROOT context so the app is reachable at '/'.
# The source WAR can keep its normal name; we copy it to ROOT.war on the Tomcat webapps.
WAR_NAME="test_project"

# Chemin vers le dossier du projet (là où se trouve le dossier target)
PROJECT_DIR="/home/faniry/Documents/GitHub/framework_project/test_project_new"
# --- Fin de la configuration ---


# Chemin complet vers le fichier .war source et le dossier de destination
SOURCE_WAR="$PROJECT_DIR/target/$WAR_NAME.war"
# Always deploy to ROOT.war so the application is mounted at '/'
DEST_WAR_FILE="$TOMCAT_WEBAPPS/ROOT.war"
DEST_APP_DIR="$TOMCAT_WEBAPPS/ROOT"

echo "Début du déploiement..."

# Vérifier si le .war source existe
if [ ! -f "$SOURCE_WAR" ]; then
    echo "Erreur : Le fichier $SOURCE_WAR n'a pas été trouvé."
    echo "Veuillez d'abord compiler le projet (ex: mvn package)."
    exit 1
fi

# Suppression des anciennes versions
echo "Suppression des anciennes versions..."
if [ -f "$DEST_WAR_FILE" ]; then
    rm -f "$DEST_WAR_FILE"
    echo "Ancien ROOT.war supprimé."
fi

if [ -d "$DEST_APP_DIR" ]; then
    rm -rf "$DEST_APP_DIR"
    echo "Ancien dossier ROOT supprimé."
fi

# Copier le nouveau .war sous le nom ROOT.war
echo "Copie du nouveau fichier .war vers $DEST_WAR_FILE..."
cp "$SOURCE_WAR" "$DEST_WAR_FILE"

echo "Déploiement terminé avec succès."
