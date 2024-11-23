```markdown
# 📚 MCA Department Virtual Interactive Chatbot

An intelligent chatbot system designed to help users access MCA department-related information through natural language queries.

## ✨ Features

- 🔒 Admin-controlled content management
- 🧠 Natural language query processing
- 📄 PDF document analysis and indexing
- 🔑 Secure authentication system
- 💬 User-friendly chat interface
- 🎤 Voice input/output support
- 🤖 Context-aware responses

## 📂 Project Structure

```bash
mca-bot/
│
├── app/                    # 📱 Android application files
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/      # 🔤 Java source files
│   │   │   └── res/       # 🎨 Android resources
│   │   └── test/          # 🧪 Test files
│   └── build.gradle       # 🛠 App level build file
│
├── backend/               # 🌐 Backend server files
│   ├── models/           # 📊 ML models and data processing
│   ├── utils/            # 🛠 Utility functions
│   └── requirements.txt  # 📜 Python dependencies
│
└── README.md             # 📖 Project documentation
```

## ⚙️ Setup Instructions

### 📋 Prerequisites

- 📱 Android Studio (latest version)
- ☕ JDK 11 or higher
- 🐍 Python 3.8 or higher
- 🔥 Firebase account
- 🛠 Git

### 🚀 Android App Setup

1. **Clone the repository:**
```bash
git clone <repository-url>
cd mca-bot
```

2. **Open Android Studio:**
   - Open the project by selecting the root folder
   - Let Gradle sync complete
   - Install any required SDK packages if prompted

3. **🔥 Firebase Setup:**
   - Create a new Firebase project at Firebase Console
   - Add an Android app in Firebase project settings
   - Download google-services.json and place it in the app/ directory
   - Enable Authentication and Realtime Database in Firebase Console

4. **Build and Run:**
   - Connect an Android device or start an emulator
   - Click Run in Android Studio (or press Shift + F10)
   - Select your device and click OK

### 🌐 Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Create and activate a virtual environment:**
```bash
python -m venv venv
# On Windows
venv\Scripts\activate
# On Unix/MacOS
source venv/bin/activate
```

3. **Install dependencies:**
```bash
pip install -r requirements.txt
```

4. **Configure environment:**
   - Copy .env.example to .env
   - Update Firebase credentials and other configurations

5. **Run the server:**
```bash
python app.py
```

## 🧪 Testing

**Run Android tests:**
```bash
./gradlew test            # Unit tests
./gradlew connectedCheck  # Instrumented tests
```

**Run backend tests:**
```bash
python -m pytest
```

## 📱 Minimum Requirements

- 📱 Android 6.0 (API level 23) or higher
- 🧠 2GB RAM
- 💾 100MB free storage
- 🌐 Internet connection for full functionality

## 🛠 Technologies Used

- 📱 Android (Java)
- 🔥 Firebase (Authentication, Storage)
- 🗄 SQLite/Firebase Realtime Database
- 🤖 TensorFlow Lite
- 📄 PDF processing libraries
- 🧠 Natural Language Processing tools

## 🌟 Requirements

- 📱 Android Studio
- 🐍 Python 3.8+
- 🔥 Firebase account
- ☁️ Google Cloud Platform account
```
