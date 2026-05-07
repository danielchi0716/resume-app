import SwiftUI
import Shared

struct WorkView: View {
    @ObservedObject var store: ResumeStore
    @State private var detailIndex: Int? = nil
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                resolveLoadState(store.work, retry: { Task { await store.loadWork() } }) { jobs in
                    StatStrip(jobs: jobs)
                    SectionHeaderText(L10n.sectionWork)
                    VStack(spacing: 12) {
                        ForEach(Array(jobs.enumerated()), id: \.offset) { idx, job in
                            WorkCard(job: job, idx: idx) { detailIndex = idx }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                }
            }
        }
        .background(hig.systemGroupedBackground.ignoresSafeArea())
        .navigationDestination(item: $detailIndex) { idx in
            if case .ready(let jobs) = store.work {
                WorkDetailView(jobs: jobs, initialIndex: idx)
            }
        }
    }
}

private struct StatStrip: View {
    let jobs: [WorkExperience]
    @Environment(\.hig) private var hig

    private var stats: [(String, String)] {
        let years = PeriodFormatter.totalYearsApprox(jobs.map { $0.period })
        let projectsCount = jobs.reduce(0) { $0 + Int($1.projects.count) }
        return [
            (years, L10n.statTotalYears),
            ("\(jobs.count)", L10n.statCompanies),
            ("\(projectsCount)+", L10n.statProjects)
        ]
    }

    var body: some View {
        HStack {
            ForEach(Array(stats.enumerated()), id: \.offset) { _, stat in
                VStack(spacing: 2) {
                    Text(stat.0)
                        .font(HIGType.title1)
                        .foregroundColor(hig.tint)
                    Text(stat.1)
                        .font(HIGType.caption1)
                        .foregroundColor(hig.secondaryLabel)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .padding(18)
        .background(
            hig.secondarySystemGroupedBackground,
            in: RoundedRectangle(cornerRadius: 22, style: .continuous)
        )
        .padding(.horizontal, 16)
        .padding(.top, 4)
        .padding(.bottom, 20)
    }
}

private struct WorkCard: View {
    let job: WorkExperience
    let idx: Int
    let onTap: () -> Void

    @Environment(\.hig) private var hig

    private var accent: Color { idx == 0 ? hig.tint : hig.indigo }
    private var isCurrent: Bool { job.period.end == nil }

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(hig.secondarySystemGroupedBackground)
            RadialGradient(colors: [accent.opacity(0.15), .clear],
                           center: .topTrailing, startRadius: 0, endRadius: 140)
                .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
                .allowsHitTesting(false)

            HStack(alignment: .top, spacing: 14) {
                Glyph(systemImage: idx == 0 ? "building.2.fill" : "building.fill",
                      size: 42, background: accent, radius: 10)

                VStack(alignment: .leading, spacing: 0) {
                    Text(job.title)
                        .font(HIGType.headline)
                        .foregroundColor(hig.label)
                    Text(job.company)
                        .font(HIGType.subheadline)
                        .foregroundColor(hig.secondaryLabel)
                        .padding(.top, 2)

                    HStack(spacing: 6) {
                        TagView(text: PeriodFormatter.format(job.period),
                                color: isCurrent ? hig.green : hig.secondaryLabel,
                                leadingDot: isCurrent)
                        TagView(text: PeriodFormatter.duration(job.period),
                                color: hig.secondaryLabel)
                    }
                    .padding(.top, 10)

                    if let first = (job.bullets as? [String])?.first {
                        Text(first)
                            .font(HIGType.footnote)
                            .foregroundColor(hig.secondaryLabel)
                            .lineSpacing(2)
                            .padding(.top, 12)
                    }

                    HStack(spacing: 4) {
                        Text(L10n.actionViewDetail)
                            .font(HIGType.subheadlineEmph)
                            .foregroundColor(hig.tint)
                        Image(systemName: "chevron.right")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(hig.tint)
                    }
                    .padding(.top, 12)
                }
            }
            .padding(18)
        }
        .contentShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
        .onTapGesture(perform: onTap)
    }
}
