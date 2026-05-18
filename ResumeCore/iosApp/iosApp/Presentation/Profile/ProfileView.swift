import SwiftUI
import Shared

struct ProfileView: View {
    @State private var viewModel = ProfileViewModel()
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            resolveLoadState(viewModel.state, retry: { Task { await viewModel.load() } }) { data in
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
                                EducationRow(edu: edu, accent: idx == 0 ? hig.purple : hig.brown,
                                             isLast: idx == data.education.count - 1)
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
                Avatar(photo: header.photo)
                Text("\(header.name) · Daniel Chi")
                    .font(HIGType.title2)
                    .foregroundColor(hig.label)
                    .multilineTextAlignment(.center)
                    .padding(.top, 4)
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

    private struct Avatar: View {
        let photo: Photo
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
                            monogram
                        }
                    }
                } else {
                    monogram
                }
            }
            .frame(width: 92, height: 92)
            .clipShape(Circle())
            .shadow(color: .black.opacity(0.18), radius: 18, y: 6)
            .accessibilityLabel(photo.alt)
        }

        private var monogram: some View {
            ZStack {
                LinearGradient(
                    colors: [hig.tint, hig.indigo],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                Text("DC")
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
        let endLabel = edu.period.end?.formatted ?? String(localized: "period.present")
        let subtitle = "\(edu.major) · \(edu.period.start.formatted) ─ \(endLabel)"
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

private struct AboutSection: View {
    let paragraphs: [String]
    @State private var expanded = false
    @Environment(\.hig) private var hig

    private var canToggle: Bool { paragraphs.count > 1 }

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
