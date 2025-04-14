#!/bin/bash

# Zielverzeichnis
INSTALL_DIR="/opt/passwortmanager"

# Erstelle Zielverzeichnis, falls nicht vorhanden
echo "📁 Erstelle Installationsverzeichnis: $INSTALL_DIR"
sudo mkdir -p "$INSTALL_DIR"
sudo chown "$USER":"$USER" "$INSTALL_DIR"

cd "$INSTALL_DIR" || { echo "❌ Konnte nicht nach $INSTALL_DIR wechseln"; exit 1; }

# Docker installieren
echo "🔧 Docker wird installiert..."
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Docker-Dienst starten und aktivieren
sudo systemctl start docker
sudo systemctl enable docker

# Lade docker-compose.yml von GitHub (raw URL)
GITHUB_RAW_URL="https://raw.githubusercontent.com/NykyTV/PasswortManager/refs/heads/develop/server-install/docker-compose.yml"

echo "⬇️ Lade docker-compose.yml herunter nach $INSTALL_DIR..."
curl -fsSL "$GITHUB_RAW_URL" -o docker-compose.yml || wget -O docker-compose.yml "$GITHUB_RAW_URL"

echo "✅ docker-compose.yml gespeichert in $INSTALL_DIR"

# Optional: Container starten
# echo "🚀 Starte Docker-Container..."
docker compose -f docker-compose.yml up -d

echo "✅ Installation abgeschlossen in $INSTALL_DIR"