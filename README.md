# Finna API Image Gallery

A native Android application that fetches and displays historical landscape images from the open Finna REST API. Implemented with MVVM architecture and Jetpack Compose.

## Technologies and Architecture
* **User Interface (View):** Jetpack Compose (`LazyVerticalStaggeredGrid`, `HorizontalPager`)
* **State Management (ViewModel):** MVVM, Kotlin Coroutines, `StateFlow`
* **Network Layer (Model):** Retrofit2 & Gson
* **Image Loading:** Coil (`AsyncImage`)

## Key Features
* **Dynamic API Search:** Year selection (1880–2020) using a Compose Slider.
* **Infinite Scroll:** Automatically loads more results at the end of the list (`page`, `limit`).
* **Full-Screen Browsing:** Image zooming and swipe-to-browse functionality for search results.
* **Image Randomization:** The search utilizes a dynamic seed (`random_seed`) to ensure results vary with each search.

## API Integration
The application makes asynchronous HTTP GET requests to the Finna API.
* **Base URL:** `https://api.finna.fi/`
* **Example Request (free text search + image filter):**
  `https://api.finna.fi/api/v1/search?lookfor=maisema+1920&filter[]=format:"0/Image/"&limit=20`

## How to Run
1. Open the project in Android Studio.
2. Sync Gradle dependencies (Retrofit, Coil, Gson).
3. Run the app on an emulator or a physical device with an active internet connection.