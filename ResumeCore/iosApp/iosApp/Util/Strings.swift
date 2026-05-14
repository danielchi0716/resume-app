import Foundation

/// User-resolved app locale, honoring iOS per-app language (Settings > [App] > Language).
/// Reads from `Bundle.main.preferredLocalizations`, which is the bundle's resolved
/// localization for the user — exactly what NSLocalizedString uses to pick strings.
enum AppLocale: String {
    case zh
    case en

    static var current: AppLocale {
        let lang = (Bundle.main.preferredLocalizations.first ?? "en").lowercased()
        return lang.hasPrefix("zh") ? .zh : .en
    }

    static var isChinese: Bool { current == .zh }

    /// Mapping to the shared module's content locale (data origin folder key).
    var sharedLocaleCode: String {
        switch self {
        case .zh: return "tc"
        case .en: return "en"
        }
    }
}
