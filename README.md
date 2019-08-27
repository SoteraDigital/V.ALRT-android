# V.ALRT Android

Sanitized mirror from app used on the App Store.

# Build instructions

1. Download a file called "fabric.properties" from the Fabric console and place it on path "Vsnmobilstudio/app/fabric.properties"
2. Download a file called "google-services.json" from the Google console and place it on path "Vsnmobilstudio/app/google-services.json"
3. On "Vsnmobilstudio/app/src/main/AndroidManifest.xml", replace the metadata "io.fabric.ApiKey" with your own value.
4. On file "Vsnmobilstudio/app/src/main/java/com/vsnmobil/valrt/VALRTApplication.java", replace "VOIP_SMS_WEBSERVICE_URL" and "VOIP_CALL_WEBSERVICE_URL" with your own server names.
