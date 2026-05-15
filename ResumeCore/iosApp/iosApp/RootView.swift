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
    @State private var store = ResumeStore()
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
                                    .accessibilityLabel("sheet.title")
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
        .confirmationDialog("sheet.title", isPresented: $menuOpen, titleVisibility: .visible) {
            Button("theme.light") { setTheme(.light) }
            Button("theme.dark") { setTheme(.dark) }
            Button("theme.system") { setTheme(.system) }
            Button("sheet.open_settings") {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }
            Button("sheet.open_web") {
                if let url = URL(string: AppConfig.resumeShareUrl) {
                    UIApplication.shared.open(url)
                }
            }
            Button("sheet.cancel", role: .cancel) {}
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

    private func navTitle(for tab: RootTab) -> LocalizedStringKey {
        switch tab {
        case .profile: return "nav.profile"
        case .work:    return "nav.work"
        case .skills:  return "nav.skills"
        case .more:    return "nav.more"
        }
    }

    private func setTheme(_ value: ThemePreference) {
        theme = value
        ThemePrefStore.current = value
    }
}
