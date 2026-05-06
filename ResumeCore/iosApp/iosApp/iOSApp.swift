import SwiftUI
import Shared

@main
struct iOSApp: App {

    init() {
        ResumeEntry.shared.doInit(
            config: NetworkConfig(host: AppConfig.resumeDataHost)
        )
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}

enum AppConfig {
    static let resumeDataHost = "resume-data.danielchi0716.workers.dev"
    static let resumeShareUrl = "https://resume.danielchi0716.workers.dev/"
    static let repoUrl = "https://github.com/danielchi0716/resume-app"
}
