#!/bin/sh

set -e

garage server &

until nc -z localhost 3901; do
  sleep 1
done

until garage status > /dev/null 2>&1; do
  sleep 1
done

NODE_ID=$(garage node id | grep -oE '[0-9a-f]{64}' | head -n 1 | cut -c1-16)
echo "Rent Node ID: $NODE_ID"

CURRENT_VERSION=$(garage layout show | grep -i "version:" | awk '{print $NF}' | tr -cd '0-9')

if [ -z "$CURRENT_VERSION" ] || [ "$CURRENT_VERSION" = "0" ]; then

    garage layout assign "$NODE_ID" --capacity 10G -z dc1
    garage layout apply --version 1

    sleep 3

    garage key create my-admin-key || true
    KEY_ID=$(garage key list | grep "my-admin-key" | awk '{print $1}')
    echo "S3 Access Key ID: $KEY_ID"

    garage bucket create chum-bucket || true

    garage bucket allow chum-bucket --key "$KEY_ID" --read --write --owner || true

else
    echo "Garage är redan konfigurerat (Version: $CURRENT_VERSION). Hoppar över initiering."
    # Om du vill se ditt Key ID även vid omstart kan du köra:
    KEY_ID=$(garage key list | grep "my-admin-key" | awk '{print $1}')
    echo "S3 Access Key ID: $KEY_ID"
fi

garage status

wait