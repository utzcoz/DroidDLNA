# cling-test-instrument

This project is forked from [DroidDLNA](https://github.com/offbye/DroidDLNA), and removes almost
all `Activity` logic and control logic. I just keep the basic renderer logic as test code, and
provide a lib called `cling-test-instrument` as test lib. I use it to test the basic logic
of renderer based on `cling`. It will help to test renderer app based on `cling`.

## Download

```shell
git clone --recursive https://github.com/utzcoz/cling-test-instrument.git
```

## Run tests

```shell
./run-test.sh
```

And it will run all tests to connected device, and show the summary result of tests.