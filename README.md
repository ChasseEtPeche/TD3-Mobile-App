The user has to provide an ID which ensures that it's the right user, the id is like a password in this application.

To securely save the user's information we use the internal storage so the data are
deleted when the user deletes the app and only this app can access to them.

To hide the API url i tried to obfuscate the code by putting minifyEnable true in the build.gradle
but i'm not sure it worked successfuly.

The screenshots are in the folder. 