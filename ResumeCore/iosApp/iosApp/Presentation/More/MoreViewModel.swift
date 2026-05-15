import Foundation
import Observation
import Shared

@MainActor
@Observable
final class MoreViewModel {
    var sideProjects: LoadState<[SideProject]> = .loading
    /// MoreView reads `header.contacts` for the contact buttons.
    var header: LoadState<Header> = .loading

    @ObservationIgnored private var didLoad = false

    private var service: ResumeService { ResumeServiceProvider.current }

    func loadAll() {
        guard !didLoad else { return }
        didLoad = true
        Task { await loadSideProjects() }
        Task { await loadHeader() }
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

    func loadHeader() async {
        header = .loading
        do {
            header = .ready(try await service.getHeader())
        } catch {
            header = .error(error.localizedDescription)
        }
    }
}
