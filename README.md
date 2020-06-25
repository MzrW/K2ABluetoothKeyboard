# Keepass2Android Plugin: K2ABluetoothKeyboard

K2ABluetoothKeyboard is a plugin for the keepass2android application. It allows you to enter your passwords over bluetooth.

- No additional hardware required 
- No root required

## for developers

Useful resources:
- [https://github.com/PhilippC/keepass2android](https://github.com/PhilippC/keepass2android)
- [K2AUSBKeyboard Plugin](https://github.com/whs/K2AUSBKeyboard)

### Build instructions

``` bash
git clone ${..this..repo..}
git submodule update --init
# make sure you have installed the android sdk
./gradlew assembleDebug
```