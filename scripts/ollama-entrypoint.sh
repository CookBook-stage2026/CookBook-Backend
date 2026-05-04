#!/bin/bash

echo "Starting Ollama server..."
/bin/ollama serve &

echo "Waiting for Ollama server to start..."
sleep 5

echo "Pulling model..."
/bin/ollama run "${OLLAMA_MODEL:-llama3.2:3b}"

echo "Model is ready."
wait