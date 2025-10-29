# embedding_service.py
from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
import numpy as np
import logging
import time

app = Flask(__name__)

# --- Logging setup ---
logging.basicConfig(
    level=logging.INFO,
    format='[%(asctime)s] %(levelname)s in %(module)s: %(message)s'
)

# --- Load embedding model ---
logging.info("🔹 Loading SentenceTransformer model... (this may take a few seconds)")
model = SentenceTransformer('VoVanPhuc/sup-SimCSE-VietNamese-phobert-base')
logging.info("✅ Model loaded successfully: VoVanPhuc/sup-SimCSE-VietNamese-phobert-base")

# --- API route ---
@app.route('/embed', methods=['POST'])
def embed():
    start_time = time.time()
    try:
        data = request.get_json(force=True)
        texts = data.get('texts', [])

        # Validate input
        if not isinstance(texts, list) or not texts:
            return jsonify({"error": "Missing or invalid 'texts' field. Expected a non-empty list."}), 400
        if len(texts) > 50:
            return jsonify({"error": "Too many texts in one request (max 50)."}), 400

        # Compute embeddings
        embeddings = model.encode(texts, convert_to_numpy=True, show_progress_bar=False)
        elapsed = (time.time() - start_time) * 1000

        logging.info(f"✅ Generated {len(texts)} embeddings in {elapsed:.1f} ms")
        return jsonify({"embeddings": embeddings.tolist()})

    except Exception as e:
        logging.error(f"❌ Error while embedding: {str(e)}")
        return jsonify({"error": str(e)}), 500

# --- Health check route ---
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "ok", "model": "VoVanPhuc/sup-SimCSE-VietNamese-phobert-base"}), 200

# --- Run server ---
if __name__ == '__main__':
    # Chạy trên tất cả IP, cổng 5000, không debug, đa luồng
    app.run(host='0.0.0.0', port=5000, debug=False, threaded=True)