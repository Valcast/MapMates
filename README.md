# <p align="center">MapMates</p>
:dancers: **MapMates** is a social meeting app that helps you discover new events, meet people, and join exciting activities nearby.
You can create your own events, invite others, or browse through a variety of events happening around you. 

## Features
- :key: **User Authentication**: Firebase Authentication with Credential Manager for seamless experience and Google login integration.
- :mag_right: **Nearby Events**: Display nearby events
- 🗺️ **Map Integration**: Google Maps API for rendering event locations and Geocoding API for retrieving formatted addresses.
- :tada: **Event Creation**: Allow users to create and manage events, specifying details like date, time, and location.
- :man: **User Profile**: Users can create and edit their profile, including personal details
## Tech Stack

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-ffffff?style=for-the-badge&logo=jetpackcompose)
![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)
![GoogleMap](https://img.shields.io/badge/Google_Map_Api-3d89fc?style=for-the-badge&logo=googlemaps&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android_Studio-ffffff?style=for-the-badge&logo=androidstudio)

**Frontend**: Kotlin, Jetpack Compose, Android SDK, Coil

**Backend**: Firebase (Authentication and Firestore), Google Maps API, Retrofit

**Testing**: JUnit, Mockk

The app follows Clean Architecture and MVVM with a separation between the UI layer, domain layer, and data layer.
Uses Dagger Hilt for Dependency Injection

## Future Improvements
- 🤝 **Implement social features**: Friend list, event sharing and inviting
- 🗪 **Add in-app messaging**: Users could chat with other participants
- 🔔 **Create notification system**: Event reminders, updates
