import Foundation
import Shared

extension Photo {
    func resolvedURL(host: String) -> URL? {
        guard let raw = url as? String, !raw.isEmpty else { return nil }
        if raw.hasPrefix("http://") || raw.hasPrefix("https://") {
            return URL(string: raw)
        }
        return URL(string: "https://\(host)/\(raw.drop(while: { $0 == "/" }))")
    }
}
