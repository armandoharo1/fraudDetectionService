# 🇺🇸 Fraud Detection Service – Real-Time Fraud Engine  
### Java 17 • Spring Boot 3.5 • PostgreSQL • Redis • Kafka • Docker

A professional **real-time fraud detection microservice**, inspired by financial-grade architectures used in digital banking.

This service evaluates incoming transactions, applies fraud rules, generates alerts, and assigns a dynamic risk score.  
It is designed to operate in an event-driven ecosystem and can scale horizontally.

---

## 🛠️ **Tech Stack**

| Layer | Technology |
|------|------------|
| Backend | **Java 17, Spring Boot 3.5, Lombok** |
| Database | **PostgreSQL, Spring Data JPA** |
| Cache / Rate Limit | **Redis** |
| Messaging | **Apache Kafka + Zookeeper** |
| Containerization | **Docker & Docker Compose** |
| Documentation | **Swagger / Springdoc OpenAPI** |
| Monitoring | **Spring Boot Actuator** |

---

## ✨ **Key Features**

- Real-time transaction evaluation  
- Pluggable fraud rules engine  
- High-risk country detection  
- High-amount detection  
- Alert persistence  
- Risk scoring system (0–100)  
- Fully documented REST API  
- Docker-ready local infrastructure

---

## 📐 **Architectural Overview**

Clean modular structure:

```
src/main/java/com.armando.frauddetection
│
├── api.controller        → REST Controllers
│
├── domain
│   ├── model             → JPA Entities
│   ├── repository        → JPA Repositories
│   └── service           → Business Services (Fraud Engine)
│
├── rules                 → Fraud Rules
│
├── config                → Swagger, Security, Beans
│
└── FraudDetectionServiceApplication
```

---

## 🔄 **How the Engine Works (Flow)**

### 1. A client sends a transaction  
`POST /api/v1/transactions`

It is validated and stored in PostgreSQL.

### 2. The fraud engine evaluates rules  
Each rule returns:

- triggered (true/false)  
- severity level  
- description  
- ruleCode  

### 3. Alerts are generated  
Stored in `fraud_alerts`.

### 4. Risk Score is calculated  
Range: **0–100** based on severity weight.

### 5. Response includes:
- transaction (flagged or not)  
- all fraud alerts  

---

## 🚀 **Run Locally**

### 1. Start infrastructure
```bash
docker compose up -d
```

### 2. Build & run the service
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Example Request

**POST /api/v1/transactions**

```json
{
  "transactionId": "TX-3001",
  "accountId": "ACC-77777",
  "amount": 4500,
  "currency": "USD",
  "channel": "WEB",
  "ipAddress": "190.10.20.30",
  "country": "RU",
  "merchantId": "M-777"
}
```

---

## 🗄️ **Database Tables**

### `transaction_events`
| Column | Description |
|--------|-------------|
| id | Primary key |
| transaction_id | Unique transaction id |
| account_id | Originating account |
| amount | transaction amount |
| currency | ISO currency |
| flagged | boolean |
| risk_score | 0–100 |
| flag_reason | concatenated rules triggered |

### `fraud_alerts`
| Column | Description |
|--------|-------------|
| id | Primary key |
| transaction_id | reference |
| rule_code | rule name |
| severity | LOW/MEDIUM/HIGH |
| description | alert description |
| created_at | timestamp |

---

## 📊 **Roadmap**

### ✔ Completed
- Microservice foundation  
- Fraud rules engine  
- Persistence / alert storage  
- Swagger documentation  
- Postgres + Redis + Kafka infra  

### 🔜 Next Steps
- Kafka integration (Producer & Consumer)  
- Real-time dashboards (React + WebSockets)  
- Grafana + Prometheus monitoring  
- JWT Authentication & RBAC  
- Full Clean Architecture (DDD)  
- Dockerfile + Cloud deploy (ECS/K8s)  

---

# 🇪🇸 Servicio de Detección de Fraudes – Motor en Tiempo Real  
### Java 17 • Spring Boot 3.5 • PostgreSQL • Redis • Kafka • Docker

Este microservicio evalúa transacciones en tiempo real aplicando reglas antifraude, generando alertas y asignando un puntaje de riesgo.

Arquitectura limpia, modular y preparada para operar en un ecosistema basado en eventos (Kafka).

---

# ✍ **Autor**
**Armando Haro**  
Backend Developer – Java | Spring Boot | Microservices  
GitHub: https://github.com/armandoharo1
