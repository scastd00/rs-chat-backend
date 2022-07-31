# WebSocket message format

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
		"encoding": "regex(UTF-8|base64)",
		"content": "<encodedContentOfMessage>"
	}
}
```
