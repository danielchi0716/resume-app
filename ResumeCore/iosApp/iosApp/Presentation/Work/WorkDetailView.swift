import SwiftUI
import Shared

struct WorkDetailView: View {
    let jobs: [WorkExperience]
    let initialIndex: Int

    @State private var page: Int
    @Environment(\.hig) private var hig

    init(jobs: [WorkExperience], initialIndex: Int) {
        self.jobs = jobs
        self.initialIndex = initialIndex
        _page = State(initialValue: initialIndex)
    }

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(jobs: jobs, page: $page)
            JobPager(jobs: jobs, page: $page)
        }
        .background(hig.systemGroupedBackground)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Text("\(page + 1) / \(jobs.count)")
                    .font(HIGType.footnote)
                    .foregroundColor(hig.secondaryLabel)
                    .monospacedDigit()
            }
        }
    }
}

private struct JobPager: View {
    let jobs: [WorkExperience]
    @Binding var page: Int
    @Environment(\.hig) private var hig

    var body: some View {
        GeometryReader { proxy in
            ScrollView(.horizontal) {
                LazyHStack(spacing: 0) {
                    ForEach(Array(jobs.enumerated()), id: \.offset) { idx, job in
                        ScrollView(.vertical) {
                            JobPage(job: job,
                                    accent: idx == 0 ? hig.tint : hig.indigo,
                                    idx: idx)
                        }
                        .frame(width: proxy.size.width)
                        .id(idx)
                    }
                }
                .scrollTargetLayout()
            }
            .scrollTargetBehavior(.paging)
            .scrollIndicators(.hidden)
            .scrollPosition(id: scrollIDBinding)
        }
    }

    private var scrollIDBinding: Binding<Int?> {
        Binding(
            get: { page },
            set: { newID in
                if let newID, newID != page {
                    page = newID
                }
            }
        )
    }
}

private struct DetailHeader: View {
    let jobs: [WorkExperience]
    @Binding var page: Int
    @Environment(\.hig) private var hig

    private var current: WorkExperience { jobs[page] }

    private var trimmedCompany: String {
        current.company
            .replacingOccurrences(of: "股份有限公司", with: "")
            .replacingOccurrences(of: "有限公司", with: "")
    }

    var body: some View {
        VStack(spacing: 12) {
            // Current company + period (centered)
            VStack(spacing: 2) {
                Text(trimmedCompany)
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(hig.label)
                    .multilineTextAlignment(.center)
                Text(verbatim: "\(current.period.start.formatted) ─ \(current.period.end?.formatted ?? String(localized: "period.present"))")
                    .font(HIGType.footnote)
                    .foregroundColor(hig.secondaryLabel)
                    .monospacedDigit()
            }
            .padding(.horizontal, 20)
            .padding(.top, 4)
            .id(page) // crossfade between pages

            // Page dots
            HStack(spacing: 6) {
                ForEach(0..<jobs.count, id: \.self) { idx in
                    Capsule()
                        .fill(idx == page ? hig.tint : hig.systemFill)
                        .frame(width: idx == page ? 22 : 7, height: 7)
                        .frame(height: 24) // expand tap target vertically
                        .contentShape(Rectangle())
                        .onTapGesture {
                            withAnimation(.easeOut(duration: 0.22)) {
                                page = idx
                            }
                        }
                        .animation(.easeOut(duration: 0.22), value: page)
                }
            }
            .padding(.bottom, 4)
        }
        .frame(maxWidth: .infinity)
        .animation(.easeInOut(duration: 0.18), value: page)
    }
}

private struct JobPage: View {
    let job: WorkExperience
    let accent: Color
    let idx: Int

    @Environment(\.hig) private var hig

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            JobHeader(job: job, accent: accent, idx: idx)
                .padding(.horizontal, 16)

            SectionHeaderText("section.main_projects")

            VStack(spacing: 10) {
                ForEach(Array(job.projects.enumerated()), id: \.offset) { _, project in
                    ProjectCard(project: project, accent: accent)
                }
            }
            .padding(.horizontal, 16)
        }
        .padding(.top, 8)
    }
}

private struct JobHeader: View {
    let job: WorkExperience
    let accent: Color
    let idx: Int

    @Environment(\.hig) private var hig
    private var isCurrent: Bool { job.period.end == nil }

    var body: some View {
        ZStack(alignment: .topLeading) {
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(hig.secondarySystemGroupedBackground)
            RadialGradient(colors: [accent.opacity(0.15), .clear],
                           center: .topTrailing, startRadius: 0, endRadius: 140)
                .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
                .allowsHitTesting(false)

            VStack(alignment: .leading, spacing: 0) {
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
                            TagView(text: "\(job.period.start.formatted) ─ \(job.period.end?.formatted ?? String(localized: "period.present"))",
                                    color: isCurrent ? hig.green : hig.secondaryLabel,
                                    leadingDot: isCurrent)
                            TagView(text: job.period.dateRange.formatted(
                                        .components(style: .condensedAbbreviated, fields: [.year, .month])
                                    ),
                                    color: hig.secondaryLabel)
                        }
                        .padding(.top, 10)
                    }
                    Spacer()
                }

                Divider().background(hig.separator).padding(.vertical, 14)

                VStack(alignment: .leading, spacing: 8) {
                    ForEach(Array(job.bullets.enumerated()), id: \.offset) { _, bullet in
                        HStack(alignment: .top, spacing: 10) {
                            Circle()
                                .fill(accent)
                                .frame(width: 5, height: 5)
                                .padding(.top, 8)
                            Text(bullet)
                                .font(HIGType.subheadline)
                                .foregroundColor(hig.label)
                                .lineSpacing(2)
                        }
                    }
                }
            }
            .padding(18)
        }
    }
}

private struct ProjectCard: View {
    let project: Project
    let accent: Color
    @State private var open = false

    @Environment(\.hig) private var hig

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button { withAnimation(.easeInOut(duration: 0.24)) { open.toggle() } } label: {
                HStack(spacing: 12) {
                    Glyph(systemImage: "folder.fill",
                          size: 32, background: accent, radius: 8)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(project.name)
                            .font(HIGType.headline)
                            .foregroundColor(hig.label)
                        Text(subtitleText)
                            .font(HIGType.footnote)
                            .foregroundColor(hig.secondaryLabel)
                    }
                    Spacer()
                    Image(systemName: "chevron.down")
                        .font(.system(size: 20, weight: .medium))
                        .foregroundColor(hig.tertiaryLabel)
                        .rotationEffect(.degrees(open ? 180 : 0))
                }
                .padding(EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16))
                .frame(maxWidth: .infinity, alignment: .leading)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)

            if open {
                ProjectBody(project: project, accent: accent)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 16)
            }
        }
        .background(
            hig.secondarySystemGroupedBackground,
            in: RoundedRectangle(cornerRadius: 14, style: .continuous)
        )
    }

    private var subtitleText: String {
        let endLabel = project.period.end?.formatted ?? String(localized: "period.present")
        let p = "\(project.period.start.formatted) ─ \(endLabel)"
        if let s = project.summary, !s.isEmpty { return "\(p) · \(s)" }
        return p
    }
}

private struct ProjectBody: View {
    let project: Project
    let accent: Color

    var body: some View {
        if let yl = project as? Project.YearList {
            YearTimeline(items: yl.yearItems, accent: accent)
        } else if let bl = project as? Project.Bullets {
            BulletList(bullets: bl.bullets, accent: accent)
        } else {
            EmptyView()
        }
    }
}

private struct YearTimeline: View {
    let items: [YearItem]
    let accent: Color
    @Environment(\.hig) private var hig

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.offset) { idx, item in
                let isFirst = idx == 0
                let isLast = idx == items.count - 1
                HStack(alignment: .top, spacing: 0) {
                    VStack(spacing: 0) {
                        Rectangle()
                            .fill(isFirst ? Color.clear : hig.separator)
                            .frame(width: 2, height: 6)
                        ZStack {
                            Circle()
                                .stroke(accent, lineWidth: 2)
                                .background(Circle().fill(hig.secondarySystemGroupedBackground))
                                .frame(width: 12, height: 12)
                            Circle()
                                .fill(accent)
                                .frame(width: 8, height: 8)
                        }
                        .zIndex(1)
                        Rectangle()
                            .fill(isLast ? Color.clear : hig.separator)
                            .frame(width: 2)
                            .frame(maxHeight: .infinity)
                    }
                    .frame(width: 24, alignment: .leading)

                    VStack(alignment: .leading, spacing: 4) {
                        Text(item.years.map { String($0.int32Value) }.joined(separator: " ─ "))
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(accent)
                            .tracking(0.4)
                        Text(item.title)
                            .font(HIGType.subheadlineEmph)
                            .foregroundColor(hig.label)
                        if let desc = item.description_, !desc.isEmpty {
                            Text(desc)
                                .font(HIGType.footnote)
                                .foregroundColor(hig.secondaryLabel)
                                .lineSpacing(4)
                        }
                        let bullets = item.bullets
                        if !bullets.isEmpty {
                            VStack(alignment: .leading, spacing: 4) {
                                ForEach(Array(bullets.enumerated()), id: \.offset) { _, b in
                                    Text("· \(b)")
                                        .font(HIGType.footnote)
                                        .foregroundColor(hig.secondaryLabel)
                                        .lineSpacing(4)
                                }
                            }
                        }
                        let tags = item.tags
                        if !tags.isEmpty {
                            FlowLayoutTags(tags: tags, color: hig.secondaryLabel)
                        }
                    }
                    .padding(.bottom, isLast ? 0 : 16)
                }
            }
        }
        .padding(.top, 6)
    }
}

private struct BulletList: View {
    let bullets: [String]
    let accent: Color
    @Environment(\.hig) private var hig

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ForEach(Array(bullets.enumerated()), id: \.offset) { _, bullet in
                HStack(alignment: .top, spacing: 10) {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(accent)
                        .padding(.top, 3)
                    Text(bullet)
                        .font(HIGType.footnote)
                        .foregroundColor(hig.label)
                        .lineSpacing(4)
                }
            }
        }
    }
}

struct FlowLayoutTags: View {
    let tags: [String]
    let color: Color
    var body: some View {
        FlexibleHStack(data: tags, spacing: 5) { tag in
            TagView(text: tag, color: color)
        }
    }
}

/// Simple wrap-flow horizontal layout (chips/tags).
struct FlexibleHStack<Data: RandomAccessCollection, Content: View>: View where Data.Element: Hashable {
    let data: Data
    let spacing: CGFloat
    let content: (Data.Element) -> Content

    init(data: Data, spacing: CGFloat, @ViewBuilder content: @escaping (Data.Element) -> Content) {
        self.data = data
        self.spacing = spacing
        self.content = content
    }

    var body: some View {
        FlowLayout(spacing: spacing) {
            ForEach(Array(data.enumerated()), id: \.offset) { _, item in
                content(item)
            }
        }
    }
}

struct FlowLayout: Layout {
    var spacing: CGFloat = 6

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let maxWidth = proposal.width ?? .infinity
        var totalHeight: CGFloat = 0
        var rowWidth: CGFloat = 0
        var rowHeight: CGFloat = 0
        for sv in subviews {
            let s = sv.sizeThatFits(.unspecified)
            if rowWidth + s.width > maxWidth {
                totalHeight += rowHeight + spacing
                rowWidth = s.width + spacing
                rowHeight = s.height
            } else {
                rowWidth += s.width + spacing
                rowHeight = max(rowHeight, s.height)
            }
        }
        totalHeight += rowHeight
        return CGSize(width: maxWidth.isFinite ? maxWidth : rowWidth, height: totalHeight)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var x: CGFloat = bounds.minX
        var y: CGFloat = bounds.minY
        var rowHeight: CGFloat = 0
        for sv in subviews {
            let s = sv.sizeThatFits(.unspecified)
            if x + s.width > bounds.maxX {
                x = bounds.minX
                y += rowHeight + spacing
                rowHeight = 0
            }
            sv.place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(s))
            x += s.width + spacing
            rowHeight = max(rowHeight, s.height)
        }
    }
}
