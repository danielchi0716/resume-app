import FactoryKit
import Foundation
import Observation
import Shared

@MainActor
@Observable
final class WorkViewModel {
    var work: LoadState<[WorkExperience]> = .loading

    @ObservationIgnored private var didLoad = false
    @ObservationIgnored @Injected(\.resumeService) private var service

    func loadAll() {
        guard !didLoad else { return }
        didLoad = true
        Task { await loadWork() }
    }

    func loadWork() async {
        work = .loading
        do {
            work = .ready(try await service.getWorkExperience())
        } catch {
            work = .error(error.localizedDescription)
        }
    }
}
