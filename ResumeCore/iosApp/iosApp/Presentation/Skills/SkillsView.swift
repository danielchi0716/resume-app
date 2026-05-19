import SwiftUI
import Shared

struct SkillsView: View {
    @State private var viewModel = SkillsViewModel()
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                LoadStateView(state: viewModel.skills, retry: { Task { await viewModel.loadSkills() } }) { skills in
                    IntroCard(skills: skills)

                    ForEach(Array(skills.enumerated()), id: \.offset) { _, skill in
                        let color = colorFor(id: skill.id)
                        let icon = iconFor(id: skill.id)
                        SectionHeaderText(nameKey(for: skill.id)) {
                            Image(systemName: icon)
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(color)
                        }
                        InsetCard(padding: EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16)) {
                            SkillBody(skill: skill, color: color)
                        }
                        Color.clear.frame(height: 24)
                    }
                }
            }
        }
        .background(hig.systemGroupedBackground.ignoresSafeArea())
        .onAppear { viewModel.loadAll() }
    }

    private func colorFor(id: SkillId) -> Color {
        switch id {
        case .aiLlm: hig.purple
        case .architecture: hig.orange
        case .kmp: hig.indigo
        case .android: hig.green
        case .ios: hig.blue
        case .tools: hig.brown
        }
    }

    private func iconFor(id: SkillId) -> String {
        switch id {
        case .aiLlm: "sparkles"
        case .architecture: "ruler.fill"
        case .kmp: "dot.scope"
        case .android: "smartphone"
        case .ios: "iphone.gen3"
        case .tools: "wrench.and.screwdriver.fill"
        }
    }

    private func nameKey(for id: SkillId) -> LocalizedStringKey {
        switch id {
        case .aiLlm: "skills.name.ai-llm"
        case .architecture: "skills.name.architecture"
        case .kmp: "skills.name.kmp"
        case .android: "skills.name.android"
        case .ios: "skills.name.ios"
        case .tools: "skills.name.tools"
        }
    }
}

private struct IntroCard: View {
    let skills: [Skill]
    @Environment(\.hig) private var hig

    var totalCount: Int {
        skills.reduce(0) { acc, s in
            if let cat = s as? Skill.Category {
                return acc + cat.tags.count
            } else if let plat = s as? Skill.Platform {
                return acc + plat.subcategories.reduce(0) { $0 + $1.items.count }
            }
            return acc
        }
    }

    var body: some View {
        HStack(spacing: 14) {
            Glyph(systemImage: "bolt.fill", size: 42, background: hig.tint, radius: 10)
            VStack(alignment: .leading, spacing: 2) {
                Text("nav.skills")
                    .font(HIGType.headline)
                    .foregroundColor(hig.label)
                Text(verbatim: String(format: String(localized: "skills.summary"), skills.count, totalCount))
                    .font(HIGType.footnote)
                    .foregroundColor(hig.secondaryLabel)
            }
            Spacer()
        }
        .padding(16)
        .background(
            hig.secondarySystemGroupedBackground,
            in: RoundedRectangle(cornerRadius: 18, style: .continuous)
        )
        .padding(.horizontal, 16)
        .padding(.top, 4)
        .padding(.bottom, 16)
    }
}

private struct SkillBody: View {
    let skill: Skill
    let color: Color

    @Environment(\.hig) private var hig

    var body: some View {
        if let plat = skill as? Skill.Platform {
            VStack(alignment: .leading, spacing: 14) {
                ForEach(Array(plat.subcategories.enumerated()), id: \.offset) { _, sub in
                    VStack(alignment: .leading, spacing: 6) {
                        Text(sub.label)
                            .font(HIGType.caption1Emph)
                            .foregroundColor(hig.secondaryLabel)
                            .tracking(0.3)
                        FlowLayoutTags(tags: sub.items, color: color)
                    }
                }
            }
        } else if let cat = skill as? Skill.Category {
            FlowLayoutTags(tags: cat.tags, color: color)
        } else {
            EmptyView()
        }
    }
}
