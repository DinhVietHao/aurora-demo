# embedding_service.py
from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
import numpy as np

app = Flask(__name__)

# Load mô hình embedding nhẹ, miễn phí
model = SentenceTransformer('all-MiniLM-L6-v2')

@app.route('/embed', methods=['POST'])
def embed():
    data = request.get_json()
    texts = data.get('texts', [])
    if not texts:
        return jsonify({"error": "Missing 'texts' field"}), 400

    embeddings = model.encode(texts).tolist()
    return jsonify({"embeddings": embeddings})

if __name__ == '__main__':
    # Chạy local server tại cổng 5000
    app.run(host='0.0.0.0', port=5000)