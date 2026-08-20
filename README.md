# Selling.uz — Android app

Native Android client of [Selling.uz](https://selling.uz), a classifieds marketplace for Uzbekistan.

**Live on Google Play:** [uz.promo.selling](https://play.google.com/store/apps/details?id=uz.promo.selling)

This is the actively developed production repository of the Android app. The Spring Boot backend, iOS app, and Next.js web client live in separate private repositories.

## Features

- Listings feed with search, categories, filters, and a map view
- AI-assisted listing creation: photo → draft title/description/category, plus AI price suggestion
- Dynamic per-category forms (parameters, options, units) in three languages (uz / ru / en)
- Real-time chat between buyers and sellers (WebSocket) with edit, delete, and reply
- Phone (OTP) and Google sign-in
- Payments for boost & premium via Payme and Click, with promo codes
- Premium seller membership: badge, listing analytics, extended AI limits
- Push notifications (Firebase Cloud Messaging)
- Favorites, "near me" radius search, price-drop badges

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3, Navigation Compose, Paging 3)
- **Hilt** for dependency injection
- **Retrofit / OkHttp** for the REST API, WebSocket for chat
- **Room** for local persistence, **WorkManager** for background work
- **Google Maps Compose** for map views, **Coil** for images
- **Firebase** Analytics & Cloud Messaging

## Building

The app builds against the public API at `selling.uz`:

```
./gradlew :app:assembleDebug
```
