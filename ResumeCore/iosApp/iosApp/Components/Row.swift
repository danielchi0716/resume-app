import SwiftUI

struct ListRow<Leading: View, Trailing: View>: View {
    var title: String
    var subtitle: String? = nil
    var detail: String? = nil
    var chevron: Bool = false
    var isLast: Bool = false
    @ViewBuilder var leading: () -> Leading
    @ViewBuilder var trailing: () -> Trailing
    var onTap: (() -> Void)? = nil

    @Environment(\.hig) private var hig

    var body: some View {
        HStack(spacing: 12) {
            leading()
            VStack(alignment: .leading, spacing: subtitle == nil ? 0 : 2) {
                Text(title)
                    .font(HIGType.body)
                    .foregroundColor(hig.label)
                if let subtitle {
                    Text(subtitle)
                        .font(HIGType.footnote)
                        .foregroundColor(hig.secondaryLabel)
                }
            }
            Spacer(minLength: 8)
            if let detail {
                Text(detail)
                    .font(HIGType.body)
                    .foregroundColor(hig.secondaryLabel)
            }
            trailing()
            if chevron {
                Image(systemName: "chevron.right")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(hig.tertiaryLabel)
                    .padding(.leading, 4)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .frame(minHeight: 56)
        .contentShape(Rectangle())
        .onTapGesture { onTap?() }
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

extension ListRow where Trailing == EmptyView {
    init(
        title: String,
        subtitle: String? = nil,
        detail: String? = nil,
        chevron: Bool = false,
        isLast: Bool = false,
        @ViewBuilder leading: @escaping () -> Leading,
        onTap: (() -> Void)? = nil
    ) {
        self.title = title
        self.subtitle = subtitle
        self.detail = detail
        self.chevron = chevron
        self.isLast = isLast
        self.leading = leading
        self.trailing = { EmptyView() }
        self.onTap = onTap
    }
}
