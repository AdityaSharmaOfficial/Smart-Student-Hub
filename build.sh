#!/bin/bash
# Smart Sphere Student Hub - Linux/macOS build script
echo "=== Smart Sphere Student Hub - Build ==="
mkdir -p out
javac -d out \
  src/model/*.java \
  src/filehandler/*.java \
  src/service/*.java \
  src/ui/*.java \
  src/main/*.java

if [ $? -eq 0 ]; then
  echo "=== Build SUCCESS ==="
  echo "Run: java -cp out main.Main"
else
  echo "=== Build FAILED ==="
fi
