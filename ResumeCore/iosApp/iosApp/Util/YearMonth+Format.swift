import Foundation
import Shared

typealias YearMonth = Kotlinx_datetimeYearMonth

extension Kotlinx_datetimeYearMonth {
    /// Anchored to the 1st day of the month at 00:00 in the gregorian calendar.
    var toDate: Date {
        Calendar(identifier: .gregorian).date(
            from: DateComponents(year: Int(year), month: Int(month.ordinal) + 1)
        ) ?? Date()
    }

    /// Locale-native abbreviated month + year, e.g. "Mar 2020" (en) or
    /// "2020年3月" (zh-Hant). Format follows the system locale via
    /// `Date.FormatStyle`; no manual locale detection.
    var formatted: String {
        toDate.formatted(.dateTime.year().month(.abbreviated))
    }
}
