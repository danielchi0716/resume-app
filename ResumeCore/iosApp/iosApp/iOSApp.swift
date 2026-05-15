import Shared
import SwiftUI

@main
struct iOSApp: App {
    init() {
        ResumeEntry.shared.doInit(config: NetworkConfig(host: AppConfig.resumeDataHost))
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}

/// Build-time configuration injected via xcconfig → Info.plist.
/// Source of truth lives in env vars (CI) or ResumeCore/local.properties (local),
/// rendered into Configuration/AppConfig.xcconfig by scripts/render-appconfig.sh.
enum AppConfig {
    static let resumeDataHost = infoString("RESUME_DATA_HOST")
    static let resumeShareUrl = infoString("RESUME_SHARE_URL")
    static let repoUrl = infoString("RESUME_REPO_URL")

    private static func infoString(_ key: String) -> String {
        guard let value = Bundle.main.object(forInfoDictionaryKey: key) as? String,
              !value.isEmpty
        else {
            assertionFailure("Missing Info.plist entry for \(key). Run scripts/render-appconfig.sh.")
            return ""
        }
        return value
    }
}
