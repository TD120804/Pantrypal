# 🥕 PantryPal AI

### **Smarter Kitchens. Less Waste.**

**An AI-powered Android pantry management application built with Java,
MVVM, Room Database, Google Gemini AI, Google ML Kit, and WorkManager to
help households organize groceries, reduce food waste, and cook
smarter.**

📦 **Inventory** • 📷 **Barcode Scanner** • 🤖 **AI Recipes** • 📊
**Analytics** • 🔔 **Notifications**

------------------------------------------------------------------------

# 🌿 Overview

PantryPal AI is a native Android application designed to simplify pantry
management while reducing household food waste.

Instead of manually keeping track of groceries, PantryPal combines
intelligent inventory management, barcode scanning, expiry tracking,
analytics, and Google Gemini AI to help users make smarter decisions
with the food they already own.

------------------------------------------------------------------------

# ✨ Key Features

## 📦 Smart Pantry Management

-   Add, edit and delete grocery items
-   Quantity & unit tracking
-   Category management
-   Expiry date management
-   Smart duplicate handling
-   Support for branded and loose products

------------------------------------------------------------------------

## 📷 Barcode Intelligence

Duplicate Detection Logic

``` text
Scan Product
      │
      ▼
Duplicate Found?
      │
 ┌────┴──────────────┐
 │                   │
 ▼                   ▼
Same Expiry     Different Expiry
 │                   │
 ▼                   ▼
Increase Qty     Create New Item

Loose Product
      │
      ▼
Always Create New Entry
```

------------------------------------------------------------------------

## 🤖 AI Recipe Generator

Powered by **Google Gemini AI**.

Generate recipes using ingredients already available in your pantry.

The AI can:

-   Generate complete recipes
-   Suggest missing ingredients
-   Reduce food waste
-   Encourage smarter cooking

------------------------------------------------------------------------

## 📊 Pantry Analytics

-   Pantry Health Score
-   Waste Risk Score
-   Pie Chart
-   Line Chart
-   Bar Chart
-   Interactive dashboard insights

------------------------------------------------------------------------

## 🔔 Smart Expiry Notifications

Background reminders powered by **WorkManager** automatically notify
users about products nearing expiry.

------------------------------------------------------------------------

# 🏗️ Architecture

``` text
Activities / Fragments
          │
          ▼
      ViewModel
          │
          ▼
      Repository
      ┌──────────────┐
      ▼              ▼
Room Database   Gemini AI
      │              │
      └──────┬───────┘
             ▼
       WorkManager
             ▼
     NotificationManager
```

------------------------------------------------------------------------

# 🧠 AI Workflow

``` text
User
 │
 ▼
Pantry Inventory
 │
 ▼
Prompt Builder
 │
 ▼
Google Gemini API
 │
 ▼
Recipe Response
 │
 ▼
User
```

------------------------------------------------------------------------

# 🛠️ Tech Stack

  Category           Technology
  ------------------ ---------------------
  Language           Java
  IDE                Android Studio
  Architecture       MVVM
  Database           Room Database
  AI                 Google Gemini API
  Barcode            Google ML Kit
  Charts             MPAndroidChart
  Background Tasks   WorkManager
  Notifications      NotificationManager
  UI                 Material Design

------------------------------------------------------------------------

# 📂 Project Structure

``` text
app/
├── ai/
├── database/
├── models/
├── repository/
├── scanner/
├── ui/
├── utils/
├── viewmodels/
└── workers/
```

------------------------------------------------------------------------

# 🚀 Installation

``` bash
git clone https://github.com/TD120804/Pantrypal.git
```

Open the project in Android Studio.

Create a `local.properties` file containing:

``` properties
GEMINI_API_KEY=YOUR_API_KEY
```

Sync Gradle and run the application.

------------------------------------------------------------------------

# 💡 Future Roadmap

-   OCR Grocery Bill Scanner
-   Smart Shopping List
-   Price Comparison
-   Family Shared Pantry
-   Nutrition Insights
-   Cloud Synchronization

------------------------------------------------------------------------

# 👩‍💻 Developer

**Trisha Sameer Date**

Master of Computer Applications (MCA)

Veermata Jijabai Technological Institute (VJTI)

------------------------------------------------------------------------
## 🌱 PantryPal AI

### Smarter Kitchens. Less Waste.

⭐ If you found this project useful, consider starring the repository!

Made with ❤️ using Java, Android, and Google Gemini AI.
