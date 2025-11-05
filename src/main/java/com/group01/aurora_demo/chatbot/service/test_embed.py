import requests

url = "http://127.0.0.1:5000/embed"
data = {"texts": ["Xin chào", "Học AI thật thú vị"]}

response = requests.post(url, json=data)
print(response.json())
