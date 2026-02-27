#!/bin/bash
set -e

echo "=== Installing Docker Compose v2 + BuildX plugins ==="

# Create plugin directory
sudo mkdir -p /usr/local/lib/docker/cli-plugins

# Install Docker Compose v2
sudo curl -sL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# Install BuildX
BUILDX_URL=$(curl -s https://api.github.com/repos/docker/buildx/releases/latest | python3 -c "
import sys, json
releases = json.load(sys.stdin)
for asset in releases['assets']:
    if 'linux-amd64' in asset['name'] and asset['name'].endswith('linux-amd64'):
        print(asset['browser_download_url'])
        break
")
if [ -n "$BUILDX_URL" ]; then
    sudo curl -sL "$BUILDX_URL" -o /usr/local/lib/docker/cli-plugins/docker-buildx
    sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx
fi

echo "Docker Compose: $(sudo docker compose version)"
echo "Docker BuildX: $(sudo docker buildx version 2>/dev/null || echo 'not installed')"
