# 🚀 Quick Start: Comprehensive Logging System

## Overview

The Zaina application now has **comprehensive logging** for all requests - both HTTP REST API calls and WebSocket communications.

## 🔥 Quick Start (3 Steps)

### 1. Start Application with Detailed Logging

```bash
# Run the automated setup script
./test-logging.sh
```

### 2. Monitor Logs in Real-Time

```bash
# In a new terminal, run the log monitor
./monitor-logs.sh
```

### 3. Test the System

```bash
# Open the WebSocket example in your browser
open frontend-websocket-example.html
```

## 📊 What Gets Logged

### HTTP Requests

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🌐 HTTP REQUEST START - 2024-01-20 14:30:25.123
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Method: POST
║ URI: /api/auth/signin
║ Query String: None
║ Remote Address: 127.0.0.1
║ User Agent: Mozilla/5.0...
║ Content Type: application/json
║ Content Length: 45
║ Authorization: Bearer ***[HIDDEN]***
║ Headers: host: localhost:8080, content-type: application/json
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🏁 HTTP REQUEST END - 2024-01-20 14:30:25.245
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Status: ✅ 200 (OK)
║ Duration: 122ms
║ Response Content Type: application/json
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

### WebSocket Events

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🔌 WEBSOCKET CONNECT ATTEMPT - 2024-01-20 14:30:30.456
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Session ID: 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6
║ Command: CONNECT
║ Headers: {Authorization=[Bearer ***[HIDDEN]***]}
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 💬 WEBSOCKET CHAT MESSAGE BROADCAST - 2024-01-20 14:35:15.123
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Message ID: 6ba7b810-9dad-11d1-80b4-00c04fd430c8
║ From: 550e8400-e29b-41d4-a716-446655440000 (Fatima Al-Qadi)
║ To: 6ba7b811-9dad-11d1-80b4-00c04fd430c8
║ Content: Hello! How are you?
║ Destination: /user/6ba7b811-9dad-11d1-80b4-00c04fd430c8/queue/messages
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

## 🔍 Log Monitoring Options

The `monitor-logs.sh` script provides filtered viewing:

1. **🌐 HTTP Requests Only** - See all REST API calls
2. **🔌 WebSocket Events Only** - Real-time WebSocket activity
3. **💬 Chat Messages Only** - Message broadcasts and typing indicators
4. **🔐 Authentication Events Only** - Login attempts and JWT validation
5. **⚠️ Errors and Warnings Only** - Problem detection
6. **📋 All Logs** - Full comprehensive stream
7. **🔍 Custom Filter** - Your own grep pattern

## 📁 Log Files

- **Real-time**: `logs/zaina-application.log`
- **Archives**: `logs/zaina-application.{date}.{index}.log.gz`
- **Max size**: 10MB per file, 30 files kept, 1GB total limit

## 🔧 Manual Commands

```bash
# Start with logging profile
export SPRING_PROFILES_ACTIVE=logging
./mvnw spring-boot:run

# Monitor specific events
tail -f logs/zaina-application.log | grep "WEBSOCKET"
tail -f logs/zaina-application.log | grep "HTTP REQUEST"
tail -f logs/zaina-application.log | grep "CHAT MESSAGE"

# Search for user activity
grep "User ID: 550e8400-e29b-41d4-a716-446655440000" logs/zaina-application.log

# Find slow requests (>1000ms)
grep -E "Duration: [0-9]{4,}ms" logs/zaina-application.log

# Count authentication failures
grep "AUTHENTICATION FAILED" logs/zaina-application.log | wc -l
```

## 🛡️ Security Features

- ✅ Authorization headers automatically masked as `***[HIDDEN]***`
- ✅ JWT tokens show only first 20 characters in error logs
- ✅ Sensitive data truncated in previews
- ✅ User info limited to IDs and usernames

## 🎯 Testing Scenarios

### 1. Test HTTP Request Logging

```bash
# In one terminal
./test-logging.sh

# In another terminal
./monitor-logs.sh
# Select option 1 (HTTP Requests Only)

# Make API calls
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

### 2. Test WebSocket Logging

```bash
# Start monitoring WebSocket events
./monitor-logs.sh
# Select option 2 (WebSocket Events Only)

# Open the frontend and connect
open frontend-websocket-example.html
# Login and send messages
```

### 3. Test Chat Message Logging

```bash
# Monitor chat messages
./monitor-logs.sh
# Select option 3 (Chat Messages Only)

# Send messages through the frontend
# Watch real-time message broadcasts appear in logs
```

## 📈 Performance Impact

- **DEBUG level**: ~5-10% performance impact (development)
- **INFO level**: ~1-2% performance impact (staging)
- **WARN level**: Minimal performance impact (production)

## 🔗 Related Files

- `LOGGING_DOCUMENTATION.md` - Complete documentation
- `WEBSOCKET_DOCUMENTATION.md` - WebSocket system details
- `frontend-websocket-example.html` - Test frontend
- `src/main/resources/application-logging.yml` - Configuration

That's it! You now have complete visibility into all requests and real-time communications. 🎉
