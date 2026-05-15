import Foundation
import Observation
import Shared

@MainActor
@Observable
final class ProfileViewModel {
    var header: LoadState<Header> = .loading
    var about: LoadState<[String]> = .loading
    var languages: LoadState<[Language]> = .loading
    var education: LoadState<[Education]> = .loading

    @ObservationIgnored private var didLoad = false

    private var service: ResumeService { ResumeServiceProvider.current }

    func loadAll() {
        guard !didLoad else { return }
        didLoad = true
        Task { await loadHeader() }
        Task { await loadAbout() }
        Task { await loadLanguages() }
        Task { await loadEducation() }
    }

    func loadHeader() async {
        header = .loading
        do {
            header = .ready(try await service.getHeader())
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
}
