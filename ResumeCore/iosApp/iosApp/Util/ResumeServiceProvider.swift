import Foundation
import Shared

/// Single point of access to the KMP `ResumeService`, locale-resolved per call.
enum ResumeServiceProvider {
    @MainActor
    static var current: ResumeService {
        let locale: Shared.Locale = AppLocale.current == .zh ? .traditionalchinese : .english
        return IosBridgeKt.resumeService(locale: locale)
    }
}
