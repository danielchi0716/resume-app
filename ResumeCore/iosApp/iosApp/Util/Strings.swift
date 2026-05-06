import Foundation

enum AppLocale: String {
    case zh
    case en

    static var current: AppLocale {
        let langs = Locale.preferredLanguages.first ?? ""
        return langs.lowercased().hasPrefix("zh") ? .zh : .en
    }

    var sharedLocaleCode: String {
        switch self {
        case .zh: return "tc"
        case .en: return "en"
        }
    }
}

enum L10n {
    static let isChinese = AppLocale.current == .zh

    private static func t(_ zh: String, _ en: String) -> String {
        isChinese ? zh : en
    }

    // Tab labels
    static var tabProfile: String  { t("個人", "Profile") }
    static var tabWork: String     { t("經歷", "Work") }
    static var tabSkills: String   { t("技能", "Skills") }
    static var tabMore: String     { t("更多", "More") }

    // Nav titles
    static var titleProfile: String { t("個人資料", "Profile") }
    static var titleWork: String    { t("工作經歷", "Work") }
    static var titleSkills: String  { t("專業技能", "Skills") }
    static var titleMore: String    { t("更多",     "More") }

    // Sections
    static var sectionAbout: String        { t("關於",      "About") }
    static var sectionLanguages: String    { t("語言能力",  "Languages") }
    static var sectionEducation: String    { t("學歷",      "Education") }
    static var sectionWork: String         { t("工作經歷",  "Experience") }
    static var sectionMainProjects: String { t("主要專案",  "Key Projects") }
    static var sectionSideProjects: String { t("個人專案",  "Side Projects") }
    static var sectionContact: String      { t("聯絡我 · Get in Touch", "Get in Touch") }

    // Stats
    static var statTotalYears: String { t("總年資", "Years") }
    static var statCompanies: String  { t("間公司", "Companies") }
    static var statProjects: String   { t("主要專案", "Projects") }

    // Work
    static var actionViewDetail: String { t("查看詳細", "View detail") }
    static var workDetailTitle: String  { t("工作經歷", "Work") }
    static var swipeHint: String        { t("左右滑動切換", "Swipe to switch") }

    // Period
    static var present: String      { t("至今", "Present") }
    static func durationYearsMonths(_ y: Int, _ m: Int) -> String {
        isChinese ? "\(y) 年 \(m) 個月" : "\(y)y \(m)m"
    }
    static func durationYears(_ y: Int) -> String  { isChinese ? "\(y) 年" : "\(y)y" }
    static func durationMonths(_ m: Int) -> String { isChinese ? "\(m) 個月" : "\(m)m" }

    // CTA
    static var ctaThanks: String   { t("感謝閱讀到這裡", "Thanks for reading") }
    static var ctaSubtitle: String { t("一份內容、三個平台。Android 與 iOS 共用 KMP 核心，Web 各自獨立呈現。",
                                       "Three platforms, one resume — Android and iOS share a KMP core, Web stands alone.") }
    static var ctaBridge: String   { t("想進一步交流？任選一個方式聯絡我 ↓", "Want to chat? Pick a channel below ↓") }

    static var platformAndroid: String { t("Android", "Android") }
    static var platformIOS: String     { t("iOS",     "iOS") }
    static var platformWeb: String     { t("Web",     "Web") }
    static var platformWebDesc: String { t("響應式網頁版", "Responsive web") }

    // Action sheet
    static var sheetTitle: String  { t("外觀與選項", "Appearance") }
    static var themeLight: String  { t("淺色 Light", "Light") }
    static var themeDark: String   { t("深色 Dark", "Dark") }
    static var themeSystem: String { t("跟隨系統", "Follow system") }
    static var openWeb: String     { t("Web 版本", "Web version") }
    static var cancel: String      { t("取消", "Cancel") }

    // Contacts
    static var labelEmail: String    { t("Email", "Email") }
    static var labelPhone: String    { t("電話",  "Phone") }
    static var labelGithub: String   { t("GitHub","GitHub") }
    static var labelLinkedin: String { t("LinkedIn","LinkedIn") }

    // Loading / error
    static var errorTitle: String { t("載入失敗", "Failed to load") }
    static var errorRetry: String { t("重試", "Retry") }
    static var loading: String    { t("載入中…", "Loading…") }

    // Footer
    static var footerNote: String { "Made with HIG · iOS 26 Liquid Glass" }
}
