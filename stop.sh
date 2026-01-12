#!/bin/bash

echo "🛑 Arrêt Backend"

# Arrêter backend (Ctrl+C suffit normalement)
pkill -f spring-boot

# Arrêter PostgreSQL
sudo docker compose down

echo "✅ Backend arrêté"