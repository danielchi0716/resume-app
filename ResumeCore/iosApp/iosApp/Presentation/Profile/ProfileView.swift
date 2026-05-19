import Shared
import SwiftUI

struct ProfileView: View {
    @State private var viewModel = ProfileViewModel()
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            LoadStateView(state: viewModel.state, retry: { Task { await viewModel.load() } }) { data in
                VStack(alignment: .leading, spacing: 0) {
                    HeroCard(header: data.header)
                    QuickContacts(contacts: data.header.contacts)

                    SectionHeaderText("section.languages")
                    InsetCard {
                        VStack(spacing: 0) {
                            ForEach(Array(data.languages.enumerated()), id: \.offset) { idx, lang in
                                LanguageRow(language: lang, isLast: idx == data.languages.count - 1)
                            }
                        }
                    }

                    Color.clear.frame(height: 28)

                    SectionHeaderText("section.education")
                    InsetCard {
                        VStack(spacing: 0) {
                            ForEach(Array(data.education.enumerated()), id: \.offset) { idx, edu in
                                EducationRow(
                                    edu: edu,
                                    accent: idx == 0 ? hig.purple : hig.brown,
                                    isLast: idx == data.education.count - 1
                                )
                            }
                        }
                    }

                    Color.clear.frame(height: 28)

                    AboutSection(paragraphs: data.about)

                    Color.clear.frame(height: 24)
                }
            }
        }
        .background(hig.systemGroupedBackground.ignoresSafeArea())
        .onAppear { viewModel.loadAll() }
    }
}

private struct HeroCard: View {
    let header: Header
    @Environment(\.hig) private var hig

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 22, style: .continuous)
                .fill(hig.secondarySystemGroupedBackground)
            RadialGradient(
                colors: [hig.tint.opacity(0.20), .clear],
                center: .top,
                startRadius: 0,
                endRadius: 180
            )
            .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
            .allowsHitTesting(false)

            VStack(spacing: 10) {
                Avatar(photo: header.photo, monogram: initials(from: header.englishName))
                Text(header.name)
                    .font(HIGType.title2)
                    .foregroundColor(hig.label)
                    .multilineTextAlignment(.center)
                    .padding(.top, 4)
                Text(header.englishName)
                    .font(HIGType.footnote)
                    .foregroundColor(hig.secondaryLabel)
                    .multilineTextAlignment(.center)
                Text("\(header.subtitle) · \(header.tagline.text)")
                    .font(HIGType.subheadline)
                    .foregroundColor(hig.secondaryLabel)
                    .multilineTextAlignment(.center)
                    .lineSpacing(2)
                FlowingChips(items: header.tagline.keywords)
                    .padding(.top, 6)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 24)
        }
        .padding(.horizontal, 16)
        .padding(.top, 4)
        .padding(.bottom, 20)
    }

    private func initials(from name: String) -> String {
        name.split(separator: " ")
            .compactMap { $0.first.map(String.init) }
            .prefix(2)
            .joined()
    }

    private struct Avatar: View {
        let photo: Photo
        let monogram: String
        @Environment(\.hig) private var hig

        private var resolvedURL: URL? {
            guard let raw = photo.url as? String, !raw.isEmpty else { return nil }
            return URL(string: IosBridgeKt.resolveResourceUrl(rawUrl: raw))
        }

        var body: some View {
            Group {
                if let url = resolvedURL {
                    AsyncImage(url: url, transaction: Transaction(animation: .easeInOut(duration: 0.25))) { phase in
                        switch phase {
                        case .success(let image):
                            image.resizable().scaledToFill()
                        default:
                            monogramView
                        }
                    }
                } else {
                    monogramView
                }
            }
            .frame(width: 92, height: 92)
            .clipShape(Circle())
            .shadow(color: .black.opacity(0.18), radius: 18, y: 6)
            .accessibilityLabel(photo.alt)
        }

        private var monogramView: some View {
            ZStack {
                LinearGradient(
                    colors: [hig.tint, hig.indigo],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                Text(monogram)
                    .font(.system(size: 36, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
            }
        }
    }
}

private struct FlowingChips: View {
    let items: [String]
    @Environment(\.hig) private var hig

    var body: some View {
        HStack(spacing: 6) {
            ForEach(Array(items.prefix(4).enumerated()), id: \.offset) { idx, kw in
                let palette: [Color] = [hig.teal, hig.indigo, hig.purple]
                TagView(text: kw, color: palette[idx % palette.count])
            }
        }
    }
}

private struct QuickContacts: View {
    let contacts: [Contact]
    @Environment(\.hig) private var hig

    var body: some View {
        HStack(spacing: 12) {
            ForEach(Array(contacts.enumerated()), id: \.offset) { _, contact in
                ContactButton(contact: contact)
                    .frame(maxWidth: .infinity)
            }
        }
        .padding(.horizontal, 24)
        .padding(.bottom, 24)
    }
}

private struct LanguageRow: View {
    let language: Language
    let isLast: Bool
    @Environment(\.hig) private var hig

    var body: some View {
        let title = switch language.code {
        case .zh: Text("language.zh")
        case .en: Text("language.en")
        }

        let subtitle = switch onEnum(of: language.level) {
        case .native:
            Text("language.level.native")
        case .proficiency(let p):
            Text("language.axis.listening") + Text(" ") + Text(skillKey(for: p.listening))
                + Text(" · ") + Text("language.axis.speaking") + Text(" ") + Text(skillKey(for: p.speaking))
                + Text(" · ") + Text("language.axis.reading") + Text(" ") + Text(skillKey(for: p.reading))
                + Text(" · ") + Text("language.axis.writing") + Text(" ") + Text(skillKey(for: p.writing))
        }

        return HStack(spacing: 12) {
            switch onEnum(of: language.level) {
            case .native:
                Glyph(systemImage: "character.book.closed.fill", background: hig.red)
            case .proficiency:
                Glyph(systemImage: "globe", background: hig.blue)
            }
            VStack(alignment: .leading, spacing: 2) {
                title
                    .font(HIGType.body)
                    .foregroundColor(hig.label)
                subtitle
                    .font(HIGType.footnote)
                    .foregroundColor(hig.secondaryLabel)
            }
            Spacer(minLength: 8)
            if let badge = language.badge {
                GoldBadge(text: badge)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .frame(minHeight: 56)
        .overlay(alignment: .bottom) {
            if !isLast {
                Rectangle()
                    .fill(hig.separator.opacity(0.5))
                    .frame(height: 0.5)
                    .padding(.leading, 60)
            }
        }
    }

    private func skillKey(for skill: LanguageSkill) -> LocalizedStringKey {
        switch skill {
        case .basic: "language.skill.basic"
        case .intermediate: "language.skill.intermediate"
        case .advanced: "language.skill.advanced"
        case .fluent: "language.skill.fluent"
        }
    }

    private struct GoldBadge: View {
        let text: String
        var body: some View {
            HStack(spacing: 4) {
                Image(systemName: "medal.fill")
                    .font(.system(size: 13, weight: .semibold))
                Text(text)
                    .font(.system(size: 12, weight: .semibold))
                    .tracking(0.3)
            }
            .foregroundColor(Color(hex: 0xFFFBE8))
            .padding(.horizontal, 10)
            .frame(height: 24)
            .background(
                LinearGradient(
                    colors: [Color(hex: 0xC8A24A), Color(hex: 0xB0863A)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                ),
                in: RoundedRectangle(cornerRadius: 8, style: .continuous)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 8, style: .continuous)
                    .strokeBorder(Color(hex: 0xB0863A).opacity(0.5), lineWidth: 0.5)
            )
            .shadow(color: Color(hex: 0xB0863A).opacity(0.25), radius: 1, y: 1)
        }
    }
}

private struct EducationRow: View {
    let edu: Education
    let accent: Color
    let isLast: Bool
    @Environment(\.hig) private var hig

    var body: some View {
        let endText = if let end = edu.period.end {
            Text(verbatim: end.formatted)
        } else {
            Text("period.present")
        }
        let subtitle = Text(verbatim: "\(edu.major) · \(edu.period.start.formatted) ─ ") + endText
        return HStack(spacing: 12) {
            Glyph(systemImage: "graduationcap.fill", background: accent)
            VStack(alignment: .leading, spacing: 2) {
                Text(verbatim: edu.school)
                    .font(HIGType.body)
                    .foregroundColor(hig.label)
                subtitle
                    .font(HIGType.footnote)
                    .foregroundColor(hig.secondaryLabel)
            }
            Spacer(minLength: 8)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .frame(minHeight: 56)
        .overlay(alignment: .bottom) {
            if !isLast {
                Rectangle()
                    .fill(hig.separator.opacity(0.5))
                    .frame(height: 0.5)
                    .padding(.leading, 60)
            }
        }
    }
}

private struct AboutSection: View {
    let paragraphs: [String]
    @State private var expanded = false
    @Environment(\.hig) private var hig

    private var canToggle: Bool {
        paragraphs.count > 1
    }

    private var visibleParagraphs: [String] {
        expanded || !canToggle ? paragraphs : Array(paragraphs.prefix(1))
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            SectionHeaderText("section.about")
            InsetCard(padding: EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16)) {
                VStack(alignment: .leading, spacing: 12) {
                    ForEach(Array(visibleParagraphs.enumerated()), id: \.offset) { _, p in
                        Text(p)
                            .font(HIGType.subheadline)
                            .foregroundColor(hig.label)
                            .lineSpacing(2)
                    }
                    if canToggle {
                        Button {
                            withAnimation(.easeInOut(duration: 0.22)) {
                                expanded.toggle()
                            }
                        } label: {
                            HStack(spacing: 4) {
                                (expanded ? Text("action.collapse") : Text("action.expand"))
                                    .font(HIGType.subheadlineEmph)
                                Image(systemName: "chevron.down")
                                    .font(.system(size: 12, weight: .semibold))
                                    .rotationEffect(.degrees(expanded ? 180 : 0))
                            }
                            .foregroundColor(hig.tint)
                        }
                        .buttonStyle(.plain)
                        .padding(.top, 2)
                    }
                }
            }
        }
    }
}
