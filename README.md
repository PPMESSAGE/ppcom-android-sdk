## PPComDemo 

A demo use PPComSDK via gradle.

To use local source project instead of bintray gradle. Use settings.gradle.local to ooverwrite settings.gradle and use gradle.build.local to overwrite gradle.

And do the same for PPComSDK!

> A config-build.sh is intented to help build from local or jcenter.

- build demo reference local source

```
sh config-build.sh local
```

- build demo reference jcenter

```
sh config-build.sh jcenter
```

> The script will link settings.gradle and build.gradle for you.

## PPComSDK

PPComSDK which depends on PPMessageSDK.

### Two steps to use PPComSDK

- Create and initialize PPComSDK by using basic information.

```java
public class MyApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        PPComSDK sdk = PPComSDK.getInstance();

        // Global config PPComSDK
        PPComSDKConfiguration.Builder builder = new PPComSDKConfiguration.Builder();
        sdk.init(builder.setContext(this)
                 .setAppUUID("YOUR_APP_UUID")
                 .setApiKey("PPCOM_API_KEY")
                 .setApiSecret("PPCOM_API_SECRET")
                 .setServerUrl("YOUR_SERVER_URL")

                 .setUserFullName("YOUR_USER_FULL_NAME")
                 .setUserEmail("YOUR_USER_EMAIL")
                 .setEntUserUUID("YOUR_ENT_USER_UUID")
                 .setEntUserType("YOUR_ENT_USER_TYPE")

                 .setJpushRegistrationId("JPUSH_REGISTRATION_ID");
                 
                 .setInputHint("Any questions") // default is ""
                 .setActionbarBackgroundColor(getResources().getColor(
                                                                      android.R.color.holo_blue_dark))
                 .setActionbarTitleColor(Color.WHITE)
                 .setEnableLog(true) // default is false
                 .setEnableEnterKeyToSendText(true) // default is false
                 .build());

    }
}
```

> If enterprise user uuid is set, the user is identified by this uuid; if user email is set, the user is identified by email. If you want to set the user type, must set here, not update() of PPComSDK.

- Extend `ConversationsActivity` to start conversation

> `sdk.update()` is optional and only `user full name`, `user icon url` and `ent user data` could be update.
If you want to clear data, set it to an empty string.

`sdk.update()` must called after sdk.init() success, otherwise `RuntimeException` will be throwed.

```java
public class MainActivity extends ConversationsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PPComSDK sdk = PPComSDK.getInstance();

        // Optional !
        sdk.update(new PPComSDKConfiguration.Builder()
        
                   .setUserFullName("YOUR_USER_FULL_NAME")
                   .setUserIcon("YOUR_USER_ICON_URL")
                   .setEntUserData("YOUR_ENT_USER_DATA")
                   .setJpushRegistrationId("YOUR_JPUSH_REGISTRATION_ID")
                 
                   .build());

        startUp();
    }

}

```

## PPMessageSDK

`PPMessageSDK` is core lib and distributed with gradle.
