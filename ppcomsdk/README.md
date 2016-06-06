PPCom Android SDK
=======

Android SDK for PPCom 

For more information please see [the website](https://ppmessage.com)

[ ![Download](https://api.bintray.com/packages/ppmessage/maven/ppcomsdk/images/download.svg) ](https://bintray.com/ppmessage/maven/ppcomsdk/_latestVersion)

Download
======

You can download through `Maven` or `Gradle`.

## Maven

```xml
<dependency>
  <groupId>com.ppmessage</groupId>
  <artifactId>ppcomsdk</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```

## Gradle

```groovy
compile 'com.ppmessage:ppcomsdk:0.0.2'
```
	
Usage
======

## Initialize

```java
public class App extends Application {

	private static final String PPMESSAGE_APP_UUID = "xxx";

	@Override
	public void onCreate() {
		super.onCreate();

		PPComSDK sdk = PPComSDK.getInstance();
		sdk.init(new PPComSDKConfiguration
	        .Builder(this)
        	.setAppUUID(PPMESSAGE_APP_UUID)
	        .build());

	}

}
```

## Startup

```java
public class MainActivity extends ConversationsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	startUp();
	}

}
```
