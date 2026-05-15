import FactoryKit
import Shared

extension Container {
    /// User-resolved locale, derived from `Bundle.main.preferredLocalizations`
    /// so it stays aligned with NSLocalizedString / String Catalog selection.
    var sharedLocale: Factory<Shared.Locale> {
        self {
            let lang = (Bundle.main.preferredLocalizations.first ?? "en").lowercased()
            return lang.hasPrefix("zh") ? .traditionalchinese : .english
        }
    }

    /// KMP `ResumeService`, locale-resolved per resolution.
    /// `MainActor.assumeIsolated` is safe because all `@Injected` accesses
    /// happen on MainActor view models.
    var resumeService: Factory<ResumeService> {
        self {
            MainActor.assumeIsolated {
                IosBridgeKt.resumeService(locale: Container.shared.sharedLocale())
            }
        }
    }
}
