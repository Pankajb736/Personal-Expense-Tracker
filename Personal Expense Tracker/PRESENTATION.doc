# Project Q&A Handbook (Presentation Day)

Audience: External reviewers, stakeholders, and the team. Concise, high-signal Q/A grouped by module and owner. Use this deck for live questioning; export to PDF/DOCX from your editor.

---

## Register, Login, Splash (Owner: Princeton)

1) Q: What auth provider do we use?  
A: Firebase Auth (Email/Password) for speed, reliability, and low maintenance.

2) Q: Where are auth flows implemented?  
A: `Registration.java`, `home_screen.java`, `resetpassword.java`, `change_password.java`.

3) Q: How do we persist sessions?  
A: Firebase persists tokens; we check `FirebaseAuth.getInstance().getCurrentUser()` on start.

4) Q: How do we validate email/password?  
A: Client checks for non-empty/format; Firebase returns structured error codes for UI toasts.

5) Q: What does the splash do?  
A: `MainActivity` shows branding, then navigates via `Handler` to `home_screen`.

6) Q: How do we handle wrong credentials?  
A: Present friendly toasts based on Firebase error codes; do not navigate forward.

7) Q: How do we prevent UI jank during login?  
A: Disable submit while request is in-flight; re-enable on completion.

8) Q: Where is password reset implemented?  
A: `resetpassword.java` using `sendPasswordResetEmail`.

9) Q: Where is change password implemented?  
A: `change_password.java` via Firebase Auth updates after re-auth if required.

10) Q: How do we handle network loss during login?  
A: Catch failure callback and show a clear retry message; no crash.

11) Q: Any brute-force mitigation?  
A: Firebase’s backend rate-limits; UI locks button briefly after repeated failures.

12) Q: How do we handle empty inputs?  
A: Show inline errors (Required) and block submission.

13) Q: What happens post-registration?  
A: Navigate to home and initialize user-specific database paths lazily.

14) Q: Can we sign out?  
A: Yes, Firebase `signOut()`; UI returns to login/registration.

15) Q: Do we support 2FA?  
A: Not currently; can extend with Firebase Multi-Factor Auth.

16) Q: Why not OAuth providers?  
A: Scope/time; email/password meets current requirements.

17) Q: How are errors communicated?  
A: Toasts with human-readable summaries; logs for developers.

18) Q: Any analytics on auth?  
A: Optional; can add screen events (not PII) later.

19) Q: How do we avoid memory leaks?  
A: Use Activity/Fragment lifecycle; no long-lived references to context.

20) Q: Localization for auth errors?  
A: Strings externalized; errors mapped to localized messages.

---

## Dashboard (Owner: Deepthi)

1) Q: What’s shown on the dashboard?  
A: Income, expense, balance totals; recent income/expense lists; expense chart with daily/monthly toggle.

2) Q: Where is it implemented?  
A: `DashboardFragment.java`, UI in `fragment_dashboard.xml`.

3) Q: How do we sort entries by date?  
A: Store `timestamp` per entry; query with `orderByChild("timestamp")`.

4) Q: How are charts populated?  
A: Read expense data, group by day or month, then render `BarDataSet`.

5) Q: How do we prevent blank chart gaps?  
A: Toggle chart visibility vs. empty-state text depending on data.

6) Q: How do we reduce load time?  
A: Use `limitToLast`, cache via `keepSynced(true)`, and single-value listeners for charts.

7) Q: How are totals computed?  
A: Sum amounts client-side from recent data snapshots.

8) Q: How do we prevent double counting?  
A: Distinct entries keyed by unique push IDs.

9) Q: How do we keep UI responsive?  
A: Lightweight listeners; chart rendering on UI thread after data preparation.

10) Q: How are category totals updated?  
A: Atomic Firebase Transactions increment totals per category path.

11) Q: What happens if there’s no data?  
A: Show empty-state message; no crash.

12) Q: How do we format currency?  
A: Locale-aware formatting on display; store as integers.

13) Q: Can we switch daily/monthly on the fly?  
A: Yes, chip listeners reload chart grouping.

14) Q: How do we handle legacy date formats?  
A: Fallback parsing; drive chart order via canonical `timestamp`.

15) Q: Any pagination for lists?  
A: Currently `limitToLast(50)`; can extend to full pagination.

16) Q: How do we avoid heavy redraws?  
A: Single dataset updates and `invalidate()` post-binding.

17) Q: How do we test correctness?  
A: Cross-check aggregated sums vs. raw totals.

18) Q: What if Firebase fails?  
A: onCancelled/Failure listeners display user-friendly status.

19) Q: How are animations used?  
A: `animateY` for chart; FAB open/close animations.

20) Q: Accessibility considerations?  
A: Text contrast, content descriptions, larger tap targets for FAB.

---

## Show Income & Expense (Owner: Akhil)

1) Q: How are lists rendered?  
A: `RecyclerView` with `FirebaseRecyclerAdapter` bound to per-user paths.

2) Q: What is the default order?  
A: Most recent first (`reverse` + `stackFromEnd`).

3) Q: What fields are shown?  
A: Type, note, date, and amount (expense prefixed visually as negative).

4) Q: How do we open edit/delete?  
A: Tap a list item to open prefilled dialog.

5) Q: How do we prevent invalid updates?  
A: Validate amount (>0) and type (non-empty) with inline errors.

6) Q: What happens on delete?  
A: Confirmation dialog; on success, show toast; on fail, error toast.

7) Q: How do we avoid crashes on null keys?  
A: Guard for null `post_key` with friendly message.

8) Q: How do we handle long notes?  
A: Truncate or wrap in UI; limit input length at form level.

9) Q: How are empty states handled?  
A: Lists allow showing guidance text when no data.

10) Q: How do we reflect changes immediately?  
A: Firebase adapter updates real-time; RecyclerView refreshes.

11) Q: Are list items clickable areas large enough?  
A: Row views are full-width with adequate padding.

12) Q: How’s performance with many items?  
A: Efficient binding and limited initial query size.

13) Q: Can we filter by category?  
A: Search screens support filtering; future: list-level filters.

14) Q: Do we show dates uniformly?  
A: Yes, consistent `MMM dd, yyyy` presentation.

15) Q: How do we ensure stable amounts?  
A: Integers stored; conversion only on display.

16) Q: How are adapters cleaned up?  
A: Lifecycle-managed by Fragment; GC handles.

17) Q: How to add pagination?  
A: Use query cursors (`startAt`/`endAt`) or incremental `limitToLast`.

18) Q: Error visibility?  
A: Toasts for user; logs for devs.

19) Q: How do we test edits?  
A: Verify field updates in database and UI binding refresh.

20) Q: Any row actions?  
A: Primary action is tap-to-edit; can extend to swipe actions.

---

## Analytics (Owner: Pankaj)

1) Q: Which libraries?  
A: MPAndroidChart for BarChart (and PieChart in analytics screen).

2) Q: Why BarChart for dashboard?  
A: Clear comparison of totals across time buckets.

3) Q: Bucket logic?  
A: Group by day (Mon–Sun) or month (`MMM yyyy`), sorted by parsed date.

4) Q: Data source?  
A: Firebase user-scoped paths; snapshot processed to entries.

5) Q: Mixed date handling?  
A: Fallback parsers; canonical `timestamp` guides order.

6) Q: Label formatting?  
A: `IndexAxisValueFormatter` for X-axis labels; rotation for readability.

7) Q: Value formatting?  
A: Currency-like integer values with value text enabled.

8) Q: Legend and description?  
A: Enabled/disabled per chart; concise description text.

9) Q: Performance optimizations?  
A: Single dataset mutation, limited queries, and debounced reload on toggle.

10) Q: Empty state?  
A: Chart hidden, empty message shown when no data.

11) Q: Error resilience?  
A: Catches parsing errors; chart remains stable.

12) Q: Comparisons across months?  
A: Side-by-side bars for income vs expense on Analytics page.

13) Q: Accessibility?  
A: Colors with sufficient contrast; labels readable.

14) Q: Extensibility?  
A: Additional buckets (quarter/year) can be added in the same pattern.

15) Q: Testing?  
A: Log counts of processed entries; verify datasets vs raw.

16) Q: Rendering issues?  
A: Axis bounds and grouping parameters adjusted to avoid overlap.

17) Q: Data integrity?  
A: Client-side sums verified against totals; server-side rules recommended.

18) Q: Internationalization?  
A: Month/day formats are locale-aware; labels can be localized.

19) Q: Why not line charts?  
A: Bars emphasize discrete period totals; lines better for continuous series.

20) Q: Can we export charts?  
A: MPAndroidChart supports saving bitmaps; can add action to export.

---

## Add Income & Add Expense (Owner: Sharanya)

1) Q: Where are add dialogs?  
A: Quick-add in `DashboardFragment`; full add in `IncomeFragment`/`ExpenseFragment`.

2) Q: Validations enforced?  
A: Positive integer amount; non-empty type; input length limits; whitespace normalized.

3) Q: Save button state?  
A: Disabled until form becomes valid; live-updates on text/spinner change.

4) Q: Categories?  
A: Curated lists (income/expense) via Spinner; fallback to typed type if provided.

5) Q: Error handling on write?  
A: Completion/failure callbacks with clear toasts; no crashes on network errors.

6) Q: Sorting after add?  
A: Entries include `timestamp`; lists use `orderByChild("timestamp")`.

7) Q: Date stored?  
A: Human-readable `date` and machine-sortable `timestamp`.

8) Q: Category totals?  
A: Firebase Transactions increment per-category totals.

9) Q: Prevention of duplicates?  
A: Unique push keys; UI guards against double taps (disabled during write).

10) Q: Edge cases?  
A: Null key guard, invalid amount/type errors, friendly retry messages.

11) Q: Input sanitation?  
A: Trim + collapse internal spaces for type/note; length caps.

12) Q: Feedback on success?  
A: Toast “Income added” / “Expense added”.

13) Q: Pre-filling?  
A: Update dialogs show current values to minimize typing.

14) Q: Accessibility?  
A: Labels, hints, and large touch targets.

15) Q: Localization?  
A: Strings externalized; category labels can be localized.

16) Q: Testing strategy?  
A: Attempt invalid inputs to verify gating; confirm DB write on success.

17) Q: Undo on add?  
A: Not present; can add Snackbar undo in future.

18) Q: Duplicate categories?  
A: Totals keyed by category string; normalize labels to avoid drift.

19) Q: Keyboard handling?  
A: Dismiss keyboard after save/cancel; maintain focus order.

20) Q: Offline add?  
A: Firebase queues writes; success toast on completion when back online.

---

## Backend, Menu, About, Search, Profile, Password, Delete (Owner: Chirag)

1) Q: Why Realtime Database?  
A: Real-time sync, low ops, offline cache.

2) Q: Transaction schema?  
A: `{ amount, type, note, id, date, timestamp }`.

3) Q: Category totals?  
A: `/IncomeCategoryTotals/{uid}/{cat}`, `/ExpenseCategoryTotals/{uid}/{cat}` with transactions.

4) Q: Security model?  
A: Client scoping; recommend strict Firebase Rules for `auth.uid` and field validations.

5) Q: Where is data access coded?  
A: Fragments with per-user references; can be refactored into repository layer.

6) Q: Search implementation?  
A: `searchdata.java`, `searchdata2.java` (type/keyword). Future: indexed/prefix search.

7) Q: Menu & navigation?  
A: `nav_menu.xml`, Android Navigation UI; app bar layouts.

8) Q: About screen?  
A: `about.java` and `activity_about.xml` with project info.

9) Q: Profile data?  
A: `Profile.java`, optional Storage path `/ProfileImages/{uid}`.

10) Q: Password flows?  
A: `change_password.java`, `resetpassword.java`.

11) Q: Account deletion?  
A: Remove user in Auth; optionally purge user data nodes.

12) Q: Performance knobs?  
A: `keepSynced(true)`, limited queries, single-value chart loads.

13) Q: Error strategy?  
A: User toasts + dev logs; guarded DB access to avoid NPEs.

14) Q: Backup/export?  
A: CSV export planned; Storage for files.

15) Q: Internationalization?  
A: Strings externalized; date/currency locale-aware.

16) Q: Theming?  
A: Material components; can upgrade to Material 3 with dark mode.

17) Q: Testing?  
A: Unit tests for aggregations and validation; UI tests for flows (future).

18) Q: Observability?  
A: Optional Timber logging; non-PII analytics can be added.

19) Q: Offline-first posture?  
A: Local cache; queued writes; graceful errors.

20) Q: Migration path?  
A: Firestore for complex queries or Room for richer offline.

---

## Export Notes

- To PDF: Open this file in Android Studio/VSCode → Print/Export to PDF.
- To DOCX: Use an online Markdown-to-DOCX converter or Pandoc (`pandoc PRESENTATION_QA.md -o PRESENTATION_QA.docx`).
