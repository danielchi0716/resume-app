import Foundation

enum LoadState<T> {
    case loading
    case ready(T)
    case error(String)
}
