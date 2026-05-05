### Title
CashKeeper – Personal Finance Management App (Android)

### Introduction
CashKeeper is an Android app to track income and expenses, analyze spending with charts, and manage personal finances in real time using Firebase services. It emphasizes a clean Material UI, simple navigation, and data privacy.

### Objectives
- Provide fast, intuitive income and expense entry and management
- Deliver real‑time analytics (charts, summaries) to aid decision making
- Enable secure authentication and user profile management
- Store and sync data reliably across devices via cloud backend
- Support data export for backups and external analysis

### Scope
- User registration, login, logout, password reset, and change password
- CRUD for income and expense entries with search and filters
- Dashboard with daily/monthly summaries and totals
- Analytics with bar/pie charts and date range filters
- Profile management with image upload
- CSV export of transaction data

### Future Scope
- Budgeting and category‑wise limits with alerts
- Recurring transactions and reminders
- OCR receipt scanning and auto‑categorization
- Offline‑first mode with sync conflict handling
- Data visualization enhancements (trends, forecasts)
- Multi‑currency support and localization

### Advantages
- Real‑time sync and updates via Firebase
- Simple, modern Material UI and smooth navigation
- Secure auth with email/password and server‑side rules
- Scales without managing servers; low ops overhead

### Disadvantages
- Requires internet for full functionality (cloud‑backed)
- Vendor lock‑in to Firebase services
- Free tier limits may constrain heavy usage

### Frontend
- Platform: Android (Java)
- UI: XML layouts, Material Components, RecyclerView
- Charts: MPAndroidChart (BarChart, PieChart)
- Navigation: Bottom navigation and navigation drawer
- Minimum SDK 21; Target SDK 31

### Backend
- Firebase Authentication: email/password auth, password reset
- Firebase Realtime Database: structured JSON data per user
- Firebase Storage: profile image uploads
- Security: Firebase Rules for auth‑scoped access

### Firebase Realtime Database
- Nodes
  - /IncomeData/{uid}/{id}: income entries
  - /ExpenseData/{uid}/{id}: expense entries
  - /UserInfo/{uid}: user profile basics (e.g., name, email, profileImageUrl)
  - /Feedback/{uid}/{id}: user feedback (optional)
- Each transaction entry commonly includes: amount, type, note, id, date
- Real‑time listeners used for live UI updates and analytics

### Modules
- Authentication: Registration, Login, Logout, Password Reset, Change Password
- Dashboard: summaries of income, expenses, and balance
- Income Management: add, edit, delete, list, search
- Expense Management: add, edit, delete, list, search
- Analytics: bar/pie charts with filter by month/year/custom range
- Profile: view/update profile, upload profile image
- Search: query transactions by text/date/type
- About: app information
- Export: CSV export of income/expense data

### Conclusion
CashKeeper delivers a practical, real‑time personal finance solution with a modern Android UI and a serverless Firebase backend. It covers core tracking, analytics, and profile features while leaving room for advanced budgeting, intelligence, and offline capabilities.

### Reference
- Android Developers: Material Design, Navigation, RecyclerView
- Firebase: Authentication, Realtime Database, Storage
- MPAndroidChart library documentation
- Material Design Guidelines

### DB Tables
Although Firebase is NoSQL (JSON), the logical data tables/nodes are:

| Node (Path)                 | Key Fields                                         | Purpose                                   |
|-----------------------------|----------------------------------------------------|-------------------------------------------|
| /IncomeData/{uid}/{id}      | amount:int, type:string, note:string, id, date     | Stores user income entries                |
| /ExpenseData/{uid}/{id}     | amount:int, type:string, note:string, id, date     | Stores user expense entries               |
| /UserInfo/{uid}             | name, email, profileImageUrl                        | Basic user profile info                   |
| /Feedback/{uid}/{id}        | text, timestamp                                    | Optional user feedback                    |

### Software Hardware Requirements
- Software (Development)
  - Android Studio (latest), JDK 8+, Gradle 7.2
  - Firebase project with Auth, Realtime Database, Storage enabled
  - google-services.json in app module
- Software (Runtime)
  - Android 5.0 (API 21) or higher, active internet connection
- Hardware (Development)
  - 4‑core CPU, 8 GB RAM (16 GB recommended), ≥10 GB free disk
- Hardware (User)
  - Android device with ≥1.5 GB RAM and stable network


