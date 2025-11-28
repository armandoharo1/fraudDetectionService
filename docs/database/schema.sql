-- ===========================================
--  Fraud Detection Service - Database Schema
--  PostgreSQL
-- ===========================================

-- DROP TABLE IF EXISTS fraud_alerts CASCADE;
-- DROP TABLE IF EXISTS transaction_events CASCADE;
-- DROP TABLE IF EXISTS users CASCADE;

-- ===========================================
-- USERS TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

-- ===========================================
-- TRANSACTION EVENTS TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS transaction_events (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(255),
    account_id VARCHAR(255),
    amount NUMERIC(38, 2),
    currency VARCHAR(3),
    channel VARCHAR(255),
    ip_address VARCHAR(255),
    country VARCHAR(255),
    merchant_id VARCHAR(255),
    timestamp TIMESTAMPTZ,
    flagged BOOLEAN,
    flag_reason VARCHAR(255),
    risk_score NUMERIC(38, 2)
);

-- Optional useful indexes
CREATE INDEX IF NOT EXISTS idx_transaction_event_account
    ON transaction_events(account_id);

CREATE INDEX IF NOT EXISTS idx_transaction_event_timestamp
    ON transaction_events(timestamp);

-- ===========================================
-- FRAUD ALERTS TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS fraud_alerts (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(255),
    rule_code VARCHAR(255),
    description VARCHAR(5000),
    severity VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Useful index
CREATE INDEX IF NOT EXISTS idx_fraud_alert_transaction
    ON fraud_alerts(transaction_id);

