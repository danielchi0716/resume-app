import FactoryKit
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
    @ObservationIgnored @Injected(\.resumeService) private var service

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
            about = .ready(try await service.getAbout())
        } catch {
            about = .error(error.localizedDescription)
        }
    }

    func loadLanguages() async {
        languages = .loading
        do {
            languages = .ready(try await service.getLanguages())
        } catch {
            languages = .error(error.localizedDescription)
        }
    }

    func loadEducation() async {
        education = .loading
        do {
            education = .ready(try await service.getEducation())
        } catch {
            education = .error(error.localizedDescription)
        }
    }
}
