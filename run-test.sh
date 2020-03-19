# bin/bash
# See the page https://developer.android.com/studio/test/command-line
# for more information
# Before run shell script, you should ensure there is a connected device
# Run intrumentation unit test
./gradlew connectedAndroidTest
# Show the test summary result
python3 junit_result_parser.py -d app/build/outputs/androidTest-results/connected/
