import FactoryKit
import Foundation
import Shared

extension Container {
    var appConfig: Factory<AppConfig> {
        self { .live }
    }

    var sharedLocale: Factory<Shared.Locale> {
        self {
            let lang = (Bundle.main.preferredLocalizations.first ?? "en").lowercased()
            return lang.hasPrefix("zh") ? .traditionalChinese : .english
        }
    }

    var resumeService: Factory<ResumeService> {
        self {
            ResumeCore().service(locale: self.sharedLocale())
        }
    }
}
