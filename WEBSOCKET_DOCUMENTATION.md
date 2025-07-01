# WebSocket Real-time Messaging System

## Overview

The Zaina application now includes a comprehensive real-time messaging system built with WebSocket technology. This system provides:

- **Real-time chat messaging** between connected users
- **Typing indicators** to show when users are typing
- **Online status tracking** to see who's currently active
- **Message read receipts** to confirm when messages are read
- **Connection notifications** for real-time connection requests and acceptances
- **JWT-based authentication** for secure WebSocket connections

## Architecture

### Backend Components

1. **WebSocketConfig** - Configuration for STOMP messaging over WebSocket
2. **WebSocketAuthChannelInterceptor** - JWT authentication for WebSocket connections
3. **ChatWebSocketController** - Handles WebSocket message routing
4. **WebSocketService** - Business logic for real-time messaging
5. **WebSocketEventListener** - Handles connection/disconnection events
6. **WebSocketController** - REST endpoints for WebSocket-related data

### Frontend Integration

The system uses STOMP.js over SockJS for reliable WebSocket connections with fallback support.

## WebSocket Endpoints

### Connection Endpoint

```
/ws - Main WebSocket endpoint with SockJS fallback
```

### Subscription Channels

#### Personal Queues (user-specific)

- `/user/queue/messages` - Receive personal chat messages
- `/user/queue/typing` - Receive typing indicators for active conversations
- `/user/queue/read-receipts` - Receive read receipts for sent messages
- `/user/queue/connections` - Receive connection request notifications

#### Topic Channels (broadcast)

- `/topic/user-status` - Receive user online/offline status updates

### Application Destinations (send messages to)

- `/app/chat.send` - Send a chat message
- `/app/chat.typing` - Send typing indicator
- `/app/chat.markRead` - Mark a message as read
- `/app/user.status` - Update user online status

## Message Types

### ChatMessage

```kotlin
data class ChatMessage(
    val id: UUID,
    val senderId: UUID,
    val receiverId: UUID,
    val content: String,
    val sentAt: LocalDateTime,
    val senderName: String?
)
```

### TypingIndicator

```kotlin
data class TypingIndicator(
    val senderId: UUID,
    val receiverId: UUID,
    val typing: Boolean,
    val senderName: String?
)
```

### UserStatus

```kotlin
data class UserStatus(
    val userId: UUID,
    val online: Boolean,
    val lastSeen: LocalDateTime?
)
```

### MessageReadReceipt

```kotlin
data class MessageReadReceipt(
    val messageId: UUID,
    val readBy: UUID,
    val readAt: LocalDateTime
)
```

## Frontend Implementation

### 1. Establishing Connection

```javascript
const socket = new SockJS("http://localhost:8080/ws");
const stompClient = Stomp.over(socket);

const headers = {
  Authorization: "Bearer " + authToken,
};

stompClient.connect(headers, function (frame) {
  console.log("Connected: " + frame);
  // Set up subscriptions
});
```

### 2. Subscribing to Messages

```javascript
// Personal messages
stompClient.subscribe("/user/queue/messages", function (message) {
  const wsMessage = JSON.parse(message.body);
  if (wsMessage.type === "CHAT_MESSAGE") {
    displayMessage(wsMessage.data);
  }
});

// Typing indicators
stompClient.subscribe("/user/queue/typing", function (message) {
  const wsMessage = JSON.parse(message.body);
  if (wsMessage.type === "TYPING_INDICATOR") {
    showTypingIndicator(wsMessage.data);
  }
});
```

### 3. Sending Messages

```javascript
// Send chat message
const message = {
  receiverId: targetUserId,
  content: messageContent,
};
stompClient.send("/app/chat.send", {}, JSON.stringify(message));

// Send typing indicator
const typingIndicator = {
  receiverId: targetUserId,
  typing: true,
};
stompClient.send("/app/chat.typing", {}, JSON.stringify(typingIndicator));
```

## REST API Endpoints

### Get Online Users

```http
GET /api/websocket/online-users
Authorization: Bearer {token}
```

### Get User Status

```http
GET /api/websocket/user-status/{userId}
Authorization: Bearer {token}
```

## Security

- **JWT Authentication**: All WebSocket connections require valid JWT tokens
- **User Authorization**: Users can only access their own message queues
- **Connection Validation**: Server validates user permissions before establishing connections

## Integration with Existing Features

### Message Service Integration

- When messages are sent via REST API, they're automatically broadcast via WebSocket
- Read receipts are sent in real-time when messages are marked as read

### Connection Service Integration

- Connection requests and acceptances are broadcast in real-time
- Users receive immediate notifications about connection status changes

## Testing the System

### Prerequisites

1. Start the Spring Boot application
2. Ensure you have valid user accounts in the system
3. Open the provided `frontend-websocket-example.html` in a browser

### Testing Flow

1. **Login**: Use valid credentials to get JWT token
2. **Connect**: WebSocket connection is established automatically
3. **Chat**: Select a user and send messages in real-time
4. **Typing**: Type messages to see typing indicators
5. **Status**: Observe online/offline status changes
6. **Connections**: Test connection requests and responses

## Error Handling

### Connection Errors

- Failed authentication results in connection rejection
- Network issues trigger automatic reconnection attempts
- Malformed messages are logged and ignored

### Message Validation

- Empty messages are rejected client-side
- Invalid user IDs result in error responses
- Unauthorized access attempts are logged

## Performance Considerations

### Connection Management

- Online users are tracked in memory for fast access
- Connection cleanup happens automatically on disconnect
- Typing indicators have automatic timeouts to prevent spam

### Message Broadcasting

- Messages are sent only to intended recipients
- User status updates are broadcast efficiently
- Connection pools are managed by Spring WebSocket

## Deployment Notes

### Environment Configuration

- Ensure CORS is properly configured for your frontend domain
- Set appropriate WebSocket buffer sizes for your expected load
- Configure SSL/TLS for production WebSocket connections

### Monitoring

- WebSocket connections are logged for debugging
- User activity is tracked for online status
- Error events are logged with appropriate detail levels

## Example Usage

The `frontend-websocket-example.html` file provides a complete working example of:

- User authentication
- WebSocket connection establishment
- Real-time messaging
- Typing indicators
- Online status display
- Connection request handling

This example can be used as a reference for building your own frontend implementation.

## Future Enhancements

Potential improvements for the system:

- Message history pagination in real-time
- Group chat functionality
- File sharing capabilities
- Voice/video call initiation
- Push notifications for offline users
- Message encryption for enhanced security
