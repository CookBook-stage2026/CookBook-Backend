#!/bin/bash

echo "Starting Ollama server..."
ollama serve &

echo "Waiting for Ollama server to start..."
sleep 5

echo "Pulling model..."
ollama create finetuned_llama -f /model_files/Modelfile
ollama run finetuned_llama

echo "Model is ready."