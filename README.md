<div align="center">

  # Sowit Technical Test

  **Create, manage, and visualize geographical areas with ease**

  [📱 View Demo](https://streamable.com/z520tb)

  [![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
  [![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
  [![Material 3](https://img.shields.io/badge/Design-Material%203-purple.svg)](https://m3.material.io)
  [![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
</div>

---

## What is this project?

This project is a modern Android app that makes geographical area management intuitive and powerful. Whether you're planning routes, marking territories, or organizing locations, MapZone provides the tools you need with a beautiful, user-friendly interface.

## Getting Started

### Prerequisites

Before you can run the project, you'll need to obtain a Google Maps API key:

1. **Go to the Google Cloud Console**
   - Visit [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one

2. **Enable required APIs**
   - Enable the **Maps SDK for Android**
   - Enable the **Places API**

3. **Create an API key**
   - Go to **APIs & Services > Credentials**
   - Click **Create Credentials > API Key**
   - Copy your API key

4. **Restrict your API key (recommended)**
   - Click on your API key to edit it
   - Under **Application restrictions**, select **Android apps**
   - Add your app's package name: `ma.abdokarimi.sowittechtest`
   - Under **API restrictions**, select **Restrict key** and choose:
     - Maps SDK for Android
     - Places API

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/AbdoXmDevos/Sowit_test
   cd Sowit_test
   ```

2. **Add your Google Maps API key**

   Open `app/src/main/AndroidManifest.xml` and replace the placeholder:

   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_ACTUAL_API_KEY_HERE" />
   ```

   **Important**: Never commit your real API key to version control!

3. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```

### How to Use

<table>
<tr>
<td width="50%">

**Create Areas**
1. Tap the `+` button to enter creation mode
2. Tap anywhere on the map to add points
3. Watch your polygon form in real-time
4. Save with a custom name

</td>
<td width="50%">

**Manage Areas**
- View all saved areas in the dropdown
- Select areas to highlight them
- Delete areas with confirmation
- See point counts for each area

</td>
</tr>
<tr>
<td width="50%">

**Search Places**
- Use the floating search bar
- Find cities, landmarks, addresses
- Navigate instantly to results
- Clear search anytime

</td>
<td width="50%">

**Auto-Save**
- All areas persist automatically
- No data loss between sessions
- Local storage for privacy
- Fast access to your work

</td>
</tr>
</table>

## Built With

<div align="center">

| Category | Technologies |
|----------|-------------|
| **Language** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) |
| **UI Framework** | ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white) |
| **Architecture** | ![MVVM](https://img.shields.io/badge/MVVM-FF6B6B?style=for-the-badge) ![StateFlow](https://img.shields.io/badge/StateFlow-4ECDC4?style=for-the-badge) |
| **Database** | ![Room](https://img.shields.io/badge/Room-45B7D1?style=for-the-badge) ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white) |
| **Maps** | ![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=for-the-badge&logo=googlemaps&logoColor=white) |

</div>

### Core Dependencies

```kotlin
dependencies {
    // Modern UI
    implementation "androidx.compose.material3:material3"
    implementation "androidx.activity:activity-compose"

    // Local Database
    implementation "androidx.room:room-runtime:2.7.2"
    ksp "androidx.room:room-compiler:2.7.2"

    // Maps & Places
    implementation "com.google.maps.android:maps-compose:2.11.0"
    implementation "com.google.android.gms:play-services-maps:18.1.0"
    implementation "com.google.android.libraries.places:places:3.3.0"

    // Async & Utils
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    implementation "com.google.code.gson:gson:2.10.1"
}
```

## Project Architecture

```
├── MainActivity.kt                   # App entry point
├── SplashActivity.kt                # Welcome experience
├── ui/
│   ├── components/                  # Reusable UI components
│   │   ├── MainScreen.kt            # Main app interface
│   │   ├── SearchTopBar.kt          # Smart search bar
│   │   ├── AreaListDropdown.kt      # Area management
│   │   ├── SaveAreaDialog.kt        # Area creation
│   │   ├── DeleteAreaDialog.kt      # Safe deletion
│   │   ├── ModeChip.kt             # Visual feedback
│   │   └── InstructionToast.kt     # User guidance
│   ├── theme/                       # Material 3 theming
│   └── viewmodels/                  # Business logic
│       └── MainViewModel.kt         # State management
├── database/
│   ├── AppDatabase.kt              # Room configuration
│   └── dao/AreaDao.kt              # Data access
├── entity/Area.kt                  # Data models
└── utils/                          # Helper utilities
    ├── PlacesSearchUtils.kt        # Google Places integration
    ├── SerializationUtils.kt       # JSON handling
    └── LocationUtils.kt            # Geographic calculations
```

## Acknowledgments

- **Google Maps Platform** for excellent mapping services
- **Material Design** for beautiful design guidelines
- **Jetpack Compose** for making UI development enjoyable

---

## Author

**Abderrahim Karimi**

- 🌐 GitHub: [@AbdoXmDevos](https://github.com/AbdoXmDevos)
- 📧 Email: abdssamadkarimi.ak@gmail.com
- 💼 LinkedIn: [Abdessamad Karimi](https://www.linkedin.com/in/abdessamad-karimi/)


