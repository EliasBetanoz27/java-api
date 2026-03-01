#!/bin/bash

echo "Testing /login endpoint..."

# Test 1: Invalid credentials (should fail)
echo "Test 1: Invalid credentials"
curl -s -X POST "http://localhost:8080/login" \
  -d "taxId=INVALID123&password=wrongpassword" \
  -H "Content-Type: application/x-www-form-urlencoded" | jq .

echo ""

# Test 2: Valid credentials (first create a user, then login)
echo "Test 2: Create user and login"
curl -s -X POST "http://localhost:8080/users" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "name": "Test User",
    "phone": "1234567890",
    "password": "password123",
    "taxId": "BOMM850101YYY"
  }' | jq .

echo ""

# Test 3: Login with valid credentials
echo "Test 3: Login with valid credentials"
curl -s -X POST "http://localhost:8080/login" \
  -d "taxId=BOMM850101YYY&password=password123" \
  -H "Content-Type: application/x-www-form-urlencoded" | jq .
