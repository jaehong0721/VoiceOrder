# Begin: Proguard rules for Firebase

#database
-keepattributes Signature
-keepclassmembers class com.rena21c.voiceorder.model.** {*;}

#auth
-keepattributes *Annotation*

#crash

# End: Proguard rules for Firebase