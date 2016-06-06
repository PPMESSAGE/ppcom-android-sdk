PPMessage Android Library
======

This library included `API`, `Notification`, `UI` and some other useful tools for the basic usage of [PPMessage](https://ppmessage.com)

[ ![Download](https://api.bintray.com/packages/ppmessage/maven/ppmessagesdk/images/download.svg) ](https://bintray.com/ppmessage/maven/ppmessagesdk/_latestVersion)

Download
======

You can download through `Maven` or `Gradle`.

## Maven

```xml
<dependency>
  <groupId>com.ppmessage</groupId>
  <artifactId>sdk</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```

## Gradle

```groovy
compile 'com.ppmessage:sdk:0.0.2'
```

Usage
======

## Initialize

```java
PPMessageSDK sdk = PPMessageSDK.getInstance();
sdk.init(new PPMessageSDKConfiguration
	.Builder(getContext())
	// fir PPCom use
	.setAppUUID("YOUR_APP_UUID")
	// for PPKefu use
	.setServiceUserInfo("SERVICE_USER_EMAIL", "SERVICE_USER_SHA1_PASSWORD")
	// Enable Log.d(XXX) info, default is false
	.setEnableDebugLogging(true)
	// Enable Log.w(xxx) and Log.e(XXX) info, default is true
	.setEnableLogging(true)
	.build());
```
                
## API

Here is a sample usage of api to demonstrate how to get app info, check `com.ppmessage.sdk.core.api.IAPI.class` to see what apis you can called now.

```java
JSONObject requestParam = new JSONObject();
requestParams.put("app_uuid", "MY_APP_UUID");
	
sdk.getAPI().getAppInfo(requestParam, new OnAPIRequestCompleted() {
	@Override
	public void onResponse(JSONObject jsonResponse) {}
	
	@Override
	public void onCancelled() {}
	
	@Override
	public void onError(int errorCode) {}
});
```
	
## Notification

Notification was designed for send message and receive message by WebSocket. 

Before usage, you must config it, the server uses these information to authenticate the client, and `Notification` uses these information to recognize current active user, so when a new message arrived, it knows how to parse this message correctly.

### Token

you should provide token for `Notification` authenticate.
	
```java
// for PPCom use
sdk.getToken().getApiToken("YOUR_APP_UUID", new IToken.OnRequestTokenEvent() {
	@Override
	public void onGetToken(String accessToken) {}
});

// for PPKefu use
sdk.getToken().getApiToken("SERVICE_USER_EMAIL", "SERVICE_USER_SHA1_PASSWORD", new IToken.OnRequestTokenEvent() {
	@Override
	public void onGetToken(String accessToken) {}
});
```

### Config

```java
// for PPCom, you should provide current PPCom user
// for PPKefu, you should provide current logined PPKefu user
final User activeUser = getActiveUser();

sdk.getNotification().config(new INotification.Config() {
	@Override
	public String getAppUUID() { return "YOUR_APP_UUID"; }

	@Override
	public User getActiveUser() { return activeUser; }

	@Override
	public String getApiToken() { return "API_TOKEN" }
});
```
	
### Start
	
```java
sdk.getNotification().start();
```
		
### On notification arrived

The server may send many different types info, but you may only interested in a few ones. So, you can only listen the interested infos, and you will get notified when the message which you watched arrived, other types message won't interrupt you.
	
```java
INotification.OnNotificationEvent notificationEvent = new INotification.SimpleNotificationEvent() {
	@Override
	public int getInterestedEvent() {
		return INotification.EVENT_MESSAGE |
			INotification.EVENT_MSG_SEND_ERROR;
	}
	
	@Override
	public void onMessageInfoArrived(PPMessage message) {
		// A new message arrived
	}
	
	@Override
	public void onMessageSendError(WSMessageAckNotificationHandler.MessageSendResult messageSendResult) {
		// Oops, message send error ...
	}
};

sdk.getNotification().addListener(notificationEvent);
```
	
### Send message

```java
PPMessage messageToBeSend = getToBeSendMessage();
sdk.getNotification().sendMessage(messageToBeSend);
```
	
## UI

### MessageActivity

`MessageActivity` provided basic ui for chatting activity.

```java
public class MyMessageActivity extends MessageActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<PPMessage> messages = new ArrayList<>();
		MessageAdapter messageAdapter = new MessageAdapter(sdk, this, messages);
		setAdapter(messageAdapter);
		
	}
	
}
```
	
### ConversationFragment

`ConversationFragment` provided basic ui for conversation list view.

```java
public class MyConversationsActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<Conversation> conversations = new ArrayList<>();
		
		ConversationFragment fragment = new ConversationFragment();
		fragment.setMessageSDK(sdk);
		fragment.setConversationList(conversations);
	
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
	        .replace(android.R.id.content, fragment)
        	.commit();
	
	}
	
}
```
	
## Bean

### PPMessage

`PPMessage` represent for message entity.

#### Async parse message

```java
PPMessage.asyncParse(sdk, messageJSONObject, new onParseListener() {
	@Override
	public void onCompleted(PPMessage message) {
	}
});
```

#### Build message for send

Here is a simple usage for how to send text message.

```java
PPMessage message = new PPMessage.Builder()
	.setConversation(conversation)
	.setFromUser(fromUser)
	.setMessageBody("Hello, PPMessage")
	.build();
sdk.getNotification().sendMessage(message);
```
	
#### Get message summary

```java
String summary = PPMessage.summary(getContext(), message);
```

## Model

### MessagesModel

`MessagesModel` store and get messages.

- get messages by conversation uuid
- get history load to load history
- add message to tail
- add messages to head
- find message

### MessageHistoryLoader

`MessageHistoryLoader` for help you load message historys.

```java
MessageHistorysModel.MessageHistoryRequestParam requestParam = new MessageHistorysModel.MessageHistoryRequestParam(conversationUUID, maxUUID, 0);

MessageHistoryLoader historyLoader = new MessageHistoryLoader(sdk);
historyLoader.loadHistory(requestParam, new MessageHistorysModel.OnLoadHistoryEvent() {
	@Override
	public void onCompleted(HistoryPageIndex pageIndex, List<PPMessage> messageList) {
	}
});
```
	
### UnackedMessagesLoader

`UnackedMessagesLoader` will try get all unacked messages, and then convert them to WebSocket message style. 

```java
UnackedMessagesLoader unackedMessagesLoader = new UnackedMessagesLoader(messageSDK);
unackedMessagesLoader.loadUnackedMessages();
```
	
## Utils

### TxtLoader

`TxtLoader` for load large txt

```java
TxtLoader txtLoader = new TxtLoader();
txtLoader.loadTxt("b2cced63-06e0-11e6-b73b-acbc327f19e9", new TxtLoader.OnTxtLoadEvent() {
	@Override
	public void onCompleted(String text) {
	}
});
```
	
### TxtUploader

`TxtUploader` for upload large txt to server

```java
TxtUploader txtUploader = new TxtUploader();
txtUploader.upload("LARGE TEXT ...", new OnUploadingListener() {
	@Override
	void onError(Exception e) {}
	
	@Override
	void onCompleted(JSONObject response);
});
```
	
Note
=======

This library not tested for PPKefu now :)

