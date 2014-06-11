# Android-Stomper
===============
Android-Stomper is an implementation for STOMP protocol for android , 
it is usually used with JMS server to , make jsm client for android application 
so that you can listen to queues an publish/subscribe model
A Library based on StompJ on googlr groups 



* It has been tested with ActiveMQ stomp and ssl+stomp 
* supports Server authentication with username and password


### How To Use?
```
Connection connection = new Connection("Server_NAME_OR_IP",
				PORT_NUMBER, USER_NAME,
				PASSWORD);
				
				
				
....
//To Check if connection is live
if(connection.isConnected()){
	//do something
} 


ErrorMessage errMsg = connection.connect(); //connectionSSL() if the connection is encrypted
if(errorMsg == null) {//connection success}

if(connection.isConnected()){
	Dictionary<String, String> headers = new Hashtable<String, String>();
					
					headers.put("header1", "value2");
					headers.put("header2", "value2");
					//USe this header to transform json to a java object
					//because we must send a json representaion in STOP protocol
					headers.put("transformation", "jms-map-json");
				
					mConnection.subscribe(queue, headers, true);
}
```


### Event Handling

<pre>
connection.addMessageHandler(queue, new MessageHandler() {

						@Override
						public void onMessage(Message msg) {
							String response = msg.getContentAsString();
						}
					});
</pre>

  
  
  
### Map Message Format: 
  We must send a json encded message with following format
  <pre>
  {
   "map":{
      "entry":[
         {
            "string":[
               "param1",
               "value"
            ]
         },
         {
            "string":[
               "param2",
               "value2"
            ]
         }
      ]
   }
}

</pre>

### Sending message to stomp server 

<pre>
//t set headers property
message.setProperty("header1", value);
message.setContent(messageContent.getBytes());
mConnection.send(message, "/queue/name/on/jsm_stomp_enabled_server");
</pre> 



## Contact me
If you have any question , don't hasitate to open an issue or write in the wiki.
visit my website www.muhannadalhariri.org
