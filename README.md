# 📚 MCA Department Virtual Interactive Chatbot

> An intelligent chatbot system designed to help users access MCA department-related information through natural language queries.

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Python](https://img.shields.io/badge/Python-3.8+-green.svg)
![Android](https://img.shields.io/badge/Android-6.0+-brightgreen.svg)
![Firebase](https://img.shields.io/badge/Firebase-Enabled-orange.svg)

</div>

# ✨ Features

- 🔒 **Admin-controlled Content Management**  
  Secure backend system for managing chatbot responses and knowledge base

- 🧠 **Natural Language Query Processing**  
  Advanced NLP capabilities for understanding user intent and context

- 📄 **PDF Document Analysis**  
  Intelligent parsing and indexing of department documents

- 🔑 **Secure Authentication**  
  Role-based access control with Firebase authentication

- 💬 **User-friendly Interface**  
  Intuitive chat interface with modern Material Design

- 🎤 **Voice Interaction**  
  Support for voice input and text-to-speech output

- 🤖 **Context-aware Responses**  
  Smart response generation based on conversation history

# 📂 Project Structure

```plaintext
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

# ⚙️ Setup Instructions

## 📋 Prerequisites

- 📱 Android Studio (latest version)
- ☕ JDK 11 or higher
- 🐍 Python 3.8 or higher
- 🔥 Firebase account
- 🛠 Git

## 🚀 Installation

### Android App Setup

1. **Clone the repository:**
```bash
git clone https://github.com/yourusername/mca-bot.git
cd mca-bot
```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

3. **Firebase Configuration:**
   - Create a new project in [Firebase Console](https://console.firebase.google.com)
   - Add an Android app in project settings
   - Download `google-services.json`
   - Place it in the `app/` directory
   - Enable Authentication and Realtime Database

4. **Build & Run:**
   - Connect an Android device or start an emulator
   - Click "Run" (or press Shift + F10)
   - Select your target device

### 🌐 Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Create virtual environment:**
```bash
python -m venv venv

# Windows
venv\Scripts\activate

# Unix/MacOS
source venv/bin/activate
```

3. **Install dependencies:**
```bash
pip install -r requirements.txt
```

4. **Configure environment:**
   - Copy `.env.example` to `.env`
   - Update Firebase credentials
   - Configure other environment variables

5. **Start the server:**
```bash
python app.py
```

# 🧪 Testing

## Android Tests
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedCheck
```

## Backend Tests
```bash
python -m pytest
```

# 📱 System Requirements

- Android 6.0 (API level 23) or higher
- 2GB RAM minimum
- 100MB free storage
- Active internet connection

# 🛠 Technologies Used

- 📱 **Frontend:**
  - Android (Java)
  - Material Design Components
  - Retrofit for API calls
  - Room Database

- 🌐 **Backend:**
  - Python FastAPI
  - TensorFlow/PyTorch
  - Natural Language Processing
  - PDF processing libraries

- ☁️ **Cloud Services:**
  - Firebase Authentication
  - Firebase Realtime Database
  - Firebase Cloud Storage
  - Google Cloud Platform

# 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

# 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

# 📧 Contact

For any queries, please reach out to:
- Project Maintainer - [abhiramak963@gmail.com](mailto:abhiramak963@gmail.com)

---
<div align="center">
Made with ❤️ by MCA Department
</div>
