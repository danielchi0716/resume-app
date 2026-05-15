import SwiftUI

struct Glyph: View {
    let systemImage: String
    var size: CGFloat = 30
    var background: Color
    var foreground: Color = .white
    var radius: CGFloat = 7
    var iconWeight: Font.Weight = .semibold

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: radius, style: .continuous)
                .fill(background)
            Image(systemName: systemImage)
                .font(.system(size: size * 0.6, weight: iconWeight))
                .foregroundColor(foreground)
        }
        .frame(width: size, height: size)
    }
}

struct CircleGlyph: View {
    let systemImage: String
    var size: CGFloat = 56
    var background: Color
    var foreground: Color
    var iconSize: CGFloat = 26
    var iconWeight: Font.Weight = .medium

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(background)
            Image(systemName: systemImage)
                .font(.system(size: iconSize, weight: iconWeight))
                .foregroundColor(foreground)
        }
        .frame(width: size, height: size)
    }
}
