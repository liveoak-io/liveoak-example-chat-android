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

1) Configuring a GCM application in UPS.

2) Setting up and configuring an LiveOak instance.

3) Building the Native Android Application


Aerogear UPS Configuration
-------------------------------------

For the example to work you will need a running and configured AeroGear UPS server. Please note that your UPS instance will need to be running in a separate server than the one running LiveOak and will need to be network accessible to both the LiveOak server and the mobile device. Your UPS instance cannot run in the same Wildfly instance which is running LiveOak.

Please see the [Aerogear Android Push Documentation](https://aerogear.org/push/) for installation instructions.

Once you have your UPS instance up and running, you will need to perform two additional steps

1) Creating a Google Cloud Messaging Project in the [Google Developer Console](https://console.developers.google.com/). Instructions for this can be found in the UPS documentation [here](https://aerogear.org/docs/unifiedpush/aerogear-push-android/google-setup/)

2) Creating a GCM based application in UPS. Instruction for this can be found in the UPS documentation [here](https://aerogear.org/docs/unifiedpush/aerogear-push-android/register-device/)

Once those steps are completed, you will be ready to setup and configure your LiveOak instance.

LiveOak Configuration
------------------------------

In LiveOak there are are few steps which need to be taken.

### Installing the chat-html example

If you have not already installed the chat-html example you will need to do so now.

This can easily be done by going to http://localhost:8080/admin#/applications and clicking on 'Try Example Applications' and then selecting 'HTML Chat'.

Once you have imported this example you will need to go to the storage configuration page http://localhost:8080/admin#/applications/chat-html/storage/storage/browse/chat and make sure there is a storage collection named 'chat' available.

### Configuring the UPS settings for the chat example

From your UPS console you will need to open your Android application. You should then be presented with a page which displays the `Server URL`, `Application ID` and `Master Secret`.

From your chat-html push configuration page http://localhost:8080/admin#/applications/chat-html/push you will then be presented to enter the information from the UPS console. Fill out this form and click 'save'

That's it for the LiveOak configuration. Now onto building the android application.

Building the Example
--------------------

### Configuring the Example

Before you can build the example, you will need to modify a single file which includes all the specifics unique to your setup and configuration. This includes things like the URLs your application needs to access and your UPS identifiers.

The single file you need to modify is `app/src/main/assets/liveoak.json`. By default your file will look something like:

```
{
    liveoak-url: "INSERT YOUR LIVEOAK URL HERE",
    application-name: "chat-html",

    push: {
        resource-name: "push",
        ups-configuration: {
                ups-url: "INSERT THE URL TO THE UPS SERVER HERE",
                variant-id: "INSERT YOUR VARIANT ID HERE",
                variant-secret: "INSERT YOUR VARIANT SECRET HERE",
                gcm-sender-id: ["INSERT YOUR GCM SENDER ID HERE"]
        }
    }
}
```

You will need to fill out this json file with the specifics for your setup.

`liveoak-url` is the url underwhich your LiveOak instance is accessible under. This needs to start with 'http://' and remember it needs to be under a url that your mobile device can access.

Under ups-configuration:

`ups-url`, `variant-id`, `variant-secret` can be found under your Android variant in your UPS Console. Note that `gcm-sender-id` is equal to the 'Project Number' shown for your Android variant in the UPS Console.


### Building the example

This example uses gradle to build the native android application. You can build this project from the command line using the gradle build tool or importing the project into the [Android Developer Studio](http://developer.android.com/sdk/installing/studio.html).

### Building from the Command Line

From the command line, within the liveoak-example-chat-android directory, run the following:

```
$export ANDROID_HOME=/path/where/your/Android/SDK/is/installed
$gradle clean build
```

Your .apk should now be build and available in the app/build/apk directory.

### Importing and building from Android Developer Studio

From the Android Developer Studio, you should be able to import the project directly

From here please see the Android Developer Studio [documentation](http://developer.android.com/sdk/installing/studio.html) for how to build and deploy the application to a running device.

Running the Application
-------------------------------
From your Android device, run the 'LiveOak Chat' application. Also open up a browser and access the web version of the chat example. Notice how the chats from either the android application or the web application show up in the other. If the android application is not in focus you will even receive system notifications about them.

To logout of the chat application and to stop receiving notifications of new chats, just use the 'logout' button from the action bar.
