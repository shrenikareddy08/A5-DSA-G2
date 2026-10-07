#!/bin/bash
set -e
cd "$(dirname "$0")"
printf '\n============================================================\n'
printf ' TEXT HACK — Intelligent Text Analytics Engine\n'
printf '============================================================\n'
printf 'Compiling Java backend...\n'
rm -rf bin
mkdir -p bin
find src -name "*.java" -print0 | xargs -0 javac -d bin
printf '\nBuild successful. Starting server...\n'
printf 'Open: http://localhost:8000\n'
printf 'Press Ctrl+C to stop the server.\n\n'
java -cp bin web.Server
