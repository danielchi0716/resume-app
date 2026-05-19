import SwiftUI
import Shared

struct MoreView: View {
    @State private var viewModel = MoreViewModel()
    @Environment(\.hig) private var hig

    var body: some View {
        ScrollView {
            LoadStateView(state: viewModel.state, retry: { Task { await viewModel.load() } }) { data in
                VStack(alignment: .leading, spacing: 0) {
                    SectionHeaderText("section.side_projects")
                    VStack(spacing: 12) {
                        ForEach(Array(data.sideProjects.enumerated()), id: \.offset) { idx, project in
                            SideProjectCard(project: project,
                                            accent: idx == 0 ? hig.green : hig.purple,
                                            idx: idx)
                        }
                    }
                    .padding(.horizontal, 16)

                    Color.clear.frame(height: 28)

                    ClosingCard()
                        .padding(.horizontal, 16)

                    Color.clear.frame(height: 24)

                    SectionHeaderText("section.contact")
                    HStack(spacing: 12) {
                        ForEach(Array(data.header.contacts.enumerated()), id: \.offset) { _, contact in
                            ContactButton(contact: contact)
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .padding(.horizontal, 24)

                    Footer()
                }
            }
        }
        .background(hig.systemGroupedBackground.ignoresSafeArea())
        .onAppear { viewModel.loadAll() }
    }
}

private struct SideProjectCard: View {
    let project: SideProject
    let accent: Color
    let idx: Int

    @Environment(\.hig) private var hig

    var body: some View {
        ZStack(alignment: .topLeading) {
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(hig.secondarySystemGroupedBackground)
            RadialGradient(colors: [accent.opacity(0.15), .clear],
                           center: .topTrailing, startRadius: 0, endRadius: 110)
                .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
                .allowsHitTesting(false)

            VStack(alignment: .leading, spacing: 0) {
                HStack(alignment: .top, spacing: 14) {
                    Glyph(systemImage: idx == 0 ? "photo.stack.fill" : "sparkles",
                          size: 40, background: accent, radius: 10)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(project.name)
                            .font(HIGType.headline)
                            .foregroundColor(hig.label)
                        Text(project.subtitle)
                            .font(HIGType.footnote)
                            .foregroundColor(hig.secondaryLabel)
                        TagView(text: "\(project.period.start.formatted) ─ \(project.period.end?.formatted ?? String(localized: "period.present"))",
                                color: accent)
                            .padding(.top, 8)
                    }
                    Spacer()
                }

                Divider().background(hig.separator).padding(.vertical, 12)

                VStack(alignment: .leading, spacing: 8) {
                    ForEach(Array(project.bullets.enumerated()), id: \.offset) { _, b in
                        HStack(alignment: .top, spacing: 10) {
                            Circle()
                                .fill(accent)
                                .frame(width: 5, height: 5)
                                .padding(.top, 8)
                            Text(b)
                                .font(HIGType.footnote)
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

private struct ClosingCard: View {
    @Environment(\.hig) private var hig

    var body: some View {
        ZStack(alignment: .topLeading) {
            LinearGradient(colors: [hig.tint, hig.indigo],
                           startPoint: .topLeading, endPoint: .bottomTrailing)
                .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))

            Circle()
                .fill(Color.white.opacity(0.12))
                .frame(width: 200, height: 200)
                .offset(x: 220, y: -60)
                .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
                .allowsHitTesting(false)

            VStack(alignment: .leading, spacing: 6) {
                Image(systemName: "heart.fill")
                    .font(.system(size: 32, weight: .medium))
                    .foregroundColor(.white)

                Text("cta.thanks")
                    .font(HIGType.title2)
                    .foregroundColor(.white)
                    .padding(.top, 10)

                Text("cta.subtitle")
                    .font(HIGType.subheadline)
                    .foregroundColor(.white.opacity(0.9))
                    .lineSpacing(1)
                    .padding(.top, 6)

                ArchitectureDiagram()
                    .padding(.top, 16)

                WebRow()
                    .padding(.top, 10)

                Text("cta.bridge")
                    .font(HIGType.footnote)
                    .foregroundColor(.white.opacity(0.85))
                    .padding(.top, 14)
            }
            .padding(EdgeInsets(top: 24, leading: 20, bottom: 24, trailing: 20))
        }
    }
}

private struct ArchitectureDiagram: View {
    var body: some View {
        Link(destination: URL(string: AppConfig.repoUrl)!) {
            VStack(spacing: 4) {
                HStack(spacing: 8) {
                    Image(systemName: "circle.hexagongrid.fill")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(HIGColors.light.tint)
                    VStack(alignment: .leading, spacing: 1) {
                        Text("Kotlin Multiplatform")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(HIGColors.light.tint)
                            .tracking(0.2)
                        Text("Shared Domain · Data")
                            .font(HIGType.caption2)
                            .foregroundColor(HIGColors.light.tint.opacity(0.7))
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(
                    Color.white.opacity(0.95),
                    in: RoundedRectangle(cornerRadius: 10, style: .continuous)
                )
                .shadow(color: .black.opacity(0.12), radius: 3, y: 2)

                ZStack {
                    ConnectorLines()
                }
                .frame(height: 34)

                HStack {
                    PlatformCircle(systemImage: "smartphone", label: "platform.android")
                    Spacer()
                    PlatformCircle(systemImage: "iphone.gen3", label: "platform.ios")
                }
                .padding(.horizontal, 28)
            }
            .padding(EdgeInsets(top: 16, leading: 14, bottom: 18, trailing: 14))
            .background(
                Color.white.opacity(0.12),
                in: RoundedRectangle(cornerRadius: 16, style: .continuous)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .stroke(Color.white.opacity(0.22), lineWidth: 0.5)
            )
            .overlay(alignment: .topTrailing) {
                Image(systemName: "arrow.up.right.square")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.white.opacity(0.8))
                    .padding(EdgeInsets(top: 10, leading: 0, bottom: 0, trailing: 12))
            }
        }
        .buttonStyle(.plain)
    }
}

private struct ConnectorLines: View {
    var body: some View {
        Canvas { ctx, size in
            let style = StrokeStyle(lineWidth: 1.5, dash: [3, 3])
            let stroke = Color.white.opacity(0.7)

            let topY: CGFloat = 0
            let midY: CGFloat = 12
            let bottomY: CGFloat = size.height
            let centerX = size.width / 2
            let leftX = size.width * 0.22
            let rightX = size.width * 0.78

            var path = Path()
            path.move(to: CGPoint(x: centerX, y: topY))
            path.addLine(to: CGPoint(x: centerX, y: midY))
            path.addLine(to: CGPoint(x: leftX, y: midY))
            path.addLine(to: CGPoint(x: leftX, y: bottomY))

            path.move(to: CGPoint(x: centerX, y: midY))
            path.addLine(to: CGPoint(x: rightX, y: midY))
            path.addLine(to: CGPoint(x: rightX, y: bottomY))
            ctx.stroke(path, with: .color(stroke), style: style)

            for p in [CGPoint(x: centerX, y: topY),
                      CGPoint(x: leftX, y: bottomY),
                      CGPoint(x: rightX, y: bottomY)] {
                let dot = Path(ellipseIn: CGRect(x: p.x - 3, y: p.y - 3, width: 6, height: 6))
                ctx.fill(dot, with: .color(Color.white.opacity(0.95)))
            }
        }
    }
}

private struct PlatformCircle: View {
    let systemImage: String
    let label: LocalizedStringKey

    var body: some View {
        VStack(spacing: 5) {
            ZStack {
                Circle()
                    .fill(Color.white.opacity(0.95))
                Image(systemName: systemImage)
                    .font(.system(size: 22, weight: .semibold))
                    .foregroundColor(HIGColors.light.tint)
            }
            .frame(width: 42, height: 42)
            .overlay(Circle().stroke(Color.white.opacity(0.5), lineWidth: 1.5))

            Text(label)
                .font(HIGType.caption1Emph)
                .foregroundColor(.white)
        }
    }
}

private struct WebRow: View {
    var body: some View {
        Link(destination: URL(string: AppConfig.resumeShareUrl)!) {
            HStack(spacing: 12) {
                ZStack {
                    Circle().fill(Color.white.opacity(0.95))
                    Image(systemName: "globe")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundColor(HIGColors.light.tint)
                }
                .frame(width: 36, height: 36)

                VStack(alignment: .leading, spacing: 1) {
                    Text("platform.web")
                        .font(HIGType.subheadlineEmph)
                        .foregroundColor(.white)
                    Text("platform.web_desc")
                        .font(HIGType.caption1)
                        .foregroundColor(.white.opacity(0.8))
                }
                Spacer()
                Image(systemName: "arrow.up.right.square")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.white.opacity(0.8))
            }
            .padding(EdgeInsets(top: 12, leading: 14, bottom: 12, trailing: 14))
            .background(
                Color.white.opacity(0.12),
                in: RoundedRectangle(cornerRadius: 14, style: .continuous)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .stroke(Color.white.opacity(0.22), lineWidth: 0.5)
            )
        }
        .buttonStyle(.plain)
    }
}

private struct Footer: View {
    @Environment(\.hig) private var hig

    var body: some View {
        Text("footer.note")
            .font(HIGType.caption1)
            .foregroundColor(hig.tertiaryLabel)
            .frame(maxWidth: .infinity)
            .padding(.top, 32)
            .padding(.bottom, 24)
    }
}
