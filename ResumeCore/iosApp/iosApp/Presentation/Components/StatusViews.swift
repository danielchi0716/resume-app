import SwiftUI

fileprivate struct LoadingPanel: View {
    @Environment(\.hig) private var hig
    var body: some View {
        VStack(spacing: 12) {
            ProgressView()
            Text("state.loading")
                .font(HIGType.footnote)
                .foregroundColor(hig.secondaryLabel)
        }
        .frame(maxWidth: .infinity, minHeight: 200)
    }
}

fileprivate struct ErrorPanel: View {
    let message: String
    let onRetry: () -> Void

    @Environment(\.hig) private var hig

    var body: some View {
        VStack(spacing: 10) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 28, weight: .semibold))
                .foregroundColor(hig.orange)
            Text("error.title")
                .font(HIGType.headline)
                .foregroundColor(hig.label)
            Text(message)
                .font(HIGType.footnote)
                .foregroundColor(hig.secondaryLabel)
                .multilineTextAlignment(.center)
            Button(action: onRetry) {
                Text("error.retry")
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

struct LoadStateView<T, Content: View>: View {
    let state: LoadState<T>
    let retry: () -> Void
    @ViewBuilder let content: (T) -> Content

    var body: some View {
        switch state {
        case .loading: LoadingPanel()
        case .error(let msg): ErrorPanel(message: msg, onRetry: retry)
        case .ready(let data): content(data)
        }
    }
}
