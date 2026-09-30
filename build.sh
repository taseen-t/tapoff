#!/bin/sh
# Builds TapOff.apk without Gradle: aapt2 -> javac -> d8 -> zipalign -> apksigner.
# Needs JDK 17 and the Android SDK with "platforms;android-35" and "build-tools;35.0.0".
# Set JAVA_HOME / ANDROID_HOME, or on a Mac with Homebrew it finds openjdk@17 and android-commandlinetools.
# Pass --install to push it to a phone connected with USB debugging and grant the one-time permission.
set -e
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME:-$(brew --prefix openjdk@17)}"
export PATH="$JAVA_HOME/bin:$PATH"
SDK="${ANDROID_HOME:-/opt/homebrew/share/android-commandlinetools}"
BT=$SDK/build-tools/35.0.0
JAR=$SDK/platforms/android-35/android.jar
# The keystore stays local (see .gitignore). Updates must be signed with the same key as the installed app.
KS="${TAPOFF_KEYSTORE:-debug.keystore}"
KS_PASS="${TAPOFF_KEYSTORE_PASS:-android}"

rm -rf out && mkdir -p out/classes
"$BT/aapt2" compile --dir res -o out/res.zip
"$BT/aapt2" link out/res.zip -I "$JAR" --manifest AndroidManifest.xml \
  --min-sdk-version 33 --target-sdk-version 35 --version-code 10 --version-name 1.7.1 \
  --java out/gen -o out/unsigned.apk
javac -source 17 -target 17 -cp "$JAR" -d out/classes $(find src out/gen -name '*.java')
"$BT/d8" --min-api 33 --lib "$JAR" --output out $(find out/classes -name '*.class')
(cd out && zip -q unsigned.apk classes.dex)
"$BT/zipalign" -f 4 out/unsigned.apk out/aligned.apk

[ -f "$KS" ] || keytool -genkeypair -keystore "$KS" -storepass "$KS_PASS" -keypass "$KS_PASS" \
  -alias tapoff -keyalg RSA -validity 10000 -dname "CN=TapOff" >/dev/null 2>&1
"$BT/apksigner" sign --ks "$KS" --ks-pass "pass:$KS_PASS" --out TapOff.apk out/aligned.apk
echo "built TapOff.apk"

if [ "$1" = "--install" ]; then
  adb install -r TapOff.apk
  adb shell pm grant com.taseen.tapoff android.permission.WRITE_SECURE_SETTINGS
fi
exit 0
