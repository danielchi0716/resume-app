import Foundation
import Shared

private let enMonths = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
]

enum PeriodFormatter {

    static func format(_ period: Period) -> String {
        let useChinese = L10n.isChinese
        let start = period.start
        let end = period.end
        let startsInJan = start.month == 1
        let endsInDec = end?.month == 12
        if startsInJan && (endsInDec || end == nil) {
            if let end {
                return start.year == end.year
                    ? "\(start.year)"
                    : "\(start.year) ─ \(end.year)"
            }
            return "\(start.year)"
        }
        let endLabel = end.map { yearMonth($0, useChinese: useChinese) } ?? L10n.present
        return "\(yearMonth(start, useChinese: useChinese)) ─ \(endLabel)"
    }

    static func duration(_ period: Period) -> String {
        let endYM = period.end ?? nowYearMonth()
        let months = max(0, monthsBetween(period.start, endYM))
        let y = months / 12
        let m = months % 12
        switch (y, m) {
        case (0, _): return L10n.durationMonths(m)
        case (_, 0): return L10n.durationYears(y)
        default:     return L10n.durationYearsMonths(y, m)
        }
    }

    static func yearRangeShort(_ period: Period) -> String {
        if let end = period.end, end.year != period.start.year {
            return "\(period.start.year)–\(end.year)"
        }
        return "\(period.start.year)"
    }

    static func totalYearsApprox(_ periods: [Period]) -> String {
        let totalMonths = periods.map { months in
            let end = months.end ?? nowYearMonth()
            return max(0, monthsBetween(months.start, end))
        }.reduce(0, +)
        let years = totalMonths / 12
        return "\(years)+"
    }

    private static func yearMonth(_ ym: YearMonth, useChinese: Bool) -> String {
        if useChinese {
            let mm = String(format: "%02d", ym.month)
            return "\(ym.year)/\(mm)"
        }
        let idx = Int(ym.month) - 1
        let name = (idx >= 0 && idx < 12) ? enMonths[idx] : "?"
        return "\(name) \(ym.year)"
    }

    private static func monthsBetween(_ a: YearMonth, _ b: YearMonth) -> Int {
        Int((b.year - a.year) * 12 + (b.month - a.month))
    }

    private static func nowYearMonth() -> YearMonth {
        let cal = Calendar(identifier: .gregorian)
        let comps = cal.dateComponents([.year, .month], from: Date())
        return YearMonth(year: Int32(comps.year ?? 2026), month: Int32(comps.month ?? 1))
    }
}
