import Shared
import SwiftUI

@main
struct iOSApp: App {
    init() {
        ResumeEntry.shared.doInit(config: NetworkConfig(host: AppConfig.live.resumeDataHost))
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
struct AppConfig: Sendable {
    let resumeDataHost: String
    let resumeShareUrl: String
    let repoUrl: String

    static let live = AppConfig(
        resumeDataHost: infoString("RESUME_DATA_HOST"),
        resumeShareUrl: infoString("RESUME_SHARE_URL"),
        repoUrl: infoString("RESUME_REPO_URL")
    )

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
