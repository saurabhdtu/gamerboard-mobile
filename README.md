# Gamerboard-LIVE

Flutter project for Android and iOS app.

## Steps to set up

1. Install android studio
2. Install dart and flutter plugin
3. Download flutter 
4. Configure flutter and dart path to flutter and dart sdk in Android studio

Voila.....

## Updates release process on `Notion`
https://www.notion.so/gamerboard/Regression-Testing-Checklist-34cf217a48084733b02193258ee215e1

## Setup wrangler (CLI for Cloudflare)
1. Request access for cloud flare R2 dashboard from akshat@gamerboard.live
2. After getting access setup wrangler on local machine. Follow https://developers.cloudflare.com/workers/wrangler/install-and-update/
3. After wrangler is setup, run `nvm use v16.13.0`
4. run `export NODE_TLS_REJECT_UNAUTHORIZED='0'` if you face any ssl certificate issue

## Steps to release
### For website download
1. Create signed release apk using `prod-build` branch
2. Upload the apk as `gamerboard-release-prod.apk` on Cloud flare. For uploading run:

 `wrangler r2 object put android-apk/gamerboard-release-prod.apk --file=<PATH TO APK>/gamerboard-release-prod.apk  --content-type=application/vnd.android.package-archive`
 
3. You might be asked to login. Follow the login process on browser and after login wrangler will be validated on the terminal. Use akshat@gamerboard.live as the account.
4. Command at 3 uploads the apk for the website
### For in app update
5. Upload the `gamerboard-release-vXX.apk` using following command 

`wrangler r2 object put android-apk/apk-versions/gamerboard-release-prod-v109.apk --file=<PATH TO APK>/gamerboard-release-prod-v109.apk   --content-type=application/vnd.android.package-archive`

## Add the FFMpeg library
Add https://drive.google.com/file/d/1tiHtIWJSkXVy2YmUIMNjt5FCaYo0yrqn/view?usp=sharing TO `android/app/libs`

## Setting up ffmpeg tool
1. Download ffmpeg from https://github.com/tanersener/ffmpeg-kit
2. Install android NDK and cmake tool in android sdk 
3. pwd to mobile-ffmpeg
4. `brew install pkg-config`
5. `brew install autoconf`
6. `brew install automake`
7. `brew install libtool`
8. `brew install curl`
9. `brew install cmake`
10. `brew install gcc`
11. `brew install gperf`
12. `brew install texinfo`
13. `brew install yasm`
14. `brew install bison`
15. `brew install autogen`
16. `brew install git`
17. `brew install wget`
18. `brew install autopoint`
19. `brew install meson`
20. `brew install ninja`
21. run `export ANDROID_SDK_ROOT=<Android SDK Path>`
   
   `export ANDROID_NDK_ROOT=<Android NDK Path>`
   
   `./android.sh`
    
      if ./android.sh throws permission denied
      
      ```
      chown root ./android.sh
      chmod 700 ./android.sh
      ```
22. Run `./android.sh --enable-gpl --enable-x264`	
 
