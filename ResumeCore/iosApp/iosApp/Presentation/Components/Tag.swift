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
                .font(.system(size: 12, weight: .medium))
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

