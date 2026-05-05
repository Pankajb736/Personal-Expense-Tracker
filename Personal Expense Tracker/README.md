







<div align="center">

# 💰 CashKeeper
**A Modern Personal Finance Management App for Android**

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Language">
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase">
  <img src="https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white" alt="Android Studio">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-blue?style=for-the-badge" alt="Version">
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License">
  <img src="https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge" alt="Build Status">
  <img src="https://img.shields.io/badge/Open_Source-❤️-red?style=for-the-badge" alt="Open Source">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Min_SDK-21-orange?style=for-the-badge" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target_SDK-31-blue?style=for-the-badge" alt="Target SDK">
  <img src="https://img.shields.io/badge/Gradle-7.2-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle">
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#screenshots">Screenshots</a> •
  <a href="#installation">Installation</a> •
  <a href="#tech-stack">Tech Stack</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

---

### 🎯 **Track. Analyze. Save. Repeat.**

*CashKeeper is a comprehensive personal finance management application that helps you track income and expenses, visualize spending patterns with beautiful charts, and make informed financial decisions.*

</div>

---

## 🌟 Overview
CashKeeper is a modern, feature-rich personal finance management app for Android. Track your income and expenses, visualize spending patterns, and stay on top of your budget with real‑time Firebase sync and a clean Material UI.

### What, Why, How
- **What**: A lightweight, offline‑tolerant (Firebase cache) app to add/view/search income and expenses, with analytics and a compact dashboard.
- **Why**: Personal finance tracking should be fast, reliable, and understandable. We optimize for quick entry, date-accurate sorting, and clear summaries.
- **How**: Firebase Auth + Realtime Database, MPAndroidChart, and a modular Android UI. We store both a human‑readable `date` and a sortable `timestamp` for correct ordering. Category totals are updated atomically using Firebase Transactions for consistency.

### Recent Enhancements (2025-08)
- Added `timestamp` to every transaction and switched dashboard queries to `orderByChild("timestamp")` for true date-wise sorting.
- Category accumulation: each add updates `/IncomeCategoryTotals/{uid}/{category}` and `/ExpenseCategoryTotals/{uid}/{category}` atomically.
- Faster dashboard reads (scoped queries, local cache) and resilient inserts (success/failure listeners + toasts).
- Expanded category pickers (Income: Salary, Business, Freelance, … | Expense: Food, Groceries, Rent, Utilities, …).

---

## 🚀 Features

- **User-Friendly Interface:** Intuitive navigation with Material Design
- **User Authentication:** Secure login, registration, and password reset (Firebase Auth)
- **Income & Expense Tracking:** Add, edit, view, and search transactions
- **Advanced Analytics:**
  - Bar charts for income and expenses (real-time, filterable)
  - Daily/Monthly toggles and accurate date-wise grouping 📆
- **Dashboard:** Daily/monthly expense summaries
- **Category Totals:** Automatic accumulation per category using atomic Firebase Transactions
- **Profile Management:** View & update profile, upload image 👥
- **Export:** Export all data as CSV 📁
- **Modern UI:** Material Components, custom fonts (Montserrat, Roboto), responsive layouts
- **Navigation:** Bottom navigation bar and navigation drawer
- **Robust Error Handling:** Detailed error messages for all auth and network failures 🚨
- **Firebase-First:** All data stored securely in Firebase Realtime Database and Storage 🔒
- **Real-time Updates:** Instant updates to charts and data as transactions are added or modified


## 📷 Screenshots
<!-- Add screenshots here -->
- Dashboard
- Analytics
- Add/Edit Expense
- Profile
- About


## 🗂️ Project Structure


```
app/src/main/java/com/example/genz/
├── AnalyticsFragment.java      # Analytics page, income/expense bar charts
├── DashboardFragment.java      # Dashboard with expense summaries
├── ExpenseFragment.java        # Add/view/edit/delete expenses
├── IncomeFragment.java         # Add/view/edit/delete income
├── Profile.java                # User profile page
├── Registration.java           # User registration
├── MainActivity.java           # App entry, navigation
├── about.java                  # About page
├── change_password.java        # Change password screen
├── first_home_page.java        # Main navigation activity
├── home_screen.java            # Home page
├── Model/Data.java             # Transaction data model
└── ... (other utility/activity files)

app/src/main/res/values/
app/src/main/res/layout/
├── activity_about.xml              # About page layout
├── fragment_analytics.xml          # Analytics screen layout
├── fragment_dashboard.xml          # Dashboard layout
├── fragment_expense.xml            # Expenses page layout
├── fragment_income.xml             # Income page layout
├── profile_toolbar.xml, about_toolbar.xml, ...
└── ... (other layouts)


app/src/main/res/menu/
├── bottommenu.xml                  # Bottom navigation menu
├── nav_menu.xml                    # Navigation drawer menu
└── ...
```

## 🔗 Firebase Data Structure
- `/ExpenseData/{uid}/` — All expense entries for user
- `/IncomeData/{uid}/` — All income entries for user
- Each entry: `{ amount, type, note, id, date, timestamp }`
- `/IncomeCategoryTotals/{uid}/{category}` — Running totals per category (atomic increments)
- `/ExpenseCategoryTotals/{uid}/{category}` — Running totals per category (atomic increments)
- `/ProfileImages/{uid}/` — Profile images


## 📊 Analytics & Dashboard
- **Dashboard:**
  - Bar chart of expenses (toggle daily/monthly)
  - Quick summary of income, expense, and balance
- **Analytics Page:**
  - Bar charts: Income and Expense (real-time, filterable)
  - Filter: All time, this month, this year, custom range

## ⚡ Setup & Usage
1. **Clone the repository:**
   ```sh
   git clone https://github.com/Chirag-S-Kotian/CashKeeper.git
   cd genz
   ```

2. **Open in Android Studio** (latest recommended)
    - Open Android Studio
    - Import the project from the cloned directory
    - Ensure you have the latest Android SDK and build tools installed
    - Sync Gradle files
3. **Add Firebase:**
   - Download `google-services.json` from your Firebase Console
   - Place it in `app/`
   - Enable Auth, Realtime Database, and Storage in your Firebase project
   - Add dependencies in `build.gradle`:
     ```groovy
     implementation 'com.google.firebase:firebase-auth:21.0.1'
     implementation 'com.google.firebase:firebase-database:20.0.3'
     implementation 'com.google.firebase:firebase-storage:20.0.0'
     implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
     ```

   - Apply Google Services plugin at the bottom:
     ```groovy
     apply plugin: 'com.google.gms.google-services'
     ```

4. **Fonts/Colors:**
   - Uses downloadable fonts (no setup needed)
   - Colors in `res/values/colors.xml`
5. **Build & Run:** Sync Gradle, build, and run on device/emulator

### Data & Ordering Notes
- We store both a human‑readable `date` (e.g., `Jun 20, 2025`) and a machine‑sortable `timestamp` (epoch millis).
- Queries are made with `orderByChild("timestamp")` so lists and totals are truly date-wise even if devices have different locales.

### Category Accumulation
- Each time you add an entry, we increment `/IncomeCategoryTotals/{uid}/{category}` or `/ExpenseCategoryTotals/{uid}/{category}` atomically via Firebase Transactions. This ensures totals are race‑condition safe even across multiple devices.

6. **Sign in or register to use all features**


## 🧑‍💻 Contributing
- Fork this repo and submit pull requests!
- Please open issues for bugs or feature requests.
- Follow best practices for Java/Android and Firebase security.

## ⚠️ Troubleshooting
- If you see resource or R class errors, try `Build > Clean Project` and `Sync Project with Gradle Files` in Android Studio.
- Make sure `google-services.json` is in the correct location.
- Check Firebase rules for database and storage access.
- If charts don’t show, ensure MPAndroidChart is in your dependencies.


## 🛠️ Tech Stack
- **Language:** Java
- **UI:** Android XML, Material Components, CardView
- **Charts:** MPAndroidChart (BarChart)
- **Backend:** Firebase Realtime Database, Firebase Auth, Firebase Storage
- **IDE:** Android Studio Chipmunk 2021.2.1
- **Fonts:** Montserrat, Roboto (downloadable fonts)
---

## 📄 License
MIT License (see LICENSE file)
---


© 2025 Chirag S Kotian. CashKeeper is open-source and community-driven.


## 🔐 Authentication & API Details

### Authentication Methods
- **Register:**
  - Endpoint: Firebase Auth (Email/Password)
  - Fields: Email, Password
  - Validation: Email format, password length
- **Login:**
  - Endpoint: Firebase Auth (Email/Password)
  - Fields: Email, Password
  - Validation: Email format, password length
- **Logout:**
  - Firebase signOut
- **Password Reset:**
  - Endpoint: Firebase Auth (Email/Password)
  - Fields: Email
  - Action: Send password reset email

---

## 🗄️ Database Structure (Firebase Realtime Database)

### Data Organization
- `/IncomeData/{uid}/` — Stores user income entries
- `/ExpenseData/{uid}/` — Stores user expense entries
- Each entry contains: amount, type, note, date, etc.
- `/UserProfile/{uid}/` — Stores user profile information
- `/Feedback/{uid}/` — Stores user feedback

### Sample Data Structure

#### Income Entry Example
```json
{
  "IncomeData": {
    "uid123": {
      "-Mxyz...": {
        "amount": 5000,
        "type": "Salary",
        "note": "June Salary",
        "date": "Jun 01, 2025",
        "timestamp": 1759344000000
      }
    }
  }
}
```

#### Expense Entry Example
```json
{
  "ExpenseData": {
    "uid123": {
      "-Mxyz...": {
        "amount": 2000,
        "type": "Groceries",
        "note": "Weekly groceries",
        "date": "Jun 02, 2025",
        "timestamp": 1759430400000
      }
    }
  }
}
```

#### Category Totals Example
```json
{
  "ExpenseCategoryTotals": {
    "uid123": {
      "Groceries": 12450,
      "Rent": 15000
    }
  }
}
```

---

## 👤 Profile Management
- User profile data is stored under `/UserProfile/{uid}/`
- Contains fields like name, email, profile image URL, etc.

---

## 💬 Feedback System
- User feedback is stored under `/Feedback/{uid}/`
- Contains fields like feedback text, timestamp, etc.

---
## 📦 Storage Management
- Used for profile images and file uploads (Firebase Storage)
- Images are stored under `/ProfileImages/{uid}/`


---

## 📈 Analytics Generation
- Dashboard analytics are generated using data from the Realtime Database
- Real-time chart updates based on user transactions
- Filterable by date ranges and transaction types


---

## 📋 API Documentation

### Firebase Realtime Database API

#### Authentication Endpoints

**Register User**
```java
FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // User successfully registered
        } else {
            // Handle registration error
        }
    });
```

**Login User**
```java
FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            // User successfully logged in
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // Handle login error
        }
    });
```

**Reset Password**
```java
FirebaseAuth.getInstance().sendPasswordResetEmail(email)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            // Password reset email sent
        }
    });
```

#### Data Operations

**Add Income Entry**
```java
String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
DatabaseReference incomeRef = FirebaseDatabase.getInstance()
    .getReference().child("IncomeData").child(uid);

String id = incomeRef.push().getKey();
String date = DateFormat.getDateInstance().format(new Date());
Data incomeData = new Data(amount, type, note, id, date);

incomeRef.child(id).setValue(incomeData)
    .addOnSuccessListener(aVoid -> {
        // Income added successfully
        Toast.makeText(context, "Income added!", Toast.LENGTH_SHORT).show();
    })
    .addOnFailureListener(e -> {
        // Handle error
        Log.e("Firebase", "Error adding income: " + e.getMessage());
    });
```

**Add Expense Entry**
```java
String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
DatabaseReference expenseRef = FirebaseDatabase.getInstance()
    .getReference().child("ExpenseData").child(uid);

String id = expenseRef.push().getKey();
String date = DateFormat.getDateInstance().format(new Date());
Data expenseData = new Data(amount, type, note, id, date);

expenseRef.child(id).setValue(expenseData)
    .addOnSuccessListener(aVoid -> {
        // Expense added successfully
    })
    .addOnFailureListener(e -> {
        // Handle error
    });
```

**Retrieve User Data**
```java
DatabaseReference userDataRef = FirebaseDatabase.getInstance()
    .getReference().child("ExpenseData").child(uid);

userDataRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        List<Data> expenses = new ArrayList<>();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Data expense = dataSnapshot.getValue(Data.class);
            if (expense != null) {
                expenses.add(expense);
            }
        }
        // Update UI with expenses
        updateExpensesList(expenses);
    }
    
    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.e("Firebase", "Error loading data: " + error.getMessage());
    }
});
```

**Update Data Entry**
```java
Map<String, Object> updates = new HashMap<>();
updates.put("amount", newAmount);
updates.put("type", newType);
updates.put("note", newNote);

DatabaseReference itemRef = FirebaseDatabase.getInstance()
    .getReference().child("ExpenseData").child(uid).child(itemId);

itemRef.updateChildren(updates)
    .addOnSuccessListener(aVoid -> {
        // Update successful
    })
    .addOnFailureListener(e -> {
        // Handle error
    });
```

**Delete Data Entry**
```java
DatabaseReference itemRef = FirebaseDatabase.getInstance()
    .getReference().child("ExpenseData").child(uid).child(itemId);

itemRef.removeValue()
    .addOnSuccessListener(aVoid -> {
        // Deletion successful
        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
    })
    .addOnFailureListener(e -> {
        // Handle error
    });
```

### Firebase Storage API

**Upload Profile Image**
```java
StorageReference storageRef = FirebaseStorage.getInstance().getReference();
StorageReference profileImagesRef = storageRef.child("ProfileImages/" + uid + "/profile.jpg");

UploadTask uploadTask = profileImagesRef.putFile(imageUri);
uploadTask.addOnSuccessListener(taskSnapshot -> {
    // Get download URL
    profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
        String downloadUrl = uri.toString();
        // Save URL to user profile in database
        updateUserProfileImage(downloadUrl);
    });
}).addOnFailureListener(exception -> {
    // Handle upload error
});
```

### Chart Integration Examples

**Setup Bar Chart**
```java
private void setupBarChart(BarChart chart) {
    chart.getDescription().setEnabled(false);
    chart.getXAxis().setDrawGridLines(false);
    chart.getAxisRight().setEnabled(false);
    chart.getAxisLeft().setDrawGridLines(false);
    chart.setExtraOffsets(10, 10, 10, 10);
    chart.animateY(1000);
}

private void updateBarChart(Map<String, Float> data) {
    ArrayList<BarEntry> entries = new ArrayList<>();
    int index = 0;
    for (Map.Entry<String, Float> entry : data.entrySet()) {
        entries.add(new BarEntry(index++, entry.getValue()));
    }
    
    BarDataSet dataSet = new BarDataSet(entries, "Expenses");
    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
    
    BarData barData = new BarData(dataSet);
    chart.setData(barData);
    chart.invalidate();
}
```

**Setup Pie Chart**
```java
private void setupPieChart(PieChart chart) {
    chart.setUsePercentValues(true);
    chart.getDescription().setEnabled(false);
    chart.setDrawHoleEnabled(true);
    chart.setHoleColor(Color.WHITE);
    chart.setRotationEnabled(true);
    
    Legend legend = chart.getLegend();
    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
    legend.setOrientation(Legend.LegendOrientation.VERTICAL);
}

private void updatePieChart(Map<String, Float> categoryData) {
    ArrayList<PieEntry> entries = new ArrayList<>();
    for (Map.Entry<String, Float> entry : categoryData.entrySet()) {
        entries.add(new PieEntry(entry.getValue(), entry.getKey()));
    }
    
    PieDataSet dataSet = new PieDataSet(entries, "Categories");
    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
    
    PieData pieData = new PieData(dataSet);
    chart.setData(pieData);
    chart.invalidate();
}
```

### Error Handling Best Practices

**Network Error Handling**
```java
private void handleFirebaseError(DatabaseError error) {
    String errorMessage;
    switch (error.getCode()) {
        case DatabaseError.NETWORK_ERROR:
            errorMessage = "Network connection failed. Please check your internet.";
            break;
        case DatabaseError.PERMISSION_DENIED:
            errorMessage = "Access denied. Please login again.";
            break;
        case DatabaseError.UNAVAILABLE:
            errorMessage = "Service temporarily unavailable. Try again later.";
            break;
        default:
            errorMessage = "An error occurred: " + error.getMessage();
    }
    
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    Log.e("FirebaseError", "Error: " + error.getDetails());
}
```

**Input Validation**
```java
private boolean validateInput(String amount, String type) {
    if (TextUtils.isEmpty(amount)) {
        editTextAmount.setError("Amount is required");
        return false;
    }
    
    if (TextUtils.isEmpty(type)) {
        editTextType.setError("Type is required");
        return false;
    }
    
    try {
        double amountValue = Double.parseDouble(amount);
        if (amountValue <= 0) {
            editTextAmount.setError("Amount must be positive");
            return false;
        }
    } catch (NumberFormatException e) {
        editTextAmount.setError("Invalid amount format");
        return false;
    }
    
    return true;
}
```

### Usage Examples

**Complete Income Addition Flow**
```java
public void addIncome(View view) {
    String amountStr = editTextAmount.getText().toString().trim();
    String type = editTextType.getText().toString().trim();
    String note = editTextNote.getText().toString().trim();
    
    if (!validateInput(amountStr, type)) {
        return;
    }
    
    int amount = Integer.parseInt(amountStr);
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference incomeRef = FirebaseDatabase.getInstance()
        .getReference().child("IncomeData").child(uid);
    
    String id = incomeRef.push().getKey();
    String date = DateFormat.getDateInstance().format(new Date());
    Data incomeData = new Data(amount, type, note, id, date);
    
    // Show loading indicator
    progressBar.setVisibility(View.VISIBLE);
    
    incomeRef.child(id).setValue(incomeData)
        .addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Income added successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
            updateTotalIncome();
        })
        .addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Failed to add income: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        });
}
```

---

<div align="center">

## 🤝 Support

If you find this project helpful, please give it a ⭐️!

[Report Bug](https://github.com/yourusername/cashkeeper/issues) • [Request Feature](https://github.com/yourusername/cashkeeper/issues) • [Documentation](https://github.com/yourusername/cashkeeper/wiki)

</div>

