import SwiftUI

enum HIGScheme {
    case light
    case dark
}

struct HIGColors {
    let tint: Color
    let tintPressed: Color

    let label: Color
    let secondaryLabel: Color
    let tertiaryLabel: Color
    let quaternaryLabel: Color

    let systemFill: Color
    let secondarySystemFill: Color
    let tertiarySystemFill: Color
    let quaternarySystemFill: Color

    let systemGroupedBackground: Color
    let secondarySystemGroupedBackground: Color
    let tertiarySystemGroupedBackground: Color

    let systemBackground: Color
    let secondarySystemBackground: Color

    let separator: Color
    let opaqueSeparator: Color

    let glassFill: Color
    let glassBorder: Color
    let glassShineLight: Color
    let glassShineDark: Color

    let red: Color
    let orange: Color
    let yellow: Color
    let green: Color
    let mint: Color
    let teal: Color
    let cyan: Color
    let blue: Color
    let indigo: Color
    let purple: Color
    let pink: Color
    let brown: Color
}

extension HIGColors {
    static let light = HIGColors(
        tint: Color(hex: 0x30B0C7),
        tintPressed: Color(hex: 0x26889A),
        label: .black,
        secondaryLabel: Color(red: 60/255, green: 60/255, blue: 67/255).opacity(0.60),
        tertiaryLabel: Color(red: 60/255, green: 60/255, blue: 67/255).opacity(0.30),
        quaternaryLabel: Color(red: 60/255, green: 60/255, blue: 67/255).opacity(0.18),
        systemFill: Color(red: 120/255, green: 120/255, blue: 128/255).opacity(0.20),
        secondarySystemFill: Color(red: 120/255, green: 120/255, blue: 128/255).opacity(0.16),
        tertiarySystemFill: Color(red: 118/255, green: 118/255, blue: 128/255).opacity(0.12),
        quaternarySystemFill: Color(red: 116/255, green: 116/255, blue: 128/255).opacity(0.08),
        systemGroupedBackground: Color(hex: 0xF2F2F7),
        secondarySystemGroupedBackground: .white,
        tertiarySystemGroupedBackground: Color(hex: 0xF2F2F7),
        systemBackground: .white,
        secondarySystemBackground: Color(hex: 0xF2F2F7),
        separator: Color(red: 60/255, green: 60/255, blue: 67/255).opacity(0.29),
        opaqueSeparator: Color(hex: 0xC6C6C8),
        glassFill: Color.white.opacity(0.50),
        glassBorder: Color.black.opacity(0.06),
        glassShineLight: Color.white.opacity(0.7),
        glassShineDark: Color.white.opacity(0.4),
        red: Color(hex: 0xFF3B30),
        orange: Color(hex: 0xFF9500),
        yellow: Color(hex: 0xFFCC00),
        green: Color(hex: 0x34C759),
        mint: Color(hex: 0x00C7BE),
        teal: Color(hex: 0x30B0C7),
        cyan: Color(hex: 0x32ADE6),
        blue: Color(hex: 0x007AFF),
        indigo: Color(hex: 0x5856D6),
        purple: Color(hex: 0xAF52DE),
        pink: Color(hex: 0xFF2D55),
        brown: Color(hex: 0xA2845E)
    )

    static let dark = HIGColors(
        tint: Color(hex: 0x5DD6E5),
        tintPressed: Color(hex: 0x3FB7C5),
        label: .white,
        secondaryLabel: Color(red: 235/255, green: 235/255, blue: 245/255).opacity(0.60),
        tertiaryLabel: Color(red: 235/255, green: 235/255, blue: 245/255).opacity(0.30),
        quaternaryLabel: Color(red: 235/255, green: 235/255, blue: 245/255).opacity(0.16),
        systemFill: Color(red: 120/255, green: 120/255, blue: 128/255).opacity(0.36),
        secondarySystemFill: Color(red: 120/255, green: 120/255, blue: 128/255).opacity(0.32),
        tertiarySystemFill: Color(red: 118/255, green: 118/255, blue: 128/255).opacity(0.24),
        quaternarySystemFill: Color(red: 118/255, green: 118/255, blue: 128/255).opacity(0.18),
        systemGroupedBackground: .black,
        secondarySystemGroupedBackground: Color(hex: 0x1C1C1E),
        tertiarySystemGroupedBackground: Color(hex: 0x2C2C2E),
        systemBackground: .black,
        secondarySystemBackground: Color(hex: 0x1C1C1E),
        separator: Color(red: 84/255, green: 84/255, blue: 88/255).opacity(0.65),
        opaqueSeparator: Color(hex: 0x38383A),
        glassFill: Color(red: 120/255, green: 120/255, blue: 128/255).opacity(0.28),
        glassBorder: Color.white.opacity(0.15),
        glassShineLight: Color.white.opacity(0.15),
        glassShineDark: Color.white.opacity(0.08),
        red: Color(hex: 0xFF453A),
        orange: Color(hex: 0xFF9F0A),
        yellow: Color(hex: 0xFFD60A),
        green: Color(hex: 0x30D158),
        mint: Color(hex: 0x63E6E2),
        teal: Color(hex: 0x40CBE0),
        cyan: Color(hex: 0x64D2FF),
        blue: Color(hex: 0x0A84FF),
        indigo: Color(hex: 0x5E5CE6),
        purple: Color(hex: 0xBF5AF2),
        pink: Color(hex: 0xFF375F),
        brown: Color(hex: 0xAC8E68)
    )
}

extension Color {
    init(hex: UInt32) {
        let r = Double((hex >> 16) & 0xFF) / 255.0
        let g = Double((hex >> 8) & 0xFF) / 255.0
        let b = Double(hex & 0xFF) / 255.0
        self.init(red: r, green: g, blue: b)
    }

    func mixed(with other: Color, fraction: Double) -> Color {
        let f = max(0, min(1, fraction))
        let lhs = UIColor(self).cgColor.components ?? [0, 0, 0, 1]
        let rhs = UIColor(other).cgColor.components ?? [0, 0, 0, 1]
        let r = lhs[0] * (1 - f) + rhs[0] * f
        let g = lhs[1] * (1 - f) + rhs[1] * f
        let b = lhs[2] * (1 - f) + rhs[2] * f
        let a = (lhs.count > 3 ? lhs[3] : 1) * (1 - f) + (rhs.count > 3 ? rhs[3] : 1) * f
        return Color(red: r, green: g, blue: b, opacity: a)
    }
}

enum HIGType {
    static let largeTitle  = Font.system(size: 34, weight: .bold)
    static let title1      = Font.system(size: 28, weight: .bold)
    static let title2      = Font.system(size: 22, weight: .bold)
    static let title3      = Font.system(size: 20, weight: .semibold)
    static let headline    = Font.system(size: 17, weight: .semibold)
    static let body        = Font.system(size: 17, weight: .regular)
    static let bodyEmph    = Font.system(size: 17, weight: .semibold)
    static let callout     = Font.system(size: 16, weight: .regular)
    static let subheadline = Font.system(size: 15, weight: .regular)
    static let subheadlineEmph = Font.system(size: 15, weight: .semibold)
    static let footnote    = Font.system(size: 13, weight: .regular)
    static let footnoteEmph = Font.system(size: 13, weight: .semibold)
    static let caption1    = Font.system(size: 12, weight: .regular)
    static let caption1Emph = Font.system(size: 12, weight: .semibold)
    static let caption2    = Font.system(size: 11, weight: .regular)
}

enum HIGRadius {
    static let xs: CGFloat = 6
    static let sm: CGFloat = 10
    static let md: CGFloat = 14
    static let lg: CGFloat = 22
    static let xl: CGFloat = 26
}

enum ThemePreference: String, CaseIterable, Identifiable {
    case system, light, dark
    var id: String { rawValue }
}

private struct HIGColorsKey: EnvironmentKey {
    static let defaultValue: HIGColors = .light
}

private struct HIGSchemeKey: EnvironmentKey {
    static let defaultValue: HIGScheme = .light
}

extension EnvironmentValues {
    var hig: HIGColors {
        get { self[HIGColorsKey.self] }
        set { self[HIGColorsKey.self] = newValue }
    }

    var higScheme: HIGScheme {
        get { self[HIGSchemeKey.self] }
        set { self[HIGSchemeKey.self] = newValue }
    }
}

struct HIGEnvironment: ViewModifier {
    let preference: ThemePreference

    @Environment(\.colorScheme) private var systemScheme

    private var resolvedScheme: HIGScheme {
        switch preference {
        case .system: return systemScheme == .dark ? .dark : .light
        case .light:  return .light
        case .dark:   return .dark
        }
    }

    func body(content: Content) -> some View {
        let scheme = resolvedScheme
        let palette: HIGColors = scheme == .dark ? .dark : .light
        content
            .environment(\.hig, palette)
            .environment(\.higScheme, scheme)
            .preferredColorScheme(preference == .system ? nil : (scheme == .dark ? .dark : .light))
    }
}

extension View {
    func higTheme(_ preference: ThemePreference) -> some View {
        modifier(HIGEnvironment(preference: preference))
    }
}
