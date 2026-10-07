#!/bin/sh
cd "$(dirname "$0")"
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name '*.java') || exit 1
java -cp bin test.TextProcessingPipelineDemo
