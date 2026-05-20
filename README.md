# 紀賀元 Daniel Chi · Cross-Platform Resume

跨平台履歷展示專案 —— 同一份履歷資料以三種 client 呈現:Android app、iOS app、靜態網頁。

## 架構

```
resume-app/
├── ResumeCore/             # Kotlin Multiplatform 主專案
│   ├── shared/             # commonMain 模型 + Ktor API + Koin DI
│   ├── composeApp/         # Android app (Jetpack Compose + Material 3 + Hilt)
│   └── iosApp/             # iOS app (SwiftUI + Skie 橋接)
└── web/                    # 靜態網頁 (vanilla HTML/CSS/JS,GitHub Pages)
```

履歷資料以 JSON 形式由外部 HTTP host 提供
(`{RESUME_DATA_HOST}/data/{locale}/*.json`),三個 client 共用同一份資料、各自實作 UI。

### 技術選用

- **Shared core (KMP commonMain)** — `kotlinx-serialization`、`kotlinx-datetime`、`kotlinx-coroutines`、`Ktor` HTTP client、`Koin` DI
- **Android** — Compose Multiplatform、Material 3、Hilt、Navigation 3、Coil
- **iOS** — SwiftUI、[Skie](https://skie.touchlab.co/) 自動產生 Swift bridge
- **Web** — 純 HTML/CSS/JS,無框架

## 設定

所有 build 都需要以下三個變數,以環境變數或 `ResumeCore/local.properties` 提供:

| Key | 說明 |
|---|---|
| `RESUME_DATA_HOST` | 提供履歷 JSON 的 host(例:`resume-data.example.com`) |
| `RESUME_SHARE_URL` | Share intent 對外 URL |
| `RESUME_REPO_URL` | Repo URL(顯示在 More 頁面) |

`ResumeCore/local.properties` 範例(此檔**不入版控**):

```properties
sdk.dir=/path/to/Android/sdk

RESUME_DATA_HOST=resume-data.danielchi0716.workers.dev
RESUME_SHARE_URL=https://resume.danielchi0716.workers.dev/
RESUME_REPO_URL=https://github.com/danielchi0716/resume-app
```

解析順序:**環境變數優先,其次讀 `local.properties`**;本機開發用後者。
CI 不讀此檔 —— `RESUME_DATA_HOST` / `RESUME_SHARE_URL` 由 repo Variables
(`${{ vars.RESUME_* }}`)注入,`RESUME_REPO_URL` 由 GitHub context
(`${{ github.server_url }}/${{ github.repository }}`)推導。

## 建置

### Android

```bash
cd ResumeCore
./gradlew :composeApp:assembleDebug
# APK 產出於 composeApp/build/outputs/apk/debug/
```

Release 簽章另需以下變數(env 或 `local.properties`):

| Key | 說明 |
|---|---|
| `ANDROID_KEYSTORE_PATH` | Keystore 檔案路徑 |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore 密碼 |
| `ANDROID_KEY_ALIAS` | Key alias |
| `ANDROID_KEY_PASSWORD` | Key 密碼 |
| `ANDROID_VERSION_CODE` / `ANDROID_VERSION_NAME` | 選填,未提供時用本機預設 |

### iOS

```bash
cd ResumeCore/iosApp

# 1. 從 template 產生 AppConfig.xcconfig(Xcode build 需要)
./scripts/render-appconfig.sh

# 2. 開啟 Xcode 執行
open iosApp.xcodeproj
```

`AppConfig.xcconfig` 由 template 渲染產出(讀同樣的三個 `RESUME_*` 變數),不入版控。修改設定值後重跑腳本即可。

### Web

```bash
cd web
./serve.sh           # 預設 port 8000
# ./serve.sh 9000    # 自訂 port
```

`serve.sh` 會先呼叫 `scripts/render-config.sh` 把 `js/config.template.js` 渲染成 `js/config.js`,再啟動 Python 的 SimpleHTTPServer。瀏覽器打開:

- 繁中:`http://localhost:8000/index.html`
- English:`http://localhost:8000/index_en.html`

Web 額外需要 `RESUME_VERSION` 變數(本機開發預設 `local-dev`,CI 用 git 計算的版本號)。

## CI / Release

`.github/workflows/deploy.yml`(`workflow_dispatch` 手動觸發):

1. 計算版本號(`YYYY.MM.DD-{sha}`,version code 用 commit 數)
2. 三個平台**並行建置** —— Web tarball、Android release APK、iOS unsigned IPA + Simulator app
3. 任一平台失敗則跳過後續發布(三平台保持版本一致)
4. Web 部署到 GitHub Pages
5. 建立 / 更新 GitHub Release,附上 APK / IPA / Simulator zip,自動產生 changelog

CI 變數:`RESUME_*` 來自 repo Variables、Android keystore 來自 Secrets。
