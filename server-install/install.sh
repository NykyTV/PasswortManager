#!/bin/bash

INSTALL_DIR="/opt/passwortmanager"

# 📁 Erstelle Zielverzeichnis
echo "📁 Erstelle Installationsverzeichnis: $INSTALL_DIR"
sudo mkdir -p "$INSTALL_DIR"
sudo chown "$USER:$USER" "$INSTALL_DIR"
cd "$INSTALL_DIR" || { echo "❌ Wechsel nach $INSTALL_DIR fehlgeschlagen"; exit 1; }

# 🧰 Installiere curl und wget, falls nicht vorhanden
echo "🔍 Überprüfe auf curl und wget..."
if ! command -v curl >/dev/null 2>&1; then
    echo "📦 curl wird installiert..."
    sudo apt update && sudo apt install -y curl
fi

if ! command -v wget >/dev/null 2>&1; then
    echo "📦 wget wird installiert..."
    sudo apt update && sudo apt install -y wget
fi

# 🔧 Docker installieren
echo "🐳 Docker wird installiert..."
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# 🛠️ Docker-Dienst starten und aktivieren
if command -v systemctl >/dev/null 2>&1; then
    sudo systemctl start docker
    sudo systemctl enable docker
else
    echo "⚠️ Achtung: systemctl nicht verfügbar – bitte Docker manuell starten."
fi

# ⬇️ Lade docker-compose.yml herunter
GITHUB_RAW_URL="https://raw.githubusercontent.com/NykyTV/PasswortManager/refs/heads/develop/server-install/docker-compose.yml"

echo "⬇️ Lade docker-compose.yml herunter..."
curl -fsSL "$GITHUB_RAW_URL" -o docker-compose.yml || wget -O docker-compose.yml "$GITHUB_RAW_URL"

echo "✅ docker-compose.yml gespeichert in $INSTALL_DIR"

echo "Starte Docker-Container mit: docker compose up -d"

# 🚀 Optional: Container starten
# echo "🚀 Starte Docker-Container..."
# docker compose -f docker-compose.yml up -d

echo "✅ Installation abgeschlossen in $INSTALL_DIR"