# Ride-Sharing Platform(TripSync)

## Overview

This project is a Ride-Sharing Platform developed using Kotlin for Android and Firebase for backend services including authentication and database. The app supports three distinct user roles: Traveler, Admin, and Traveler Companion, each with specific functionalities.

## Features

### Traveler
- **Share Ride Details**: Share ride details such as TripId, Driver Name, Driver Phone Number, and cab number through  SMS. The shared link expires after the trip is complete.
- **Audit Trail**: View an audit trail of the rides shared.

### Traveler Companion
- **Track Ride**: Track the ride of the traveler in real-time.
- **Trip Completion Notification**: Receive notifications when the trip is complete.
- **Geofence Notification**: Get notifications when the cab enters the geofence of the traveler’s drop location.
- **Feedback Sharing**: Share feedback about the experience with the Admin.

### Admin
- **View Rides**: Access and view all rides shared by users.
- **Experience Feedback**: Access overall user feedback.

## Technology Stack

- **Frontend**: Kotlin, XML
- **Backend**: Firebase (Authentication, Realtime Database)

## Setup Instructions

### Prerequisites

- Android Studio
- Java Development Kit (JDK)
- Firebase Project Setup


## Cloning the Repository
 ```bash
    git clone https://github.com/the-punisher-29/TripSync.git
    cd TripSync
 ```


## Configuring Firebase

### Create a Firebase Project
- Go to the Firebase Console.
- Create a new project if you haven’t already.

### Add the App to Firebase

- In the Firebase Console, select your project.
- Click on the “Add app” button.
- Choose the Android platform and follow the instructions to register the app.
- Download the google-services.json file and place it in the app/ directory of the Android project.

### Add Firebase Dependencies
- In the project-level build.gradle file, add the Google services classpath in the dependencies section:

 ```bash
gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.3.15'
    }
}
 ```

In the app-level build.gradle file, apply the Google services plugin and add the Firebase dependencies:

```bash
gradle
apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'

dependencies {
    implementation 'com.google.firebase:firebase-auth:22.1.0'
    implementation 'com.google.firebase:firebase-database:20.1.0'
    implementation 'com.google.android.gms:play-services-auth:20.6.0'
}
 ```

### Running the Project
- Open the project in Android Studio.
- Click “Sync Now” to sync Gradle files.
- Connect an Android device or start an emulator and run the app.

### Authentication Setup
- Ensure you have configured Firebase Authentication in the Firebase Console. Enable Google Sign-In and any other authentication providers you plan to use.

### Database Setup
- Configure Firebase Realtime Database rules and structure according to the project needs.

## Contributing
- If you would like to contribute to this project, please fork the repository, create a new branch, and submit a pull request with your changes.
