LiveOak Native Android Chat Application
=======================================

Features
--------
* Native android integration with resources stored in a LiveOak instance

* Native notifications of resource subscription from Google Cloud Messaging (GCM) via integration with the Aerogear Unified Push Server (UPS)

* System notifications when the application is in the background

* Uses GCM for updated chats when the application has focus

Steps to Run the Application
----------------------------

There are three main parts to configuring and setting up the application:

1) Configure and setup an Aerogear UPS instance. This will also require setting up and configuring GCM.

2) Setting up and configuring an LiveOak instance. Since the native application communicates with LiveOak to

3) Building the Native Android Application


Aerogear UPS Configuration
-------------------------------------

For the example to work you will need a running and configured AeroGear UPS server.

Please see the [Aerogear Android Push Documentation](http://aerogear.org/docs/guides/aerogear-push-android/) for more detailed information.

###A high level breakdown of what you will to accomplish:

1) Install [Wild Fly](http://wildfly.org/downloads/) and deploy the [UPS war](http://dl.bintray.com/aerogear/AeroGear-UnifiedPush/org/jboss/aerogear/unifiedpush/unifiedpush-server/0.10.1/unifiedpush-server-0.10.1.war) and the [hs data source file](https://raw.github.com/aerogear/aerogear-unifiedpush-server/0.10.x/databases/unifiedpush-h2-ds.xml).

If installed correctly you should be able to access the UPS Admin console at http://myhost:myport/unifiedpush-server-0.10.1 [Default admin password is '123'].

Note: since you will be running the application on an external device you will want to bind the UPS server to location that the external device can access. If you are running both the LiveOak example and the UPS server on the same machine, you will want to have them running on separate ports. The easiest way to do this is to specify -Djboss.socket.binding.port-offset=1 when starting the UPS server. This will start the wild fly instance on port 8081 instead of 8080.

2) [Configure this instance for Google Cloud Messaging support](http://aerogear.org/docs/guides/aerogear-push-android/google-setup)
  - Visit the [Google Developer Console](https://console.developers.google.com)
  - Click 'Create Project' and give your project a name (ie 'LiveOak-Chat')
  - Wait for the project to be created
  - **Make a note of the 'Project Number' at the top of the newly created page. You will need to enter this into the UPS console.**
  - From the menu on the left, click 'APIs and Auth' and make sure that 'Google Cloud Messaging for Android' is set to 'ON'
  - Then from the menu on the left, click 'Credentials', then 'Create New Key', then click 'Android Key'. Click 'create'
  - **Make a note. This is your 'Google API Key' that you will need to enter in the UPS console.**

3) [Configure the UPS instance to register an Android variant](http://aerogear.org/docs/guides/aerogear-push-android/register-device/). Note: using the UPS console may be easier here than using the curl commands.

  - Once logged into the UPS console, click on 'Create...' to create a new UPS application
  - Give your application a name (ie LiveOak-Chat). Click on 'Create'
  - Your application should now be displayed in list of applications. Click on it.
  - Under Variants, click 'Add'
  - Give your variant an name (ie 'LiveOak-Chat Android')
  - Under the 'Google Cloud Messaging' section enter the Google API key and Project number you received earlier in the Google Developer Console.
  - Click create.
  - **Make note of the Application ID and Master Secret displayed here** You will need this when configuring the example.
  - Click on your newly created variant
  - **Make note of the Variant ID, Secret, and Project Number** You will need this when configuring the Android Chat Example.

LiveOak Configuration
------------------------------

For LiveOak you will need to deploy the chat-html hosted application and configure a storage collection to store the chats:

1) Have a MongoDB instance up and running and available at the host and ports specified in the chat-html's application.json file

2) Make sure you have a collection called 'chat' within the database specified in the application.json file. If you do not have a collection called 'chat' you can create it through the LiveOak system using the following curl command:

```
curl -X POST --header "Content-Type:application/json" http://10.42.0.1:8080/chat/storage/ --data "{ id: 'chat', capped: true, size: 102400, max:100}"
```

This will create a capped collection of size 100 kilobytes which will stores 100 entries (eg chats). Since its a capped collection, it will only store 100 chats, or 100 kilobytes of data, before new chats will overwrite the old ones. Capped collections will also always preserve the insertion order.

3) Configure the chat's application.json file to include Application ID and Master secret obtained from the UPS Console. You will need to uncomment the 'push' configuration settings here and fill in the correct data.

That's it for the LiveOak configuration.


Building the Example
--------------------

### Configuring the Example

Before you can build the example, you will need to modify the code to include the configurations for LiveOak and UPS. The application will not build without these modifications. This will require modifying the app/src/main/java/io/liveoak/android/chat/ChatApplication.java file.

UPS Configuration: from the ChatApplication.java file, add the UPS URL location (eg , the variant ID, variant secret, and the GCM sender ID from where you setup the UPS server:

```java
// UPS Settings
private static final String UPS_URL = <INSERT UPS URL HERE>; //eg "http://myhost:myport/unifiedpush-server-0.10.1";
private final String VARIANT_ID = <INSERT VARIANT ID HERE>;
private final String SECRET = <INSERT VARIANT SECRET HERE>;
private final String GCM_SENDER_ID = <INSERT GCM NUMBER HERE>;
```

LiveOak Configuration: from the ChatApplication.java file, add the host and port values for the server runing LiveOak.

```java
// LiveOak Settings
private static final String LIVEOAK_HOST = <INSERT LIVEOAK HOST HERE>; //eg hostname or ip address;
private static final int LIVEOAK_PORT = <INSERT LIVEOAK PORT HERE>; //eg 8080;
```

This example uses gradle to build the native android application. You can build this project from the command line using the gradle build tool or importing the project into the [Android Developer Studio](http://developer.android.com/sdk/installing/studio.html).

### Building from the Command Line

From the command line, within the chat-android directory, run the following:

```
$export ANDROID_HOME=/path/where/your/Android/SDK/is/installed
$gradle clean build
```

Your .apk should now be build and available in the app/build/apk directory.

### Importing and building from Android Developer Studio

From the Android Developer Studio, you should be able to open the project from liveoak-examples/chat/chat-android

From here please see the Android Developer Studio [documentation](http://developer.android.com/sdk/installing/studio.html) for how to build and deploy the application to a running device.

Running the Application
-------------------------------
From your Android device, run the 'LiveOak Chat' application. Chats from the application will appear in the hosted html chat application and vise versa. If the application is not in focus you will even receive system notifications about them.

To logout of the chat application and to stop receiving notifications of new chats, just use the 'logout' button from the action bar.
