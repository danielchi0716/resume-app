import SwiftUI

struct TagView: View {
    let text: String
    var color: Color
    var leadingDot: Bool = false

    @Environment(\.higScheme) private var scheme

    var body: some View {
        HStack(spacing: 5) {
            if leadingDot {
                Circle()
                    .fill(color)
                    .frame(width: 6, height: 6)
            }
            Text(text)
                .font(HIGType.caption1Emph)
                .foregroundColor(color)
        }
        .padding(.horizontal, 10)
        .frame(height: 24)
        .background(
            color.opacity(scheme == .dark ? 0.22 : 0.14),
            in: RoundedRectangle(cornerRadius: 8, style: .continuous)
        )
    }
}

/// Gold-tinted award badge used for TOEIC etc.
struct GoldBadge: View {
    let text: String
    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: "rosette")
                .font(.system(size: 11, weight: .semibold))
            Text(text)
                .font(HIGType.caption1Emph)
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
