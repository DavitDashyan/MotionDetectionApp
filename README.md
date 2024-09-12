# Motion Detection Android Application

## Overview

This is a simple Android application that detects motion using the device's camera. The application analyzes camera frames every 0.3 seconds and compares them to detect movement. If movement is detected, a message is displayed on the screen and spoken using Text-to-Speech. When no movement is detected, the message disappears.

## Features

- Uses the camera to capture frames every 0.3 seconds.
- Compares frames to detect motion based on the number of digits in the movement value.
- Displays a text message when movement is detected.
- Uses Text-to-Speech to announce the message.
- Hides the message when no movement is detected.

## Prerequisites

- Android Studio
- Android 6.0 (Marshmallow) or later
- Camera permission must be granted

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/DavitDashyan/MotionDetectionApp.git
    ```
2. Open the project in Android Studio.
3. Build and run the application on an Android device or emulator.

## Usage

1. **Grant Camera Permission**: Make sure the application has permission to use the camera. You will be prompted to grant this permission when you run the app.
2. **Start the App**: Once the app is running, it will start capturing frames from the camera.
3. **Motion Detection**: The app will compare frames every 0.3 seconds. If movement is detected (indicated by an 8-digit number), a text message will be displayed and spoken.
4. **Message Visibility**: The text message will disappear when no movement is detected (indicated by a 7-digit number).

## Code Explanation

- **Camera Preview**: The app initializes a camera preview using `SurfaceView`.
- **Motion Detection**: It compares frames to calculate motion using byte data from the camera.
- **Text-to-Speech**: When motion is detected, the app uses `TextToSpeech` to announce the message.
- **Logs**: Detailed logs are available to monitor the frame comparison and motion detection process.

## Troubleshooting

- **No Movement Detection**: Ensure you are moving in front of the camera to trigger movement detection.
- **Permission Issues**: Make sure the camera permission is granted.

## Contributing

Feel free to contribute to this project by creating issues or pull requests. Ensure that your changes are well-documented.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or feedback, please contact Davit Dashyan at [your-email@example.com](mailto:your-email@example.com).

