import SwiftUI
import Shared

enum RootTab: String, CaseIterable, Identifiable {
    case profile, work, skills, more
    var id: String { rawValue }

    var label: LocalizedStringKey {
        switch self {
        case .profile: return "tab.profile"
        case .work:    return "tab.work"
        case .skills:  return "tab.skills"
        case .more:    return "tab.more"
        }
    }

    var systemImage: String {
        switch self {
        case .profile: return "person.crop.circle.fill"
        case .work:    return "briefcase.fill"
        case .skills:  return "chevron.left.slash.chevron.right"
        case .more:    return "square.grid.2x2.fill"
        }
    }
}

struct RootView: View {
    @State private var theme: ThemePreference = ThemePrefStore.current
    @State private var tab: RootTab = .profile

    var body: some View {
        ZStack {
            TabView(selection: $tab) {
                ForEach(RootTab.allCases) { entry in
                    NavigationStack {
                        screen(for: entry)
                            .toolbar {
                                ToolbarItem(placement: .topBarTrailing) {
                                    overflowMenu
                                }
                            }
                            .navigationTitle(navTitle(for: entry))
                    }
                    .tabItem {
                        Label(entry.label, systemImage: entry.systemImage)
                    }
                    .tag(entry)
                }
            }
            .tint(HIGColors.light.tint) // overridden via env in screens
        }
        .higTheme(theme)
        .onChange(of: theme) { _, newValue in
            ThemePrefStore.current = newValue
        }
    }

    private var overflowMenu: some View {
        Menu {
            Picker("sheet.title", selection: $theme) {
                Label("theme.light", systemImage: "sun.max").tag(ThemePreference.light)
                Label("theme.dark", systemImage: "moon").tag(ThemePreference.dark)
                Label("theme.system", systemImage: "iphone").tag(ThemePreference.system)
            }
            .pickerStyle(.inline)

            Button("sheet.open_settings", systemImage: "gear") {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }
            Button("sheet.open_web", systemImage: "safari") {
                if let url = URL(string: AppConfig.resumeShareUrl) {
                    UIApplication.shared.open(url)
                }
            }
        } label: {
            Image(systemName: "ellipsis")
                .font(.system(size: 17, weight: .semibold))
        }
        .accessibilityLabel("sheet.title")
    }

    @ViewBuilder
    private func screen(for tab: RootTab) -> some View {
        switch tab {
        case .profile: ProfileView()
        case .work:    WorkView()
        case .skills:  SkillsView()
        case .more:    MoreView()
        }
    }

    private func navTitle(for tab: RootTab) -> LocalizedStringKey {
        switch tab {
        case .profile: return "nav.profile"
        case .work:    return "nav.work"
        case .skills:  return "nav.skills"
        case .more:    return "nav.more"
        }
    }

}
