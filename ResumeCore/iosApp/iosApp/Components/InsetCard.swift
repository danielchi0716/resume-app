import SwiftUI

struct InsetCard<Content: View>: View {
    var padding: EdgeInsets = EdgeInsets()
    @ViewBuilder var content: Content

    @Environment(\.hig) private var hig

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(padding)
        .background(
            hig.secondarySystemGroupedBackground,
            in: RoundedRectangle(cornerRadius: 16, style: .continuous)
        )
        .padding(.horizontal, 16)
    }
}

struct SectionHeaderText: View {
    let textView: Text
    var leading: AnyView? = nil

    @Environment(\.hig) private var hig

    var body: some View {
        HStack(spacing: 8) {
            if let leading {
                leading
            }
            textView
                .font(HIGType.footnote)
                .foregroundColor(hig.secondaryLabel)
                .textCase(.uppercase)
                .tracking(0.5)
        }
        .padding(.horizontal, 32)
        .padding(.vertical, 6)
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    init(_ key: LocalizedStringKey) {
        self.textView = Text(key)
    }

    init(verbatim text: String) {
        self.textView = Text(verbatim: text)
    }

    init<L: View>(_ key: LocalizedStringKey, @ViewBuilder leading: () -> L) {
        self.textView = Text(key)
        self.leading = AnyView(leading())
    }

    init<L: View>(verbatim text: String, @ViewBuilder leading: () -> L) {
        self.textView = Text(verbatim: text)
        self.leading = AnyView(leading())
    }
}
