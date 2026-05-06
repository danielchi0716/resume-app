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

private func tr(_ key: String) -> String {
    NSLocalizedString(key, comment: "")
}

private func trf(_ key: String, _ args: CVarArg...) -> String {
    String(format: NSLocalizedString(key, comment: ""), arguments: args)
}

/// Localized strings — every accessor goes through `Localizable.xcstrings`.
/// The active language is whatever iOS picks from the user's per-app or
/// system language settings; this enum doesn't make that decision.
enum L10n {
    // Tab labels
    static var tabProfile: String { tr("tab.profile") }
    static var tabWork:    String { tr("tab.work") }
    static var tabSkills:  String { tr("tab.skills") }
    static var tabMore:    String { tr("tab.more") }

    // Nav titles
    static var titleProfile: String { tr("nav.profile") }
    static var titleWork:    String { tr("nav.work") }
    static var titleSkills:  String { tr("nav.skills") }
    static var titleMore:    String { tr("nav.more") }

    // Sections
    static var sectionAbout:        String { tr("section.about") }
    static var sectionLanguages:    String { tr("section.languages") }
    static var sectionEducation:    String { tr("section.education") }
    static var sectionWork:         String { tr("section.work") }
    static var sectionMainProjects: String { tr("section.main_projects") }
    static var sectionSideProjects: String { tr("section.side_projects") }
    static var sectionContact:      String { tr("section.contact") }

    // Stats
    static var statTotalYears: String { tr("stat.total_years") }
    static var statCompanies:  String { tr("stat.companies") }
    static var statProjects:   String { tr("stat.projects") }

    // Work
    static var actionViewDetail: String { tr("work.view_detail") }
    static var workDetailTitle:  String { tr("work.detail_title") }
    static var swipeHint:        String { tr("work.swipe_hint") }

    // Period
    static var present: String { tr("period.present") }
    static func durationYearsMonths(_ y: Int, _ m: Int) -> String {
        trf("duration.years_months", y, m)
    }
    static func durationYears(_ y: Int) -> String  { trf("duration.years", y) }
    static func durationMonths(_ m: Int) -> String { trf("duration.months", m) }

    // CTA
    static var ctaThanks:   String { tr("cta.thanks") }
    static var ctaSubtitle: String { tr("cta.subtitle") }
    static var ctaBridge:   String { tr("cta.bridge") }

    // Platforms
    static var platformAndroid: String { tr("platform.android") }
    static var platformIOS:     String { tr("platform.ios") }
    static var platformWeb:     String { tr("platform.web") }
    static var platformWebDesc: String { tr("platform.web_desc") }

    // Action sheet
    static var sheetTitle:   String { tr("sheet.title") }
    static var themeLight:   String { tr("theme.light") }
    static var themeDark:    String { tr("theme.dark") }
    static var themeSystem:  String { tr("theme.system") }
    static var openWeb:      String { tr("sheet.open_web") }
    static var openSettings: String { tr("sheet.open_settings") }
    static var cancel:       String { tr("sheet.cancel") }

    // Contacts
    static var labelEmail:    String { tr("contact.email") }
    static var labelPhone:    String { tr("contact.phone") }
    static var labelGithub:   String { tr("contact.github") }
    static var labelLinkedin: String { tr("contact.linkedin") }

    // Loading / error
    static var errorTitle: String { tr("error.title") }
    static var errorRetry: String { tr("error.retry") }
    static var loading:    String { tr("state.loading") }

    // Footer
    static var footerNote: String { tr("footer.note") }

    // Skills summary (count + count)
    static func skillsSummary(_ areas: Int, _ skills: Int) -> String {
        trf("skills.summary", areas, skills)
    }
}
