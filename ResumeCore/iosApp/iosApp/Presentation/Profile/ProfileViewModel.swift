import FactoryKit
import Foundation
import Observation
import Shared

struct ProfileData {
    let header: Header
    let about: [String]
    let languages: [Language]
    let education: [Education]
}

@MainActor
@Observable
final class ProfileViewModel {
    var state: LoadState<ProfileData> = .loading

    @ObservationIgnored private var didLoad = false
    @ObservationIgnored @Injected(\.resumeService) private var service

    func loadAll() {
        guard !didLoad else { return }
        didLoad = true
        Task { await load() }
    }

    func load() async {
        state = .loading
        do {
            async let header = service.getHeader()
            async let about = service.getAbout()
            async let languages = service.getLanguages()
            async let education = service.getEducation()
            state = .ready(ProfileData(
                header: try await header,
                about: try await about,
                languages: try await languages,
                education: try await education
            ))
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}
