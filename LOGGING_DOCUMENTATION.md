# Comprehensive Logging System Documentation

## Overview

The Zaina application now includes a comprehensive logging system that tracks:

- **HTTP REST API Requests** - Complete request/response logging with timing
- **WebSocket Communications** - Real-time message tracking, connections, and events
- **Authentication Events** - Login attempts, JWT validation, and security events
- **Database Operations** - SQL queries and JPA operations
- **User Activities** - Connection requests, message sending, status updates

## 🔧 Quick Setup

### Enable Detailed Logging

To enable comprehensive logging, start your application with the logging profile:

```bash
# For development with detailed logs
./mvnw spring-boot:run -Dspring.profiles.active=logging

# Or set environment variable
export SPRING_PROFILES_ACTIVE=logging
./mvnw spring-boot:run
```

### Logging Levels Available

1. **Basic Logging** (default) - Essential application events
2. **Detailed Logging** (logging profile) - All requests, WebSocket events, and debugging information

## 📊 HTTP Request Logging

### What's Logged

Every HTTP request includes:

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🌐 HTTP REQUEST START - 2024-01-20 14:30:25.123
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Method: POST
║ URI: /api/auth/signin
║ Query String: None
║ Remote Address: 127.0.0.1
║ User Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)...
║ Content Type: application/json
║ Content Length: 45
║ Authorization: None
║ Headers: host: localhost:8080, content-type: application/json, accept: */*
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

### Response Logging

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🏁 HTTP REQUEST END - 2024-01-20 14:30:25.245
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Method: POST
║ URI: /api/auth/signin
║ Status: ✅ 200 (OK)
║ Duration: 122ms
║ Response Content Type: application/json
║ Exception: None
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

## 🔌 WebSocket Logging

### Connection Events

**WebSocket Connection Attempt:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🔌 WEBSOCKET CONNECT ATTEMPT - 2024-01-20 14:30:30.456
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Session ID: 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6
║ Command: CONNECT
║ Headers: {Authorization=[Bearer ***[HIDDEN]***], accept-version=[1.1,1.0]}
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

**Successful Authentication:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ ✅ WEBSOCKET AUTHENTICATION SUCCESS - 2024-01-20 14:30:30.478
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Session ID: 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6
║ Username: fatima.alqadi@rise.sa
║ User ID: 550e8400-e29b-41d4-a716-446655440000
║ Authorities: ROLE_USER
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

**Session Connected:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🟢 WEBSOCKET SESSION CONNECTED - 2024-01-20 14:30:30.485
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Session ID: 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6
║ User ID: 550e8400-e29b-41d4-a716-446655440000
║ Username: fatima.alqadi@rise.sa
║ Email: fatima.alqadi@rise.sa
║ Role: PARTICIPANT
║ Authorities: ROLE_USER
║ Remote Address: 127.0.0.1
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

### Message Events

**Chat Message Broadcast:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 💬 WEBSOCKET CHAT MESSAGE BROADCAST - 2024-01-20 14:35:15.123
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Message ID: 6ba7b810-9dad-11d1-80b4-00c04fd430c8
║ From: 550e8400-e29b-41d4-a716-446655440000 (Fatima Al-Qadi)
║ To: 6ba7b811-9dad-11d1-80b4-00c04fd430c8
║ Content Length: 23 characters
║ Content Preview: Hello! How are you?
║ Sent At: 2024-01-20T14:35:15.120
║ Destination: /user/6ba7b811-9dad-11d1-80b4-00c04fd430c8/queue/messages
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

**Typing Indicators:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ ⌨️ WEBSOCKET TYPING INDICATOR - 2024-01-20 14:35:10.456
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ From: 550e8400-e29b-41d4-a716-446655440000 (Fatima Al-Qadi)
║ To: 6ba7b811-9dad-11d1-80b4-00c04fd430c8
║ Typing: true
║ Destination: /user/6ba7b811-9dad-11d1-80b4-00c04fd430c8/queue/typing
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

**User Status Updates:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 👤 WEBSOCKET USER STATUS UPDATE - 2024-01-20 14:30:30.485
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ User ID: 550e8400-e29b-41d4-a716-446655440000
║ Status Change: Offline → Online
║ Last Seen: Currently active
║ Total Online Users: 3
║ Destination: /topic/user-status (broadcast)
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

**Connection Requests:**

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║ 🤝 WEBSOCKET CONNECTION NOTIFICATION - 2024-01-20 14:40:12.789
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║ Connection ID: 7ba7b812-9dad-11d1-80b4-00c04fd430c8
║ Type: CONNECT
║ Status: PENDING
║ From: 550e8400-e29b-41d4-a716-446655440000 (Fatima Al-Qadi)
║ To: 6ba7b811-9dad-11d1-80b4-00c04fd430c8 (Nour Hassan)
║ Requested At: 2024-01-20T14:40:12.785
║ Destination: /user/6ba7b811-9dad-11d1-80b4-00c04fd430c8/queue/connections
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

## 📋 Log Categories

### 1. Authentication Logs

- Login attempts
- JWT token validation
- WebSocket authentication
- Access control events

### 2. API Request Logs

- All HTTP requests/responses
- Request timing and performance
- Error tracking
- User agent and IP tracking

### 3. WebSocket Communication Logs

- Connection establishment/termination
- Message broadcasting
- Subscription management
- Real-time event tracking

### 4. Database Operation Logs

- SQL query execution
- JPA operations
- Transaction boundaries
- Performance metrics

### 5. Business Logic Logs

- Message sending/receiving
- Connection requests/responses
- User status changes
- System events

## 🔍 Log Analysis

### Finding Specific Events

**All requests from a specific user:**

```bash
grep "User ID: 550e8400-e29b-41d4-a716-446655440000" logs/zaina-application.log
```

**WebSocket connection events:**

```bash
grep "WEBSOCKET.*CONNECT" logs/zaina-application.log
```

**Failed authentication attempts:**

```bash
grep "AUTHENTICATION FAILED\|AUTHENTICATION ERROR" logs/zaina-application.log
```

**Performance analysis (slow requests > 1000ms):**

```bash
grep -E "Duration: [0-9]{4,}ms" logs/zaina-application.log
```

**Chat message activity:**

```bash
grep "CHAT MESSAGE BROADCAST" logs/zaina-application.log
```

### Log File Locations

- **Application Logs**: `logs/zaina-application.log`
- **Archive Logs**: `logs/zaina-application.{date}.{index}.log.gz`
- **Console Output**: Real-time logs in terminal

## ⚙️ Configuration Options

### Enable/Disable Specific Log Categories

Edit `application-logging.yml`:

```yaml
logging:
  level:
    # HTTP Request logging
    com.zaina.zaina.config.RequestLoggingInterceptor: INFO # or DEBUG for more detail

    # WebSocket logging
    com.zaina.zaina.websocket: INFO # or DEBUG

    # Database logging
    org.springframework.jpa: INFO # or DEBUG

    # Security logging
    org.springframework.security: DEBUG # or INFO for less detail
```

### Custom Log Patterns

```yaml
logging:
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
```

### Log File Management

```yaml
logging:
  file:
    name: logs/zaina-application.log
    max-size: 10MB # Max file size before rotation
    max-history: 30 # Keep 30 archived files
  logback:
    rollingpolicy:
      total-size-cap: 1GB # Total log storage limit
```

## 🚀 Production Considerations

### Log Level Recommendations

**Development:**

```yaml
logging:
  level:
    com.zaina.zaina: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web.socket: DEBUG
```

**Staging:**

```yaml
logging:
  level:
    com.zaina.zaina: INFO
    org.springframework.security: INFO
    org.springframework.web.socket: INFO
```

**Production:**

```yaml
logging:
  level:
    com.zaina.zaina: WARN
    org.springframework.security: WARN
    org.springframework.web.socket: WARN
    com.zaina.zaina.websocket.WebSocketAuthChannelInterceptor: INFO # Keep security events
```

### Performance Impact

- **DEBUG level**: ~5-10% performance impact
- **INFO level**: ~1-2% performance impact
- **WARN level**: Minimal performance impact

### Security Considerations

- Authorization headers are automatically masked as `***[HIDDEN]***`
- Sensitive user data is truncated in log previews
- JWT tokens show only first 20 characters in error logs
- Personal information is limited to user IDs and usernames

## 📊 Monitoring & Alerting

### Key Metrics to Monitor

1. **Authentication Failures** - High failure rates may indicate attacks
2. **WebSocket Connection Drops** - May indicate network issues
3. **Slow Requests** - Performance degradation indicators
4. **Error Rates** - Application health monitoring

### Sample Log Analysis Scripts

**Authentication failure monitoring:**

```bash
#!/bin/bash
# Count authentication failures in last hour
grep "$(date -d '1 hour ago' '+%Y-%m-%d %H')" logs/zaina-application.log | \
grep "AUTHENTICATION FAILED" | wc -l
```

**Active users monitoring:**

```bash
#!/bin/bash
# Count unique active WebSocket sessions
grep "WEBSOCKET SESSION CONNECTED" logs/zaina-application.log | \
tail -100 | grep -o "User ID: [^[:space:]]*" | sort -u | wc -l
```

## 🔧 Troubleshooting

### Common Issues

**1. No WebSocket logs appearing:**

- Check if `logging` profile is active
- Verify WebSocket connections are being established
- Ensure log level is set to INFO or DEBUG

**2. Too many logs:**

- Reduce log levels to WARN in production
- Implement log filtering
- Adjust file rotation settings

**3. Missing HTTP request logs:**

- Verify `RequestLoggingInterceptor` is configured
- Check CORS settings for OPTIONS requests
- Ensure Spring Security is not blocking the interceptor

### Debug Commands

```bash
# Check active logging configuration
curl http://localhost:8080/actuator/loggers

# Test WebSocket connectivity
wscat -c ws://localhost:8080/ws

# Monitor logs in real-time
tail -f logs/zaina-application.log | grep "WEBSOCKET\|HTTP"
```

This comprehensive logging system provides complete visibility into your application's behavior, making debugging, monitoring, and performance analysis much easier!
