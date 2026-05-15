import Foundation
import Shared

enum ThemePrefStore {
    private static let key = "resume_theme_preference"

    static var current: ThemePreference {
        get {
            let raw = UserDefaults.standard.string(forKey: key) ?? ThemePreference.system.rawValue
            return ThemePreference(rawValue: raw) ?? .system
        }
        set { UserDefaults.standard.set(newValue.rawValue, forKey: key) }
    }
}
