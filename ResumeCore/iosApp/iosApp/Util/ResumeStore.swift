import Foundation
import SwiftUI
import Shared

@MainActor
final class ResumeStore: ObservableObject {

    enum LoadState<T> {
        case loading
        case ready(T)
        case error(String)
    }

    @Published var header: LoadState<Header> = .loading
    @Published var about: LoadState<[String]> = .loading
    @Published var languages: LoadState<[Language]> = .loading
    @Published var education: LoadState<[Education]> = .loading
    @Published var work: LoadState<[WorkExperience]> = .loading
    @Published var skills: LoadState<[Skill]> = .loading
    @Published var sideProjects: LoadState<[SideProject]> = .loading
    @Published var meta: LoadState<Meta> = .loading

    private let locale: Shared.Locale = AppLocale.current == .zh ? .traditionalchinese : .english

    private var service: ResumeService {
        IosBridgeKt.resumeService(locale: locale)
    }

    func loadAll() {
        Task { await self.loadHeader() }
        Task { await self.loadAbout() }
        Task { await self.loadLanguages() }
        Task { await self.loadEducation() }
        Task { await self.loadWork() }
        Task { await self.loadSkills() }
        Task { await self.loadSideProjects() }
        Task { await self.loadMeta() }
    }

    func loadHeader() async {
        header = .loading
        do {
            let value = try await service.getHeader()
            header = .ready(value)
        } catch {
            header = .error(error.localizedDescription)
        }
    }

    func loadAbout() async {
        about = .loading
        do {
            let value = try await service.getAbout()
            about = .ready(value as? [String] ?? [])
        } catch {
            about = .error(error.localizedDescription)
        }
    }

    func loadLanguages() async {
        languages = .loading
        do {
            let value = try await service.getLanguages()
            languages = .ready(value as? [Language] ?? [])
        } catch {
            languages = .error(error.localizedDescription)
        }
    }

    func loadEducation() async {
        education = .loading
        do {
            let value = try await service.getEducation()
            education = .ready(value as? [Education] ?? [])
        } catch {
            education = .error(error.localizedDescription)
        }
    }

    func loadWork() async {
        work = .loading
        do {
            let value = try await service.getWorkExperience()
            work = .ready(value as? [WorkExperience] ?? [])
        } catch {
            work = .error(error.localizedDescription)
        }
    }

    func loadSkills() async {
        skills = .loading
        do {
            let value = try await service.getSkills()
            skills = .ready(value as? [Skill] ?? [])
        } catch {
            skills = .error(error.localizedDescription)
        }
    }

    func loadSideProjects() async {
        sideProjects = .loading
        do {
            let value = try await service.getSideProjects()
            sideProjects = .ready(value as? [SideProject] ?? [])
        } catch {
            sideProjects = .error(error.localizedDescription)
        }
    }

    func loadMeta() async {
        meta = .loading
        do {
            let value = try await service.getMeta()
            meta = .ready(value)
        } catch {
            meta = .error(error.localizedDescription)
        }
    }
}

// MARK: - Theme preference persistence
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
