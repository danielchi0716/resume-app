import Foundation
import Observation
import Shared

@MainActor
@Observable
final class WorkViewModel {
    var work: LoadState<[WorkExperience]> = .loading

    @ObservationIgnored private var didLoad = false

    private var service: ResumeService { ResumeServiceProvider.current }

    func loadAll() {
        guard !didLoad else { return }
        didLoad = true
        Task { await loadWork() }
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
}
