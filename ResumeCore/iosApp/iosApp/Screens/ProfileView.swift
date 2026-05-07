import SwiftUI
import Shared

struct ProfileView: View {
    @ObservedObject var store: ResumeStore
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                resolveLoadState(store.header, retry: { Task { await store.loadHeader() } }) { header in
                    HeroCard(header: header)
                    QuickContacts(contacts: header.contacts)
                }

                resolveLoadState(store.about, retry: { Task { await store.loadAbout() } }) { paragraphs in
                    SectionHeaderText(L10n.sectionAbout)
                    InsetCard(padding: EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16)) {
                        VStack(alignment: .leading, spacing: 12) {
                            ForEach(Array(paragraphs.enumerated()), id: \.offset) { _, p in
                                Text(p)
                                    .font(HIGType.subheadline)
                                    .foregroundColor(hig.label)
                                    .lineSpacing(2)
                            }
                        }
                    }
                }

                Color.clear.frame(height: 28)

                resolveLoadState(store.languages, retry: { Task { await store.loadLanguages() } }) { langs in
                    SectionHeaderText(L10n.sectionLanguages)
                    InsetCard {
                        VStack(spacing: 0) {
                            ForEach(Array(langs.enumerated()), id: \.offset) { idx, lang in
                                LanguageRow(language: lang, isLast: idx == langs.count - 1)
                            }
                        }
                    }
                }

                Color.clear.frame(height: 28)

                resolveLoadState(store.education, retry: { Task { await store.loadEducation() } }) { edus in
                    SectionHeaderText(L10n.sectionEducation)
                    InsetCard {
                        VStack(spacing: 0) {
                            ForEach(Array(edus.enumerated()), id: \.offset) { idx, edu in
                                EducationRow(edu: edu, accent: idx == 0 ? hig.purple : hig.brown,
                                             isLast: idx == edus.count - 1)
                            }
                        }
                    }
                }

                Color.clear.frame(height: 24)
            }
        }
        .background(hig.systemGroupedBackground.ignoresSafeArea())
    }
}

private struct HeroCard: View {
    let header: Header
    @Environment(\.hig) private var hig

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 22, style: .continuous)
                .fill(hig.secondarySystemGroupedBackground)
            // Gradient halo
            RadialGradient(
                colors: [hig.tint.opacity(0.20), .clear],
                center: .top,
                startRadius: 0,
                endRadius: 180
            )
            .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
            .allowsHitTesting(false)

            VStack(spacing: 10) {
                Avatar()
                Text("\(header.name) · Daniel Chi")
                    .font(HIGType.title2)
                    .foregroundColor(hig.label)
                    .padding(.top, 4)
                Text("\(header.subtitle) · \(header.tagline.text)")
                    .font(HIGType.subheadline)
                    .foregroundColor(hig.secondaryLabel)
                FlowingChips(items: header.tagline.keywords as? [String] ?? [])
                    .padding(.top, 6)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 24)
        }
        .padding(.horizontal, 16)
        .padding(.top, 4)
        .padding(.bottom, 20)
    }

    private struct Avatar: View {
        @Environment(\.hig) private var hig
        var body: some View {
            ZStack {
                Circle()
                    .fill(LinearGradient(
                        colors: [hig.tint, hig.indigo],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing))
                Text("DC")
                    .font(.system(size: 36, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
            }
            .frame(width: 92, height: 92)
            .shadow(color: .black.opacity(0.18), radius: 18, y: 6)
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
        ListRow(
            title: language.name,
            subtitle: language.level,
            isLast: isLast,
            leading: {
                Glyph(systemImage: language.name == "中文" ? "character.book.closed.fill" : "globe",
                      background: language.name == "中文" ? hig.red : hig.blue)
            },
            trailing: {
                if let badge = language.badge {
                    GoldBadge(text: badge)
                }
            }
        )
    }
}

private struct EducationRow: View {
    let edu: Education
    let accent: Color
    let isLast: Bool

    var body: some View {
        let subtitle = "\(edu.major) · \(PeriodFormatter.format(edu.period))"
        ListRow(
            title: edu.school,
            subtitle: subtitle,
            isLast: isLast,
            leading: {
                Glyph(systemImage: "graduationcap.fill", background: accent)
            }
        )
    }
}
