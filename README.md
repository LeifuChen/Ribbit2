# Ribbit2 - a Self-Destructing Message Android App
Refactoring a retired treehouse course project with Android API 24 &amp; [back{4}app]
### Features:
- capture **video and photo**
- select the users whom we want to send the picture or video to. 
- store the **friends** and video/photos
- send message to the **cloud** to wait for our friends to view them
- access to messages into an **inbox**
- notify the user if there is a new message

### Design:
- two tabs, inbox and friends
- camera button in the tool bar that loads the camera from anywhere
- a screen to allow users to register into app
- [mockup]

### Change:
- replace action bar with toolbar 
- replace parseAnalytics.trackApp with parseAnalytics trackAppOpenedInBackground
- redesign toolbar layout
- redesign tab layout
- redesign message view layout
- replace parse push notification with parse server cloud code 

[back{4}app]:<https://www.back4app.com/>
[mockup]:<http://treehouse-code-samples.s3.amazonaws.com/Android/ribbit-android-overview.png>