import SwiftUI

struct LoadingPanel: View {
    @Environment(\.hig) private var hig
    var body: some View {
        VStack(spacing: 12) {
            ProgressView()
            Text(L10n.loading)
                .font(HIGType.footnote)
                .foregroundColor(hig.secondaryLabel)
        }
        .frame(maxWidth: .infinity, minHeight: 200)
    }
}

struct ErrorPanel: View {
    let message: String
    let onRetry: () -> Void

    @Environment(\.hig) private var hig

    var body: some View {
        VStack(spacing: 10) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 28, weight: .semibold))
                .foregroundColor(hig.orange)
            Text(L10n.errorTitle)
                .font(HIGType.headline)
                .foregroundColor(hig.label)
            Text(message)
                .font(HIGType.footnote)
                .foregroundColor(hig.secondaryLabel)
                .multilineTextAlignment(.center)
            Button(action: onRetry) {
                Text(L10n.errorRetry)
                    .font(HIGType.bodyEmph)
                    .foregroundColor(.white)
                    .padding(.horizontal, 18)
                    .frame(height: 38)
                    .background(hig.tint, in: Capsule())
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, minHeight: 200)
    }
}

extension View {
    @ViewBuilder
    func resolveLoadState<T, Content: View>(
        _ state: ResumeStore.LoadState<T>,
        retry: @escaping () -> Void,
        @ViewBuilder content: (T) -> Content
    ) -> some View {
        switch state {
        case .loading: LoadingPanel()
        case .error(let msg): ErrorPanel(message: msg, onRetry: retry)
        case .ready(let data): content(data)
        }
    }
}
