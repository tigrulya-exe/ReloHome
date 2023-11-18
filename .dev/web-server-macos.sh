#!/bin/bash

brew tap static-web-server/static-web-server
brew install static-web-server-bin

# start server with
# .\static-web-server.exe --host 127.0.0.1 --port 8787 --root "D:\IdeaProjects\ReloHome\flat-ads-notifier\notifier-telegram\src\main\resources" --https-redirect --http2 --http2-tls-cert cert.cert --http2-tls-key key.key