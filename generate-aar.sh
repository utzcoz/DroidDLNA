rm -rf release
./gradlew :cling-test-instrument:build
mkdir -p release
cp cling-test-instrument/build/outputs/aar/cling-test-instrument-release.aar release/
