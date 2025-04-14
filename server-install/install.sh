#!/bin/bash

# Installiere Docker
echo "🔧 Docker wird installiert..."
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Docker nach der Installation neu starten (je nach System notwendig)
sudo systemctl start docker
sudo systemctl enable docker

# Aktuellen User zur Docker-Gruppe hinzufügen
#sudo usermod -aG docker $USER
#echo "👉 Du musst dich evtl. neu einloggen, damit Docker ohne sudo funktioniert."

# Lade docker-compose.yml von GitHub (raw URL)
GITHUB_RAW_URL="https://raw.githubusercontent.com/DEIN-BENUTZERNAME/DEIN-REPO/main/docker-compose.yml"

echo "⬇️ Lade docker-compose.yml herunter..."
curl -fsSL "$GITHUB_RAW_URL" -o docker-compose.yml || wget -O docker-compose.yml "$GITHUB_RAW_URL"

echo "✅ docker-compose.yml gespeichert."

# Starte die Container (optional)
# echo "🚀 Starte Docker-Container..."
# docker compose up -d

echo "✅ Installation abgeschlossen."
