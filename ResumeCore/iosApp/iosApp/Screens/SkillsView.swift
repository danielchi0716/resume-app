import SwiftUI
import Shared

struct SkillsView: View {
    @ObservedObject var store: ResumeStore
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                resolveLoadState(store.skills, retry: { Task { await store.loadSkills() } }) { skills in
                    IntroCard(skills: skills)

                    ForEach(Array(skills.enumerated()), id: \.offset) { _, skill in
                        let color = colorFor(name: skill.name)
                        let icon = iconFor(name: skill.name)
                        SectionHeaderText(verbatim: skill.name) {
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
    }

    private func colorFor(name: String) -> Color {
        switch name {
        case "AI / LLM":                    return hig.purple
        case "Architecture / Principles":   return hig.orange
        case "Kotlin Multiplatform":        return hig.indigo
        case "Android":                     return hig.green
        case "iOS":                         return hig.blue
        case "General / Tools":             return hig.brown
        default:                            return hig.tint
        }
    }

    private func iconFor(name: String) -> String {
        switch name {
        case "AI / LLM":                    return "sparkles"
        case "Architecture / Principles":   return "ruler.fill"
        case "Kotlin Multiplatform":        return "dot.scope"
        case "Android":                     return "smartphone"
        case "iOS":                         return "iphone.gen3"
        case "General / Tools":             return "wrench.and.screwdriver.fill"
        default:                            return "bolt.fill"
        }
    }
}

private struct IntroCard: View {
    let skills: [Skill]
    @Environment(\.hig) private var hig

    var totalCount: Int {
        skills.reduce(0) { acc, s in
            if let cat = s as? Skill.Category {
                return acc + Int((cat.tags as? [String])?.count ?? 0)
            } else if let plat = s as? Skill.Platform {
                let subs = plat.subcategories as? [SkillSubcategory] ?? []
                return acc + subs.reduce(0) { $0 + Int(($1.items as? [String])?.count ?? 0) }
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
                ForEach(Array((plat.subcategories as? [SkillSubcategory] ?? []).enumerated()), id: \.offset) { _, sub in
                    VStack(alignment: .leading, spacing: 6) {
                        Text(sub.label)
                            .font(HIGType.caption1Emph)
                            .foregroundColor(hig.secondaryLabel)
                            .tracking(0.3)
                        FlowLayoutTags(tags: sub.items as? [String] ?? [], color: color)
                    }
                }
            }
        } else if let cat = skill as? Skill.Category {
            FlowLayoutTags(tags: cat.tags as? [String] ?? [], color: color)
        } else {
            EmptyView()
        }
    }
}
