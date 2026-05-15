import Foundation

/// Generic load state used by per-screen view models.
enum LoadState<T> {
    case loading
    case ready(T)
    case error(String)
}
