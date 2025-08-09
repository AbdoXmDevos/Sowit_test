# SowitTechTest - Interactive Map Area Manager

A modern Android application for creating, managing, and visualizing geographical areas on an interactive map with place search functionality.

## 🚀 Features

### Core Functionality
- **Interactive Map**: Google Maps integration with tap-to-interact functionality
- **Polygon Area Creation**: Create custom geographical areas by tapping points on the map
- **Area Management**: Save, view, select, and delete created areas
- **Place Search**: Search for locations using Google Places API with auto-complete
- **Local Storage**: Persistent data storage using Room database

### User Interface
- **Material 3 Design**: Modern UI following Material Design 3 guidelines
- **Splash Screen**: Branded startup experience with gradient background
- **Floating Search Bar**: Overlay search functionality on the map
- **Mode Indicators**: Visual feedback for creation vs. viewing modes
- **Dropdown Lists**: Easy area selection and management
- **Confirmation Dialogs**: Safe area deletion with user confirmation

## 📱 How to Use the App

### 1. Getting Started
- Launch the app to see the splash screen with Sowit logo
- The main screen opens with a map centered on Casablanca, Morocco
- Use the floating search bar to find specific locations

### 2. Creating Areas
1. Tap the **"+"** button in the top bar to enter creation mode
2. A mode indicator chip appears showing "Mode Création"
3. Tap on the map to add points for your polygon (minimum 3 points required)
4. Points appear as markers and connect to form a polygon
5. Tap **"Sauvegarder"** when finished
6. Enter a name for your area in the dialog
7. Confirm to save the area

### 3. Managing Areas
- **View Areas**: Tap the list icon to see all saved areas
- **Select Area**: Choose an area from the dropdown to highlight it on the map
- **Delete Area**: Use the delete icon next to any area in the list
- **Area Details**: Each area shows its name and number of points

### 4. Search Functionality
- Use the search bar at the top to find places
- Type location names (cities, landmarks, addresses)
- Tap search or press enter to navigate to the location
- Clear search results using the clear button

## 🛠 Technologies Used

### Android Development
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components and theming
- **Android SDK**: Target SDK 36, Minimum SDK 24

### Architecture & Patterns
- **MVVM Architecture**: Model-View-ViewModel pattern
- **Repository Pattern**: Data access abstraction
- **StateFlow**: Reactive state management
- **Coroutines**: Asynchronous programming

### Database & Storage
- **Room Database**: Local SQLite database with type-safe queries
- **Kotlin Symbol Processing (KSP)**: Compile-time code generation
- **JSON Serialization**: Gson for polygon points storage

### Maps & Location Services
- **Google Maps Compose**: Interactive map integration
- **Google Places API**: Location search and autocomplete
- **Google Play Services**: Maps and location services

### Key Dependencies
```kotlin
// UI & Compose
implementation("androidx.compose.material3")
implementation("androidx.activity:activity-compose")

// Database
implementation("androidx.room:room-runtime:2.7.2")
ksp("androidx.room:room-compiler:2.7.2")

// Maps & Places
implementation("com.google.maps.android:maps-compose:2.11.0")
implementation("com.google.android.gms:play-services-maps:18.1.0")
implementation("com.google.android.libraries.places:places:3.3.0")

// Utilities
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("com.google.code.gson:gson:2.10.1")
```

## 🏗 Project Structure

```
app/src/main/java/ma/abdokarimi/sowittechtest/
├── MainActivity.kt              # Main activity and UI composition
├── SplashActivity.kt           # Splash screen implementation
├── mvvm/
│   └── MainViewModel.kt        # Business logic and state management
├── entity/
│   └── Area.kt                 # Data model for geographical areas
├── database/
│   └── AppDatabase.kt          # Room database configuration
├── dao/
│   └── AreaDao.kt              # Data access object for areas
├── ui/components/              # Reusable UI components
│   ├── SearchTopBar.kt         # Floating search bar
│   ├── AreaListDropdown.kt     # Area selection dropdown
│   ├── SaveAreaDialog.kt       # Area naming dialog
│   ├── DeleteAreaDialog.kt     # Deletion confirmation
│   ├── ModeChip.kt            # Mode indicator
│   └── InstructionToast.kt    # User guidance
└── utils/                      # Utility classes
    ├── PlacesSearchUtils.kt    # Google Places API integration
    ├── SerializationUtils.kt   # JSON serialization helpers
    └── LocationUtils.kt        # Geographic calculations
```

## 🔧 Setup Requirements

1. **Android Studio**: Latest version with Kotlin support
2. **Google Maps API Key**: Required for map functionality
3. **Google Places API Key**: Required for search functionality
4. **Minimum Android Version**: API 24 (Android 7.0)

The app provides a complete solution for geographical area management with a focus on user experience and modern Android development practices.
