# Hotel Management System Documentation

## Project Overview

The **Hotel Management System** is a mobile application designed to manage hotel bookings, track customer information, and provide real-time updates. The app is built using **Android** and integrates with **Firebase Realtime Database** for storing user data, bookings, and hotel information. The system supports features such as user authentication, booking management, profile editing, and payment tracking.

## Technologies Used

1. **Programming Language**:
   - **Java**: The application is developed using Java, which is the primary programming language for Android development.

2. **Android Framework**:
   - **Android SDK**: The Android Software Development Kit (SDK) is used to build the mobile application. The app follows the standard Android architecture patterns and UI guidelines.

3. **User Interface**:
   - **XML**: For designing UI components, XML is used to define layouts and views, providing a responsive and user-friendly interface.
   - **Jetpack Libraries**: Android's Jetpack libraries are used for managing UI and handling UI interactions efficiently.

4. **Backend**:
   - **Firebase Realtime Database**: Firebase is utilized for storing and syncing data in real-time. This includes user profiles, booking information, and hotel-related data. The system offers easy integration and efficient data synchronization across devices.
   - **Firebase Authentication**: Used for user sign-in and sign-up, providing secure authentication mechanisms for users.

5. **Image Uploading**:
   - **Cloudinary**: Cloudinary is used for managing and uploading user avatars and images in the app.

6. **Libraries & Tools**:
   - **MPAndroidChart**: For generating various types of charts (e.g., bar charts, line charts) to display statistics such as revenue, booking trends, etc.
   - **Glide**: For image loading and caching, especially for displaying avatars and hotel images.
   - **Retrofit**: Used for making network calls to fetch or send data to the server, simplifying REST API integration.

7. **Other Tools**:
   - **Java AsyncTask**: For handling background tasks such as image upload without blocking the main UI thread.
   - **SharedPreferences**: For local data storage, such as saving user preferences, login status, and user-specific settings.
   - **Material Design Components**: To implement modern UI elements and provide a seamless user experience.

## Features

### 1. **User Authentication**:
   - Users can sign up, log in, and manage their profiles.
   - Firebase Authentication is used to manage user sign-in securely.

### 2. **Profile Management**:
   - Users can view and update their profile details, including username, email, and avatar.
   - Image uploads for user avatars are handled by Cloudinary.

### 3. **Booking Management**:
   - Users can book rooms, view past bookings, and see detailed booking information.
   - Booking data includes hotel name, room name, dates, total price, and payment status.
   - Bookings are stored in Firebase Realtime Database and updated in real-time.

### 4. **Revenue and Booking Statistics**:
   - Displays booking and revenue statistics using **MPAndroidChart** for visual representation (e.g., bar charts, line graphs).
   - Users can view statistics over different periods (monthly, yearly) for better insight into hotel performance.

### 5. **Hotel Management**:
   - Admins can manage hotel rooms, view available rooms, and update room information.
   - The system tracks room availability, rates, and occupancy.

## Project Structure

- **MainActivity.java**: The entry point of the application, which includes the login flow and navigation to different sections of the app.
- **Fragments**: Used for displaying different sections like user profile, booking history, and hotel information.
- **Adapters**: Handle binding data to views, such as displaying a list of bookings or user details.
- **Firebase Database**: All app data (users, bookings, rooms) are stored and managed via Firebase Realtime Database.
- **Cloudinary Integration**: For managing image uploads and serving them from the cloud.
