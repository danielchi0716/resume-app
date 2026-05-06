import SwiftUI
import Shared

enum RootTab: String, CaseIterable, Identifiable {
    case profile, work, skills, more
    var id: String { rawValue }

    var label: String {
        switch self {
        case .profile: return L10n.tabProfile
        case .work:    return L10n.tabWork
        case .skills:  return L10n.tabSkills
        case .more:    return L10n.tabMore
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
    @StateObject private var store = ResumeStore()
    @State private var theme: ThemePreference = ThemePrefStore.current
    @State private var tab: RootTab = .profile
    @State private var menuOpen = false

    var body: some View {
        ZStack {
            TabView(selection: $tab) {
                ForEach(RootTab.allCases) { entry in
                    NavigationStack {
                        screen(for: entry)
                            .toolbar {
                                ToolbarItem(placement: .topBarTrailing) {
                                    Button { menuOpen = true } label: {
                                        Image(systemName: "ellipsis")
                                            .font(.system(size: 17, weight: .semibold))
                                    }
                                    .accessibilityLabel(L10n.sheetTitle)
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
        .onAppear { store.loadAll() }
        .confirmationDialog(L10n.sheetTitle, isPresented: $menuOpen, titleVisibility: .visible) {
            Button(L10n.themeLight) { setTheme(.light) }
            Button(L10n.themeDark) { setTheme(.dark) }
            Button(L10n.themeSystem) { setTheme(.system) }
            Button(L10n.openSettings) {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }
            Button(L10n.openWeb) {
                if let url = URL(string: AppConfig.resumeShareUrl) {
                    UIApplication.shared.open(url)
                }
            }
            Button(L10n.cancel, role: .cancel) {}
        }
    }

    @ViewBuilder
    private func screen(for tab: RootTab) -> some View {
        switch tab {
        case .profile: ProfileView(store: store)
        case .work:    WorkView(store: store)
        case .skills:  SkillsView(store: store)
        case .more:    MoreView(store: store)
        }
    }

    private func navTitle(for tab: RootTab) -> String {
        switch tab {
        case .profile: return L10n.titleProfile
        case .work:    return L10n.titleWork
        case .skills:  return L10n.titleSkills
        case .more:    return L10n.titleMore
        }
    }

    private func setTheme(_ value: ThemePreference) {
        theme = value
        ThemePrefStore.current = value
    }
}
