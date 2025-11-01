#!/bin/bash

# Script pour déployer l'application .war dans Tomcat

# --- Variables à configurer ---
# Chemin vers le dossier webapps de votre serveur Tomcat
TOMCAT_WEBAPPS="/home/faniry/Documents/apache-tomcat-10.1.48/webapps"

# Nom du fichier .war (sans l'extension)
WAR_NAME="test_project"

# Chemin vers le dossier du projet (là où se trouve le dossier target)
PROJECT_DIR="/home/faniry/Documents/windows_faniry_backup/GitHub/framework_project/test_project_new"
# --- Fin de la configuration ---


# Chemin complet vers le fichier .war source et le dossier de destination
SOURCE_WAR="$PROJECT_DIR/target/$WAR_NAME.war"
DEST_WAR_FILE="$TOMCAT_WEBAPPS/$WAR_NAME.war"
DEST_APP_DIR="$TOMCAT_WEBAPPS/$WAR_NAME"

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
    echo "Ancien .war supprimé."
fi

if [ -d "$DEST_APP_DIR" ]; then
    rm -rf "$DEST_APP_DIR"
    echo "Ancien dossier de l'application supprimé."
fi

# Copier le nouveau .war
echo "Copie du nouveau fichier .war vers $TOMCAT_WEBAPPS..."
cp "$SOURCE_WAR" "$TOMCAT_WEBAPPS/"

echo "Déploiement terminé avec succès."
