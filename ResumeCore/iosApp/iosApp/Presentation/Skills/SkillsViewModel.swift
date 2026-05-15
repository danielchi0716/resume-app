import FactoryKit
import Foundation
import Observation
import Shared

@MainActor
@Observable
final class SkillsViewModel {
    var skills: LoadState<[Skill]> = .loading

    @ObservationIgnored private var didLoad = false
    @ObservationIgnored @Injected(\.resumeService) private var service

    func loadAll() {
        guard !didLoad else { return }
        didLoad = true
        Task { await loadSkills() }
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
}
