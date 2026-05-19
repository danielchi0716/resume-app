import Foundation
import Shared

extension Period {
    /// Calendar-correct (year, month) delta from `start` to `end` (or now if open).
    func toDateComponents() -> DateComponents {
        Calendar(identifier: .gregorian).dateComponents(
            [.year, .month],
            from: start.toDate,
            to: end?.toDate ?? Date()
        )
    }

    /// Half-open date range suitable for `Range<Date>.formatted(.components(...))`.
    /// Guarantees `upper > lower` even when start equals end (or end < start) by
    /// nudging upper forward 1s; the formatter rounds to the requested fields.
    var dateRange: Range<Date> {
        let s = start.toDate
        let e = end?.toDate ?? Date()
        return s..<max(s.addingTimeInterval(1), e)
    }
}

extension Sequence where Element == Period {
    var totalYearsApprox: String {
        let months = reduce(0) { acc, p in
            let c = p.toDateComponents()
            return acc + (c.year ?? 0) * 12 + (c.month ?? 0)
        }
        return "\(months / 12)+"
    }
}
