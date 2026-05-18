import FactoryKit
import Foundation
import Observation
import Shared

struct MoreData {
    let sideProjects: [SideProject]
    let header: Header
}

@MainActor
@Observable
final class MoreViewModel {
    var state: LoadState<MoreData> = .loading

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
            async let sideProjects = service.getSideProjects()
            async let header = service.getHeader()
            state = .ready(MoreData(
                sideProjects: try await sideProjects,
                header: try await header
            ))
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}
