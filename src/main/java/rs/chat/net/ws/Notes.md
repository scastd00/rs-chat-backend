# WebSocket message format

The following example shows how the messages are exchanged between the client and the server.

```json
{
  "headers": {
    "username": "<username>",
    "chatId": "<chatId>",
    "sessionId": "<sessionId>",
    "type": "<typeConstant>",
    "date": "<currentDate>",
    "token": "Bearer <accessToken>"
  },
  "body": {
    "content": "<contentOfMessage>"
  }
}
```
