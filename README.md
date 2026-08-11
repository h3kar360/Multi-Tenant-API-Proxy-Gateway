# 🚀 PROXIER - Multi-Tenant API Proxy Gateway

A production-ready, self-hosted API gateway with authentication, rate limiting, caching, and a developer dashboard — built for BaaS (Backend-as-a-Service) platforms.

---

## ✨ Features

| Feature                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| 🔐 **JWT Authentication**  | Secure login with refresh tokens                |
| 🔑 **API Keys**            | Long-lived proxy keys for developers            |
| ⚡ **Rate Limiting**       | Distributed rate limiting with Bucket4j + Redis |
| 💾 **Smart Caching**       | Threshold-based caching with Redis              |
| 📊 **Developer Dashboard** | Manage APIs, view usage, copy keys              |
| 🐳 **Dockerized**          | Run everything with `docker-compose up`         |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Docker Network                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────────┐    │
│  │   Frontend   │     │   Backend    │     │   PostgreSQL     │    │
│  │  (React +    │────▶│ (Spring Boot)│────▶│   (Database)     │    │
│  │   Tailwind)  │     │   :8080      │     │   :5432          │    │
│  │   :3000      │     └──────┬───────┘     └──────────────────┘    │
│  └──────────────┘            │                                     │
│                              ▼                                     │
│                    ┌──────────────────┐                           │
│                    │     Redis        │                           │
│                    │ (Rate Limiting   │                           │
│                    │  + Caching)      │                           │
│                    │   :6379          │                           │
│                    └──────────────────┘                           │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose installed
- Java 21+ (for local development)

### Run with Docker (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/proxier.git
cd proxier

# 2. Build the application
./mvnw clean package -DskipTests

# 3. Start all services
docker-compose up -d

# 4. Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
```

### Run Locally (Development)

```bash
# Backend
./mvnw spring-boot:run

# Frontend
cd client
npm install
npm run dev
```

---

## 🔐 Authentication

### 1. Sign Up

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dev@company.com",
    "password": "securepassword",
    "username": "dev_user"
  }'
```

**Response:**

```json
{
    "id": 1,
    "clientEmail": "dev@company.com",
    "clientName": "dev_user",
    "proxyKey": "pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4"
}
```

> ⚠️ **Important:** The `proxyKey` is shown **only once**. Save it immediately.

---

### 2. Log In

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dev@company.com",
    "password": "securepassword"
  }'
```

**Response:**

```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "clientName": "dev_user",
    "refreshToken": "refresh_abc123...",
    "expiresIn": 3600000
}
```

---

### 3. Refresh Access Token

```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Cookie: refresh_token=refresh_abc123..."
```

**Response:**

```json
{
    "newAccessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 🚦 Using the Proxy

### Authentication Methods

| Method        | Header                                | When to Use            |
| ------------- | ------------------------------------- | ---------------------- |
| **JWT**       | `Authorization: Bearer <accessToken>` | Dashboard API calls    |
| **Proxy Key** | `X-Proxy-Key: pk_live_xxx`            | Proxy forwarding calls |

---

### 📡 Forwarding Requests

**Endpoint Format:**

```
http://localhost:8080/proxy/v1/gateway/{serviceName}/{path}
```

**Headers:**

```
X-Proxy-Key: pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4
```

**Example: Weather API**

```bash
curl -X GET "http://localhost:8080/proxy/v1/gateway/weather/data/2.5/weather?q=London&units=metric" \
  -H "X-Proxy-Key: pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4"
```

**Example: OpenAI**

```bash
curl -X POST "http://localhost:8080/proxy/v1/gateway/openai/v1/chat/completions" \
  -H "X-Proxy-Key: pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-3.5-turbo",
    "messages": [{"role": "user", "content": "Hello, world!"}]
  }'
```

---

### 🌐 JavaScript/TypeScript (Fetch)

```typescript
const PROXY_KEY = "pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4";
const PROXY_URL = "http://localhost:8080/proxy/v1/gateway";

async function proxyRequest(
    service: string,
    path: string,
    options: RequestInit = {},
) {
    const response = await fetch(`${PROXY_URL}/${service}${path}`, {
        ...options,
        headers: {
            ...options.headers,
            "X-Proxy-Key": PROXY_KEY,
        },
    });

    if (!response.ok) {
        throw new Error(`Proxy error: ${response.status}`);
    }

    return response.json();
}

// Usage
const weather = await proxyRequest(
    "weather",
    "/data/2.5/weather?q=London&units=metric",
);
```

---

### 🐍 Python (Requests)

```python
import requests

PROXY_KEY = "pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4"
PROXY_URL = "http://localhost:8080/proxy/v1/gateway"

def proxy_request(service, path, method="GET", data=None):
    url = f"{PROXY_URL}/{service}{path}"
    headers = {"X-Proxy-Key": PROXY_KEY}

    response = requests.request(method, url, headers=headers, json=data)
    response.raise_for_status()
    return response.json()

# Usage
weather = proxy_request("weather", "/data/2.5/weather?q=London&units=metric")
```

---

### 🔧 Node.js (Axios)

```javascript
const axios = require("axios");

const PROXY_KEY = "pk_live_0SZpo9pnZFhHzecAPEJ8Oy_9qruqNhRcY-4F_P-Jh4";
const PROXY_URL = "http://localhost:8080/proxy/v1/gateway";

const api = axios.create({
    baseURL: PROXY_URL,
    headers: { "X-Proxy-Key": PROXY_KEY },
});

// Usage
const weather = await api.get(
    "/weather/data/2.5/weather?q=London&units=metric",
);
```

---

## 🗂️ Service Management

### 1. Register a New Service

```bash
curl -X POST http://localhost:8080/proxy/v1/api-info \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "apiName": "weather",
    "apiUrl": "https://api.openweathermap.org/data/2.5",
    "connectTimeout": 5000,
    "readTimeout": 10000
  }'
```

---

### 2. List All Services

```bash
curl -X GET http://localhost:8080/proxy/v1/api-info \
  -H "Authorization: Bearer <accessToken>"
```

---

### 3. Update a Service

```bash
curl -X PUT http://localhost:8080/proxy/v1/api-info/1 \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "apiName": "weather-v2",
    "apiUrl": "https://api.openweathermap.org/data/3.0"
  }'
```

---

### 4. Delete a Service

```bash
curl -X DELETE http://localhost:8080/proxy/v1/api-info/1 \
  -H "Authorization: Bearer <accessToken>"
```

---

## ⚙️ Configuration

### Environment Variables (Backend)

| Variable                     | Description               | Default                                     |
| ---------------------------- | ------------------------- | ------------------------------------------- |
| `SPRING_DATASOURCE_URL`      | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/proxy_db` |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username       | `proxy_user`                                |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password       | `proxy_pass`                                |
| `SPRING_REDIS_HOST`          | Redis host                | `localhost`                                 |
| `SPRING_REDIS_PORT`          | Redis port                | `6379`                                      |
| `JWT_SECRET`                 | JWT signing key           | Change this!                                |
| `JWT_EXPIRATION`             | JWT expiration (ms)       | `3600000` (1 hour)                          |

### Environment Variables (Frontend)

| Variable       | Description     |
| -------------- | --------------- | ----------------------- |
| `VITE_API_URL` | Backend API URL | `http://localhost:8080` |

---

## 📊 Rate Limiting & Caching

| Feature            | Implementation   | Default                           |
| ------------------ | ---------------- | --------------------------------- |
| **Rate Limiting**  | Bucket4j + Redis | 100 requests/min per client       |
| **Caching**        | Redis            | 60s TTL (FREE), 300s (PRO)        |
| **Cache Strategy** | Threshold-based  | Caches after 3 identical requests |

---

## 🛠️ Tech Stack

| Layer                | Technology                           |
| -------------------- | ------------------------------------ |
| **Backend**          | Spring Boot 4.0.6, Java 21           |
| **Database**         | PostgreSQL 16                        |
| **Cache**            | Redis 7.2                            |
| **Rate Limiting**    | Bucket4j                             |
| **Authentication**   | JWT + HttpOnly Refresh Tokens        |
| **Frontend**         | React 19 + TypeScript + Tailwind CSS |
| **Containerization** | Docker + Docker Compose              |

---

## 📋 API Reference

### Authentication

| Method | Endpoint        | Description              |
| ------ | --------------- | ------------------------ |
| `POST` | `/auth/signup`  | Create account           |
| `POST` | `/auth/login`   | Log in                   |
| `POST` | `/auth/refresh` | Refresh access token     |
| `POST` | `/auth/logout`  | Log out                  |
| `POST` | `/auth/verify`  | Verify account           |
| `POST` | `/auth/resend`  | Resend verification code |

### Proxy

| Method | Endpoint                         | Description                |
| ------ | -------------------------------- | -------------------------- |
| `*`    | `/proxy/v1/gateway/{service}/**` | Forward request to backend |

### API Management

| Method   | Endpoint                  | Description |
| -------- | ------------------------- | ----------- |
| `GET`    | `/proxy/v1/api-info`      | List APIs   |
| `POST`   | `/proxy/v1/api-info`      | Create API  |
| `PUT`    | `/proxy/v1/api-info/{id}` | Update API  |
| `DELETE` | `/proxy/v1/api-info/{id}` | Delete API  |

### Proxy Key

| Method | Endpoint                   | Description          |
| ------ | -------------------------- | -------------------- |
| `POST` | `/proxy/v1/key/generate`   | Generate proxy key   |
| `POST` | `/proxy/v1/key/regenerate` | Regenerate proxy key |

## ⭐ Show Your Support

If you found this project helpful, please give it a ⭐ on GitHub!
